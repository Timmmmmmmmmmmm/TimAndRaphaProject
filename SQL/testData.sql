-- ============================================================
-- TEST DATA für Schachturnier-Datenbank
-- ============================================================

-- TOURNAMENTS
INSERT INTO tournaments (id, name, date, city, base_consider_time, move_consider_time, status) VALUES
                                                                                                   (1, 'Münster Open 2025',        '2025-03-15', 'Münster',  5400, 30, 'completed'),
                                                                                                   (2, 'Westfalen Blitz Cup',      '2025-06-01', 'Dortmund', 1800, 10, 'completed'),
                                                                                                   (3, 'Herbst-Turnier Köln',      '2025-09-20', 'Köln',     5400, 30, 'active'),
                                                                                                   (4, 'Weihnachts-Simultanturnier','2025-12-20','Düsseldorf',900,  0,  'planned');

-- PLAYERS
INSERT INTO players (id, firstname, lastname, fide_rating, fide_title, gender, birthdate, status) VALUES
                                                                                                      (1,  'Magnus',    'Carlsen',    2831, 'GM',  'M', '1990-11-30', 'active'),
                                                                                                      (2,  'Judit',     'Polgar',     2675, 'GM',  'F', '1976-07-23', 'retired'),
                                                                                                      (3,  'Fabiano',   'Caruana',    2805, 'GM',  'M', '1992-07-30', 'active'),
                                                                                                      (4,  'Hou',       'Yifan',      2650, 'GM',  'F', '1994-02-27', 'active'),
                                                                                                      (5,  'Jan',       'Nepomnjaschtschi', 2780, 'GM', 'M', '1990-07-14', 'active'),
                                                                                                      (6,  'Alireza',   'Firouzja',   2760, 'GM',  'M', '2003-06-18', 'active'),
                                                                                                      (7,  'Anna',      'Muzychuk',   2550, 'IM',  'F', '1990-02-28', 'active'),
                                                                                                      (8,  'Viswanathan','Anand',     2751, 'GM',  'M', '1969-12-11', 'active'),
                                                                                                      (9,  'Sergey',    'Karjakin',   2747, 'GM',  'M', '1990-01-12', 'active'),
                                                                                                      (10, 'Lei',       'Tingjie',    2530, 'IM',  'F', '1997-01-12', 'active');

-- ROUNDS (gehören zu Turnier 1 – Münster Open 2025)
INSERT INTO rounds (id, round_number, status, begin) VALUES
                                                         (1, 1, 'completed', '2025-03-15 10:00:00'),
                                                         (2, 2, 'completed', '2025-03-15 15:00:00'),
                                                         (3, 3, 'completed', '2025-03-16 10:00:00'),
                                                         (4, 4, 'completed', '2025-03-16 15:00:00'),
                                                         (5, 5, 'completed', '2025-03-17 10:00:00');

