-- Die Datenbank sollte beim Setup neu und leer sein,
-- damit die Runden und Match und Team Ids verwendet werden können

-- Status
INSERT INTO status
    (id, description, primary_color, secondary_color, pulsing)
VALUES
    ('UNKNOWN', '???', null, null, false),
    ('PLANNED', 'anstehend', '#1b58ff', null, false),
    ('ONGOING', 'laufend', '#00ce22', '#00ce22', true),
    ('PAUSED', 'pausiert', '#ff9900', '#ff9900', true),
    ('ENDED', 'beendet', '#878787', null, false),
    ('CANCELED', 'abgebrochen', '#ff0000', null, false);


-- Stage Modes
INSERT INTO stage_modes
    (id, description)
VALUES
    -- ('QUALIFICATION', 'Standard Ringzahl-Qualifikation'),
    ('SINGLE_ELIMINATION', 'Einfaches K.O. System'),
    ('DOUBLE_ELIMINATION', 'Doppel K.O. System (mit Winner- und Loser-Bracket)'),
    ('ROUND_ROBIN', 'Jeder gegen Jeden');

-- Ranking Methods (Wie wird die Rangliste einer Stage berechnet?)
INSERT INTO ranking_methods
    (id, description)
VALUES
    -- ('TOTAL_SCORE', 'Ranking nach Gesamtsumme der Ringe'),
    -- ('AVERAGE_ARROW', 'Ranking nach Durchschnittswert pro Pfeil'),
    ('POINTS', 'Ranking nach gewonnenen Matchpunkte'),
    ('BRACKET_POS', 'Ranking nach Fortschritt im Turnierbaum');

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
