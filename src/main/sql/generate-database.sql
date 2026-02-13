--Tables
CREATE TABLE status
(
    id              VARCHAR(50) PRIMARY KEY,
    description     TEXT,
    primary_color   CHAR(7) CHECK (primary_color ~ '^#[0-9A-Fa-f]{6}$'),
    secondary_color CHAR(7) CHECK (secondary_color ~ '^#[0-9A-Fa-f]{6}$'),
    pulsing         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE stage_modes
(
    id          VARCHAR(50) PRIMARY KEY, -- z.B. 'QUALIFICATION', 'SINGLE_ELIMINATION', 'DOUBLE_ELIMINATION',
    description TEXT
);

CREATE TABLE ranking_methods
(
    id          VARCHAR(50) PRIMARY KEY, -- z.B. 'TOTAL_SCORE', 'BRACKET_POS'
    description TEXT
);

CREATE TABLE final_ranking_strategies
(
    id          VARCHAR(50) PRIMARY KEY, -- z.B. 'LAST_STAGE_WINNER', 'CUMULATIVE_SCORE'
    description TEXT
);

CREATE TABLE document_types
(
    id          VARCHAR(50) PRIMARY KEY, -- z.B. 'SCORE_SHEET', 'TOURNAMENT_STANDINGS'
    description TEXT
);

CREATE TABLE stage_templates
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(255) NOT NULL UNIQUE,
    stage_mode_id     VARCHAR(50) REFERENCES stage_modes(id),
    ranking_method_id VARCHAR(50) REFERENCES ranking_methods(id),
    stage_config      JSON
);

CREATE TABLE scores
(
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code  VARCHAR(16) NOT NULL,
    value INTEGER NOT NULL,
    color CHAR(7) CHECK (color ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE TABLE target_faces
(
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name  VARCHAR(255) NOT NULL,
    size  VARCHAR(16),
    image BYTEA,
    UNIQUE (name, size)
);

CREATE TABLE target_face_scores
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_face_id UUID NOT NULL REFERENCES target_faces (id),
    score_id       UUID NOT NULL REFERENCES scores (id),
    UNIQUE (target_face_id, score_id)
);

CREATE TABLE tournaments
(
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                      VARCHAR(255) NOT NULL,
    description               TEXT,
    location                  VARCHAR(255),
    date                      DATE,
    max_slots                 SMALLINT,
    registration_deadline     DATE,
    allow_registration        BOOLEAN NOT NULL DEFAULT FALSE,
    primary_color             CHAR(7) CHECK (primary_color ~ '^#[0-9A-Fa-f]{6}$'),
    secondary_color           CHAR(7) CHECK (secondary_color ~ '^#[0-9A-Fa-f]{6}$'),
    final_ranking_strategy_id VARCHAR(50) REFERENCES final_ranking_strategies(id),
    generated                 BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE targets
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tournament_id UUID NOT NULL REFERENCES tournaments (id),
    code          VARCHAR(16) NOT NULL,
    UNIQUE (tournament_id, code)
);

CREATE TABLE documents
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tournament_id    UUID         NOT NULL REFERENCES tournaments (id),
    document_type_id VARCHAR(50)  NOT NULL REFERENCES document_types (id),
    file_name        VARCHAR(255) NOT NULL,
    mimetype         VARCHAR(255) NOT NULL,
    content          BYTEA        NOT NULL
);

CREATE TABLE teams
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tournament_id     UUID NOT NULL REFERENCES tournaments (id),
    name              VARCHAR(255),
    contact_email     VARCHAR(255),
    team_member_count SMALLINT,
    UNIQUE (tournament_id, name)
);

CREATE TABLE team_members
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id    UUID NOT NULL REFERENCES teams (id),
    first_name VARCHAR(255),
    last_name  VARCHAR(255)
);

CREATE TABLE tournament_registrations
(
    team_id           UUID PRIMARY KEY REFERENCES teams (id),
    registration_date TIMESTAMP DEFAULT now(),
    payment_date      TIMESTAMP,
    note              TEXT
);

CREATE TABLE stages
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tournament_id     UUID NOT NULL REFERENCES tournaments (id),
    status_id         VARCHAR(50) NOT NULL REFERENCES status (id),

    name              VARCHAR(255),
    stage_mode_id     VARCHAR(50) REFERENCES stage_modes(id),
    ranking_method_id VARCHAR(50) REFERENCES ranking_methods(id),
    stage_config      JSON,

    stage_index       SMALLINT,
    parent_stage_id   UUID REFERENCES stages (id),
    UNIQUE (tournament_id, stage_index)
);

CREATE TABLE rounds
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stage_id    UUID NOT NULL REFERENCES stages (id),
    status_id   VARCHAR(50) NOT NULL REFERENCES status (id),
    name        VARCHAR(255),
    round_index SMALLINT,
    updated_at  TIMESTAMP,
    UNIQUE (stage_id, round_index)
);

CREATE TABLE matches
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    round_id       UUID NOT NULL REFERENCES rounds (id),
    status_id      VARCHAR(50) NOT NULL REFERENCES status (id),
    name           VARCHAR(255),
    shoot_off      BOOLEAN NOT NULL DEFAULT FALSE,

    team1_id       UUID REFERENCES teams (id),
    team2_id       UUID REFERENCES teams (id),
    winner_team_id UUID REFERENCES teams (id),

    target1_id     UUID REFERENCES targets (id),
    target2_id     UUID REFERENCES targets (id),

    updated_at     TIMESTAMP,
    CONSTRAINT check_teams_diff CHECK (team1_id != team2_id),
    CONSTRAINT check_winner_valid CHECK (winner_team_id IS NULL OR winner_team_id IN (team1_id, team2_id))
);

CREATE TABLE match_transitions
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_match_id     UUID     NOT NULL REFERENCES matches (id),
    source_match_winner BOOLEAN  NOT NULL,

    target_match_id     UUID     NOT NULL REFERENCES matches (id),
    target_match_slot   SMALLINT NOT NULL,
    UNIQUE (target_match_id, target_match_slot)
);

CREATE TABLE match_team_members
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id       UUID NOT NULL REFERENCES matches (id),
    team_member_id UUID NOT NULL REFERENCES team_members (id),
    rotation_index SMALLINT,
    UNIQUE (match_id, team_member_id)
);

CREATE TABLE sets
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id     UUID     NOT NULL REFERENCES matches (id),
    set_index    SMALLINT NOT NULL,
    total_team1  SMALLINT,
    total_team2  SMALLINT,
    points_team1 SMALLINT,
    points_team2 SMALLINT,
    created_at   TIMESTAMP        DEFAULT now(),
    updated_at   TIMESTAMP,
    UNIQUE (match_id, set_index)
);

CREATE TABLE arrows
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    score_id       UUID NOT NULL REFERENCES scores (id),
    team_member_id UUID         NOT NULL REFERENCES team_members (id),
    set_id         UUID REFERENCES sets (id),
    arrow_index    SMALLINT,
    UNIQUE (set_id, team_member_id, arrow_index)
);

-- Trigger Function
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE TRIGGER trg_rounds_updated_at
    BEFORE UPDATE
    ON rounds
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_matches_updated_at
    BEFORE UPDATE
    ON matches
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_sets_updated_at
    BEFORE UPDATE
    ON sets
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();