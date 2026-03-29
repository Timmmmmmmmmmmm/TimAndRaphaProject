-- Spieler einfügen
INSERT INTO players (firstname, lastname, fide_rating, fide_title, gender, birthdate) VALUES
('Magnus', 'Carlsen', 2864, 'GM', 'M', '1990-11-30'),
('Fabiano', 'Caruana', 2820, 'GM', 'M', '1992-07-30'),
('Hikaru', 'Nakamura', 2785, 'GM', 'M', '1987-12-09'),
('Alireza', 'Firouzja', 2759, 'GM', 'M', '2003-06-18'),
('Ding', 'Liren', 2791, 'GM', 'M', '1992-10-24'),
('Ian', 'Nepomniachtchi', 2789, 'GM', 'M', '1990-07-14'),
('Anish', 'Giri', 2770, 'GM', 'M', '1994-06-28'),
('Levon', 'Aronian', 2778, 'GM', 'M', '1982-10-06'),
('Ju Wenjun', 'Ju', 2580, 'WGM', 'F', '1991-01-31'),
('Humpy', 'Koneru', 2623, 'GM', 'F', '1987-03-31');

-- Turniere einfügen
INSERT INTO tournaments (name, date, city, base_consider_time, move_consider_time, status) VALUES
('Spring Open', '2026-04-15', 'Berlin', 90, 30, 'PLANNED'),
('Summer Classic', '2026-06-20', 'Paris', 120, 30, 'ACTIVE'),
('Autumn Masters', '2026-09-10', 'London', 60, 20, 'COMPLETED');

-- Spieler für Turniere registrieren
INSERT INTO player_tournament_info (tournament_id, player_id, tournament_status, score) VALUES
(1, 1, 'APPLIED', 0.0),
(1, 2, 'APPLIED', 0.0),
(1, 3, 'APPLIED', 0.0),
(1, 4, 'APPLIED', 0.0),
(2, 1, 'PLAYING', 1.5),
(2, 2, 'PLAYING', 1.0),
(2, 3, 'PLAYING', 0.5),
(2, 5, 'PLAYING', 1.0),
(2, 6, 'PLAYING', 0.0),
(3, 1, 'FINISHED_GAMES', 2.0),
(3, 4, 'FINISHED_GAMES', 2.5),
(3, 5, 'FINISHED_GAMES', 1.5),
(3, 7, 'FINISHED_GAMES', 1.0);

-- Runden für Turniere
INSERT INTO rounds (tournament_id, round_number, status, begin) VALUES
(2, 1, 'COMPLETED', '2026-06-20 10:00:00'),
(2, 2, 'RUNNING', '2026-06-20 14:00:00'),
(3, 1, 'COMPLETED', '2026-09-10 09:00:00'),
(3, 2, 'COMPLETED', '2026-09-10 13:00:00');

-- Spiele einfügen
INSERT INTO games (result, start, board_number, round_id, player_white, player_black, tournament_id) VALUES
(1, '2026-06-20 10:00:00', 1, 1, 1, 2, 2),
(0, '2026-06-20 10:00:00', 2, 1, 3, 5, 2),
(NULL, '2026-06-20 14:00:00', 1, 2, 1, 3, 2),
(NULL, '2026-06-20 14:00:00', 2, 2, 2, 5, 2),
(1, '2026-09-10 09:00:00', 1, 3, 1, 4, 3),
(0, '2026-09-10 09:00:00', 2, 3, 5, 7, 3);

-- Züge einfügen
INSERT INTO games_moves (move_number, move, games_id) VALUES
(1, 'e4', 1),
(2, 'e5', 1),
(3, 'Nf3', 1),
(4, 'Nc6', 1),
(1, 'd4', 2),
(2, 'd5', 2),
(3, 'c4', 2),
(4, 'e6', 2),
(1, 'c4', 3),
(2, 'Nf6', 3),
(1, 'e4', 4),
(2, 'c5', 4),
(1, 'd4', 5),
(2, 'Nf6', 5),
(3, 'c4', 5),
(4, 'g6', 5),
(1, 'Nf3', 6),
(2, 'd5', 6);
