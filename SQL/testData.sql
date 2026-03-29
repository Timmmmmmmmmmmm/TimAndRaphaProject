ALTER TABLE players ADD COLUMN score DECIMAL(4,1) DEFAULT 0.0;

INSERT INTO tournaments (id, name, date, city, base_consider_time, move_consider_time, status)
VALUES
(1, 'Münster Open 2026', '2026-05-10', 'Münster', 90, 30, 'PLANNED');

INSERT INTO players (id, firstname, lastname, fide_rating, fide_title, gender, birthdate)
VALUES
(1, 'Magnus', 'Carlsen', 2830, 'GM', 'M', '1990-11-30'),
(2, 'Fabiano', 'Caruana', 2800, 'GM', 'M', '1992-07-30'),
(3, 'Hikaru', 'Nakamura', 2780, 'GM', 'M', '1987-12-09'),
(4, 'Ian', 'Nepomniachtchi', 2770, 'GM', 'M', '1990-07-14'),
(5, 'Ding', 'Liren', 2790, 'GM', 'M', '1992-10-24'),
(6, 'Alireza', 'Firouzja', 2760, 'GM', 'M', '2003-06-18');

INSERT INTO player_tournament_info (tournament_id, player_id, tournament_status, score)
VALUES
(1, 1, 'PLAYING', 0.0),
(1, 2, 'PLAYING', 0.0),
(1, 3, 'PLAYING', 0.0),
(1, 4, 'PLAYING', 0.0),
(1, 5, 'PLAYING', 0.0),
(1, 6, 'PLAYING', 0.0);

UPDATE tournaments
SET status = 'ACTIVE'
WHERE id = 1;

INSERT INTO rounds (id, tournament_id, round_number, status, begin)
SELECT 1, 1, 1, 'RUNNING', NOW()
WHERE NOT EXISTS (SELECT 1 FROM rounds WHERE id = 1);

INSERT INTO rounds (id, tournament_id, round_number, status, begin)
VALUES
(2, 1, 2, 'PLANNED', NULL),
(3, 1, 3, 'PLANNED', NULL);

INSERT INTO games (id, result, start, board_number, round_id, player_white, player_black, tournament_id)
VALUES
(1, 1, NOW(), 1, 1, 1, 2, 1),
(2, 0, NOW(), 2, 1, 3, 4, 1),
(3, -1, NOW(), 3, 1, 5, 6, 1),
(4, 0, NOW(), 1, 2, 1, 3, 1),
(5, 1, NOW(), 2, 2, 2, 5, 1),
(6, -1, NOW(), 3, 2, 4, 6, 1),
(7, 1, NOW(), 1, 3, 1, 4, 1),
(8, 0, NOW(), 2, 3, 2, 6, 1),
(9, -1, NOW(), 3, 3, 3, 5, 1);

INSERT INTO games_moves (move_number, move, games_id)
VALUES
(1, 'e4', 1),(1, 'c5', 1),(2, 'Nf3', 1),(2, 'd6', 1),(3, 'd4', 1),(3, 'cxd4', 1),(4, 'Nxd4', 1),(4, 'Nf6', 1),(5, 'Nc3', 1),(5, 'a6', 1),
(1, 'd4', 2),(1, 'Nf6', 2),(2, 'c4', 2),(2, 'e6', 2),(3, 'Nc3', 2),(3, 'Bb4', 2),(4, 'e3', 2),(4, 'O-O', 2),
(1, 'e4', 3),(1, 'e5', 3),(2, 'Nf3', 3),(2, 'Nc6', 3),(3, 'Bc4', 3),(3, 'Nf6', 3),(4, 'Ng5', 3),(4, 'd5', 3),
(1, 'e4', 4),(1, 'c5', 4),(2, 'Nf3', 4),(2, 'Nc6', 4),(3, 'Bb5', 4),(3, 'g6', 4),(4, 'O-O', 4),(4, 'Bg7', 4),
(1, 'e4', 5),(1, 'c5', 5),(2, 'Nf3', 5),(2, 'Nc6', 5),(3, 'd4', 5),(3, 'cxd4', 5),(4, 'Nxd4', 5),(4, 'Nf6', 5),
(1, 'd4', 6),(1, 'Nf6', 6),(2, 'c4', 6),(2, 'g6', 6),(3, 'Nc3', 6),(3, 'Bg7', 6),(4, 'e4', 6),(4, 'd6', 6),
(1, 'e4', 7),(1, 'c5', 7),(2, 'Nf3', 7),(2, 'd6', 7),(3, 'd4', 7),(3, 'cxd4', 7),(4, 'Nxd4', 7),(4, 'Nf6', 7),
(1, 'd4', 8),(1, 'Nf6', 8),(2, 'c4', 8),(2, 'e6', 8),(3, 'Nf3', 8),(3, 'd5', 8),(4, 'Nc3', 8),(4, 'Be7', 8),
(1, 'e4', 9),(1, 'e5', 9),(2, 'Nf3', 9),(2, 'Nc6', 9),(3, 'Bb5', 9),(3, 'a6', 9),(4, 'Ba4', 9),(4, 'Nf6', 9);

UPDATE rounds SET status = 'COMPLETED' WHERE id = 1;
UPDATE rounds SET status = 'COMPLETED' WHERE id = 2;
UPDATE rounds SET status = 'COMPLETED' WHERE id = 3;