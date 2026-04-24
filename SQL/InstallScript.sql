-- TABLES
DROP TABLE IF EXISTS games_moves;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS player_tournament_info;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS tournaments;
-- VIEWS
DROP VIEW IF EXISTS leaderboard;
DROP VIEW IF EXISTS opponent_info;
DROP VIEW IF EXISTS player_analysis;
-- TRIGGER
DROP TRIGGER IF EXISTS update_player_points_after_round;

CREATE TABLE tournaments
(
    id                 INT NOT NULL AUTO_INCREMENT,
    name               VARCHAR(255),
    date               DATE,
    city               VARCHAR(255),
    base_consider_time INT,
    move_consider_time INT,
    status             ENUM ('PLANNED', 'ACTIVE', 'COMPLETED') DEFAULT 'PLANNED',
    PRIMARY KEY (id)
);

CREATE TABLE players
(
    id          INT NOT NULL AUTO_INCREMENT,
    firstname   VARCHAR(255),
    lastname    VARCHAR(255),
    fide_rating INT,
    fide_title  ENUM ('GM', 'IM', 'FM', 'CM', 'WGM', 'WIM', 'WFM', 'WCM', 'NONE') DEFAULT 'NONE',
    gender      CHAR,
    birthdate   DATE,
    PRIMARY KEY (id)
);

CREATE TABLE player_tournament_info
(
    id                INT NOT NULL AUTO_INCREMENT,
    tournament_id     INT NOT NULL,
    tournament_status ENUM ('APPLIED', 'PLAYING', 'FINISHED', 'DISQUALIFIED') DEFAULT 'APPLIED',
    player_id         INT NOT NULL,
    score             DEC(4, 1)                                                     DEFAULT 0.0,

    PRIMARY KEY (id),
    CONSTRAINT fk_tournament_registration
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_player_registration
        FOREIGN KEY (player_id)
            REFERENCES players (id)
            ON DELETE CASCADE
);

CREATE TABLE rounds
(
    id            INT NOT NULL AUTO_INCREMENT,
    tournament_id INT NOT NULL,
    round_number  INT,
    status        ENUM ('PLANNED', 'RUNNING', 'COMPLETED') DEFAULT 'PLANNED',
    begin         DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_tournament_round
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id)
            ON DELETE CASCADE
);

CREATE TABLE games
(
    id            INT NOT NULL AUTO_INCREMENT,
    result        INT,
    start         DATETIME,
    board_number  INT,
    round_id      INT,
    player_white  INT,
    player_black  INT,
    tournament_id INT,
    CONSTRAINT fk_game_round
        FOREIGN KEY (round_id)
            REFERENCES rounds (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_player_white_game
        FOREIGN KEY (player_white)
            REFERENCES players (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_player_black_game
        FOREIGN KEY (player_black)
            REFERENCES players (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_tournament_game
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id)
            ON DELETE CASCADE,
    PRIMARY KEY (id)

);

CREATE TABLE games_moves
(
    move_number INT,
    move        VARCHAR(55),
    games_id    INT,
    CONSTRAINT fk_games_games_moves
        FOREIGN KEY (games_id)
            REFERENCES games (id)
            ON DELETE CASCADE
);

CREATE VIEW leaderboard AS
SELECT p.id AS player_id, p.firstname, p.lastname, p.fide_rating, pti.score, sum(oti.score) AS buchholz
FROM players p
INNER JOIN player_tournament_info pti
    ON p.id = pti.player_id
INNER JOIN games g
    ON (g.player_white = p.id OR g.player_black = p.id)
INNER JOIN player_tournament_info oti
    ON oti.player_id =
        CASE
            WHEN g.player_white = p.id
            THEN g.player_black
            ELSE g.player_white
        END
WHERE pti.tournament_id = 1
AND oti.tournament_id = pti.tournament_id
GROUP BY p.id, p.firstname, p.lastname, p.fide_rating, pti.score
ORDER BY pti.score DESC, buchholz DESC, p.fide_rating DESC;

CREATE VIEW opponent_info AS
SELECT p.id AS player_id, p.firstname, p.lastname, p.fide_rating, MIN(op.fide_rating) AS min_opponent_rating, MAX(op.fide_rating) AS max_opponent_rating, AVG(op.fide_rating) AS avg_opponent_rating, COUNT(*) AS games_count
FROM players p
INNER JOIN games g
    ON g.player_white = p.id
    OR g.player_black = p.id
INNER JOIN players op
    ON op.id =
        CASE
            WHEN g.player_white = p.id
            THEN g.player_black
            ELSE g.player_white
        END
WHERE g.tournament_id = 1
GROUP BY p.id, p.firstname, p.lastname, p.fide_rating;

CREATE VIEW player_analysis AS
SELECT
    oi.player_id,
    oi.firstname,
    oi.lastname,
    (oi.avg_opponent_rating - oi.fide_rating) AS avg_rating_diff,
    (lb.score / NULLIF(oi.games_count, 0)) AS avg_score_per_game,
    lb.score,
    ROUND((lb.score * (oi.avg_opponent_rating / oi.fide_rating)) / oi.games_count * 10, 2) AS adjusted_score,
    ROUND((lb.score / NULLIF(oi.games_count, 0)) / (1 / (1 + POW(10, (oi.avg_opponent_rating - oi.fide_rating) / 400))) * 100, 2) AS performance
FROM opponent_info oi
INNER JOIN leaderboard lb
    ON oi.player_id = lb.player_id;

CREATE TRIGGER update_player_points_after_round
AFTER UPDATE
ON rounds
FOR EACH ROW
BEGIN
    -- Nur beim Wechsel auf COMPLETED
    IF NEW.status = 'COMPLETED' AND NOT OLD.status = 'COMPLETED' THEN

        -- Punkte für Weiß
        UPDATE player_tournament_info pti
        JOIN games g ON g.player_white = pti.player_id
        SET pti.score = pti.score +
            CASE
                WHEN g.result = 1 THEN 1
                WHEN g.result = 0 THEN 0.5
                ELSE 0
            END
        WHERE g.round_id = NEW.id
          AND pti.tournament_id = NEW.tournament_id;

        -- Punkte für Schwarz
        UPDATE player_tournament_info pti
        JOIN games g ON g.player_black = pti.player_id
        SET pti.score = pti.score +
            CASE
                WHEN g.result = -1 THEN 1
                WHEN g.result = 0 THEN 0.5
                ELSE 0
            END
        WHERE g.round_id = NEW.id
          AND pti.tournament_id = NEW.tournament_id;
    END IF;
END;