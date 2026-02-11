--Tables
CREATE TABLE status
(
    id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(255) NOT NULL UNIQUE,
    description     TEXT,
    primary_color   CHAR(7) CHECK (primary_color ~ '^#[0-9A-Fa-f]{6}$'),
    secondary_color CHAR(7) CHECK (secondary_color ~ '^#[0-9A-Fa-f]{6}$'),
    pulsing         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE document_types
(
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE stage_templates
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    data JSON
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
    name  VARCHAR(255) NOT NULL UNIQUE,
    size  VARCHAR(16),
    image BYTEA
);

CREATE TABLE target_face_scores
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_face_id UUID NOT NULL REFERENCES target_faces (id),
    score_id       UUID NOT NULL REFERENCES scores (id),
    UNIQUE (target_face_id, score_id)
);

CREATE TABLE stage_options
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_face_id    UUID REFERENCES target_faces (id),
    distance          INTEGER,
    shooting_time     INTEGER,
    arrows_per_member INTEGER,
    members_per_match INTEGER
);

CREATE TABLE tournaments
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                  VARCHAR(255) NOT NULL,
    description           TEXT,
    location              VARCHAR(255),
    date                  DATE,
    max_slots             BIGINT,
    registration_deadline DATE,
    allow_registration    BOOLEAN NOT NULL DEFAULT FALSE,
    primary_color         CHAR(7) CHECK (primary_color ~ '^#[0-9A-Fa-f]{6}$'),
    secondary_color       CHAR(7) CHECK (secondary_color ~ '^#[0-9A-Fa-f]{6}$'),
    generated             BOOLEAN NOT NULL DEFAULT FALSE
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
    document_type_id INTEGER      NOT NULL REFERENCES document_types (id),
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
    team_member_count BIGINT,
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
    status_id         INTEGER NOT NULL REFERENCES status (id),
    stage_option_id   UUID NOT NULL REFERENCES stage_options (id),
    name              VARCHAR(255),
    stage_index       SMALLINT,
    UNIQUE (tournament_id, stage_index)
);

CREATE TABLE rounds
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stage_id    UUID NOT NULL REFERENCES stages (id),
    status_id   INTEGER NOT NULL REFERENCES status (id),
    name        VARCHAR(255),
    round_index SMALLINT,
    updated_at  TIMESTAMP,
    UNIQUE (stage_id, round_index)
);

CREATE TABLE matches
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    round_id       UUID NOT NULL REFERENCES rounds (id),
    status_id      INTEGER NOT NULL REFERENCES status (id),
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
    total_team1  BIGINT,
    total_team2  BIGINT,
    points_team1 BIGINT,
    points_team2 BIGINT,
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