-- Die Datenbank sollte beim Setup neu und leer sein,
-- damit die Runden und Match und Team Ids verwendet werden können

-- Status
INSERT INTO status
    (id, description, primary_color, secondary_color, pulsing)
VALUES
    ('UNKNOWN', '???', null, null, false),
    ('UPCOMING', 'anstehend', '#1b58ff', null, false),
    ('ONGOING', 'laufend', '#00ce22', '#00ce22', true),
    ('PAUSED', 'pausiert', '#ff9900', '#ff9900', true),
    ('ENDED', 'beendet', '#878787', null, false),
    ('CANCELED', 'abgebrochen', '#ff0000', null, false);


-- Stage Modes
INSERT INTO stage_modes
    (id, description)
VALUES
    ('QUALIFICATION', 'Standard Ringzahl-Qualifikation'),
    ('SINGLE_ELIMINATION', 'K.O. System ohne Trostrunde'),
    ('DOUBLE_ELIMINATION', 'Doppel K.O. (mit Winner- und Loser-Bracket)'),
    ('ROUND_ROBIN', 'Jeder gegen Jeden (Match-System)'),
    ('FINAL_SHOOT_OFF', 'Einzelnes finales Stechen');

-- Ranking Methods (Wie wird die Rangliste einer Stage berechnet?)
INSERT INTO ranking_methods
    (id, description)
VALUES
    ('TOTAL_SCORE', 'Ranking nach Gesamtsumme der Ringe'),
    ('MATCH_WINS', 'Ranking nach gewonnenen Matches (Satzpunkte/Matchpunkte)'),
    ('BRACKET_POS', 'Ranking nach Fortschritt im Turnierbaum'),
    ('AVERAGE_ARROW', 'Ranking nach Durchschnittswert pro Pfeil');

-- finale Ranking Strategie (Wie wird die Rangliste des Turniers berechnet?)
INSERT INTO final_ranking_strategies
    (id, description)
VALUES
    ('CUMULATIVE_SCORE', 'Ranking nach kumulierte Stage-Ergebnissen'),
    ('LAST_STAGE_WINNER', 'Ranking nach letzter Stage');

-- Document Types
INSERT INTO document_types
    (id)
VALUES
    ('ANNOUNCEMENT'),
    ('LIST_OF_PARTICIPANTS'),
    ('SCORE_SHEET'),
    ('TEAMMEMBER_LINE_UP_SHEET'),
    ('STAGE_STANDINGS'),
    ('TOURNAMENT_STANDINGS');

-- Ringwerte
INSERT INTO scores
    (code, value, color)
VALUES
    ('X', 10, '#f1c40f'),
    ('10', 10, '#f1c40f'),
    ('9', 9, '#f1c40f'),
    ('8', 8, '#ff3333'),
    ('7', 7, '#ff3333'),
    ('6', 6, '#4059f7'),
    ('5', 5, '#4059f7'),
    ('4', 4, '#0f0f0f'),
    ('3', 3, '#0f0f0f'),
    ('2', 2, '#ecf0f1'),
    ('1', 1, '#ecf0f1'),
    ('M', 0, '#0f0f0f');

-- Target Faces
INSERT INTO target_faces
    (name, size)
VALUES
    ('3er Spot', '40 cm'),
    ('3er Vegas Spot', '80 cm');

-- Verknüpfung: Ringwerte -> Auflage
INSERT INTO target_face_scores (target_face_id, score_id)
SELECT tf.id, s.id FROM target_faces tf, scores s
WHERE tf.name = '3er Spot' AND tf.size = '40 cm' AND s.code IN ('10', '9', '8', '7', '6');

INSERT INTO target_face_scores (target_face_id, score_id)
SELECT tf.id, s.id FROM target_faces tf, scores s
WHERE tf.name = '3er Vegas Spot' AND tf.size = '80 cm' AND s.code IN ('X', '10', '9', '8', '7', '6');


-- Templates
INSERT INTO stage_templates
    (name, stage_mode_id, ranking_method_id, stage_config)
VALUES
    (
        'Wedemark-Team-Open Qualifikationsrunde',
        'ROUND_ROBIN',
        'MATCH_WINS',
        jsonb_build_object(
                'distance_meters', 30,
                'shooting_time_seconds', 120,
                'target_face_id', (SELECT id FROM target_faces WHERE name = '3er Vegas Spot' AND size = '80 cm'),
                'max_sets_per_match', 5,
                'points_to_win_match', 6,
                'draw_mode', 'NONE',
                'match_points_win', 2,
                'match_points_draw', 1,
                'match_points_loss', 0,
                'teammembers_per_match', 3,
                'arrows_per_teammember', 2
        )
    );

INSERT INTO stage_templates
    (name, stage_mode_id, ranking_method_id, stage_config)
VALUES
    (
        'Wedemark-Team-Open Doppel-KO-Finale',
        'DOUBLE_ELIMINATION',
        'BRACKET_POS',
        jsonb_build_object(
                'distance_meters', 30,
                'shooting_time_seconds', 120,
                'target_face_id', (SELECT id FROM target_faces WHERE name = '3er Vegas Spot' AND size = '80 cm'),
                'max_sets_per_match', 5,
                'points_to_win_match', 6,
                'draw_mode', 'SHOOT_OFF',
                'match_points_win', 2,
                'match_points_draw', 1,
                'match_points_loss', 0,
                'teammembers_per_match', 3,
                'arrows_per_teammember', 2
        )
    );
