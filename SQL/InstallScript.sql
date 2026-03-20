-- TABLES
DROP TABLE IF EXISTS games_moves;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS tournaments;
-- VIEWS
DROP VIEW IF EXISTS leaderboard;
DROP VIEW IF EXISTS round_overview;
-- TRIGGER
DROP TRIGGER IF EXISTS new_round;
DROP TRIGGER IF EXISTS update_player_points_after_round;

CREATE TABLE tournaments
(
    id                 INT NOT NULL AUTO_INCREMENT,
    name               VARCHAR(255),
    date               DATE,
    city               VARCHAR(255),
    base_consider_time INT,
    move_consider_time INT,
    status             VARCHAR(50),
    PRIMARY KEY (id)
);

CREATE TABLE players
(
    id            INT NOT NULL AUTO_INCREMENT,
    firstname     VARCHAR(255),
    lastname      VARCHAR(255),
    score         DEC(4, 1) DEFAULT 0.0,
    fide_rating   INT,
    fide_title    VARCHAR(55),
    gender        CHAR,
    birthdate     DATE,
    status        VARCHAR(55),
    tournament_id INT,
    CONSTRAINT fk_tournament_player
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id)
            ON DELETE CASCADE,
    PRIMARY KEY (id)
);

CREATE TABLE rounds
(
    id            INT NOT NULL AUTO_INCREMENT,
    tournament_id INT NOT NULL,
    round_number  INT,
    status        VARCHAR(55),
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
            REFERENCES players (id),
    CONSTRAINT fk_player_black_game
        FOREIGN KEY (player_black)
            REFERENCES players (id),
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
SELECT firstname, lastname, fide_rating, score
FROM players
ORDER BY score, fide_rating DESC;

CREATE VIEW round_overview AS
SELECT g.id,
       concat(pw.lastname, ", ", pw.firstname) AS player_white,
       concat(pb.lastname, ", ", pb.firstname) AS player_black
FROM games g
         INNER JOIN players pw ON g.player_white = pw.id
         INNER JOIN players pb ON g.player_black = pb.id
WHERE g.round_id = 1;

CREATE TRIGGER update_player_points_after_round
    AFTER UPDATE ON rounds
    FOR EACH ROW
BEGIN
    -- Nur beim Wechsel auf "completed"
    IF NEW.status = 'completed' AND OLD.status <> 'completed' THEN

        -- Punkte für Weiß
        UPDATE players p
        SET p.score = p.score + (
            SELECT g.result
            FROM games g
            WHERE g.player_white = p.id
              AND g.round_id = NEW.id
        )
        WHERE p.id IN (
            SELECT g.player_white
            FROM games g
            WHERE g.round_id = NEW.id
        );

        -- Punkte für Schwarz (invertiert)
        UPDATE players p
        SET p.score = p.score - (
            SELECT g.result
            FROM games g
            WHERE g.player_black = p.id
              AND g.round_id = NEW.id
        )
        WHERE p.id IN (
            SELECT g.player_black
            FROM games g
            WHERE g.round_id = NEW.id
        );

    END IF;
END;