-- GAMES
-- result: 1 = Weiß gewinnt, 2 = Schwarz gewinnt, 3 = Remis, 0 = laufend/kein Ergebnis
INSERT INTO games (id, result, start, board_number, round_id, player_white, player_black) VALUES
-- Runde 1
(1,  1, '2025-03-15 10:05:00', 1, 1, 1, 3),  -- Carlsen (W) vs Caruana (B) → Weiß gewinnt
(2,  3, '2025-03-15 10:05:00', 2, 1, 5, 6),  -- Nepo vs Firouzja → Remis
(3,  2, '2025-03-15 10:05:00', 3, 1, 8, 9),  -- Anand vs Karjakin → Schwarz gewinnt
(4,  1, '2025-03-15 10:05:00', 4, 1, 4, 7),  -- Hou Yifan vs Muzychuk → Weiß gewinnt
(5,  1, '2025-03-15 10:05:00', 5, 1, 2, 10), -- Polgar vs Lei → Weiß gewinnt
-- Runde 2
(6,  3, '2025-03-15 15:05:00', 1, 2, 3, 5),  -- Caruana vs Nepo → Remis
(7,  1, '2025-03-15 15:05:00', 2, 2, 6, 4),  -- Firouzja vs Hou Yifan → Weiß gewinnt
(8,  2, '2025-03-15 15:05:00', 3, 2, 9, 1),  -- Karjakin vs Carlsen → Schwarz gewinnt
(9,  1, '2025-03-15 15:05:00', 4, 2, 10, 8), -- Lei vs Anand → Weiß gewinnt
(10, 3, '2025-03-15 15:05:00', 5, 2, 7, 2),  -- Muzychuk vs Polgar → Remis
-- Runde 3
(11, 1, '2025-03-16 10:05:00', 1, 3, 1, 6),  -- Carlsen vs Firouzja → Weiß gewinnt
(12, 2, '2025-03-16 10:05:00', 2, 3, 5, 9),  -- Nepo vs Karjakin → Schwarz gewinnt
(13, 3, '2025-03-16 10:05:00', 3, 3, 4, 10), -- Hou Yifan vs Lei → Remis
(14, 1, '2025-03-16 10:05:00', 4, 3, 3, 8),  -- Caruana vs Anand → Weiß gewinnt
(15, 2, '2025-03-16 10:05:00', 5, 3, 2, 7),  -- Polgar vs Muzychuk → Schwarz gewinnt
-- Runde 4
(16, 3, '2025-03-16 15:05:00', 1, 4, 6, 1),  -- Firouzja vs Carlsen → Remis
(17, 1, '2025-03-16 15:05:00', 2, 4, 9, 3),  -- Karjakin vs Caruana → Weiß gewinnt
(18, 2, '2025-03-16 15:05:00', 3, 4, 8, 4),  -- Anand vs Hou Yifan → Schwarz gewinnt
(19, 1, '2025-03-16 15:05:00', 4, 4, 10, 2), -- Lei vs Polgar → Weiß gewinnt
(20, 3, '2025-03-16 15:05:00', 5, 4, 7, 5),  -- Muzychuk vs Nepo → Remis
-- Runde 5
(21, 1, '2025-03-17 10:05:00', 1, 5, 1, 9),  -- Carlsen vs Karjakin → Weiß gewinnt
(22, 2, '2025-03-17 10:05:00', 2, 5, 3, 6),  -- Caruana vs Firouzja → Schwarz gewinnt
(23, 1, '2025-03-17 10:05:00', 3, 5, 4, 5),  -- Hou Yifan vs Nepo → Weiß gewinnt
(24, 3, '2025-03-17 10:05:00', 4, 5, 8, 10), -- Anand vs Lei → Remis
(25, 1, '2025-03-17 10:05:00', 5, 5, 2, 7);  -- Polgar vs Muzychuk → Weiß gewinnt

-- GAMES_MOVES (Beispielzüge in algebraischer Notation für ausgewählte Partien)
INSERT INTO games_moves (move_number, move, games_id) VALUES
-- Partie 1: Carlsen vs Caruana (Sizilianische Verteidigung)
(1,  'e4',    1), (1,  'c5',   1),
(2,  'Nf3',   1), (2,  'Nc6',  1),
(3,  'd4',    1), (3,  'cxd4', 1),
(4,  'Nxd4',  1), (4,  'Nf6',  1),
(5,  'Nc3',   1), (5,  'e5',   1),
(6,  'Ndb5',  1), (6,  'd6',   1),
(7,  'Bg5',   1), (7,  'a6',   1),
(8,  'Na3',   1), (8,  'b5',   1),
(9,  'Bxf6',  1), (9,  'gxf6', 1),
(10, 'Nd5',   1), (10, 'f5',   1),
-- Partie 11: Carlsen vs Firouzja (Königsgambit)
(1,  'e4',    11), (1,  'e5',   11),
(2,  'f4',    11), (2,  'exf4', 11),
(3,  'Nf3',   11), (3,  'g5',   11),
(4,  'Bc4',   11), (4,  'g4',   11),
(5,  'O-O',   11), (5,  'gxf3', 11),
(6,  'Qxf3',  11), (6,  'Qf6',  11),
(7,  'd3',    11), (7,  'Nc6',  11),
(8,  'Nc3',   11), (8,  'Bc5+', 11),
(9,  'Kh1',   11), (9,  'd6',   11),
(10, 'Nd5',   11), (10, 'Qd8',  11),
-- Partie 21: Carlsen vs Karjakin (Ruy Lopez)
(1,  'e4',    21), (1,  'e5',   21),
(2,  'Nf3',   21), (2,  'Nc6',  21),
(3,  'Bb5',   21), (3,  'a6',   21),
(4,  'Ba4',   21), (4,  'Nf6',  21),
(5,  'O-O',   21), (5,  'Be7',  21),
(6,  'Re1',   21), (6,  'b5',   21),
(7,  'Bb3',   21), (7,  'd6',   21),
(8,  'c3',    21), (8,  'O-O',  21),
(9,  'h3',    21), (9,  'Nb8',  21),
(10, 'd4',    21), (10, 'Nbd7', 21);
