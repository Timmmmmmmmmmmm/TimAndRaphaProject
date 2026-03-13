SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS games_moves;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS tournaments;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE tournaments
(
    id                 INT NOT NULL,
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
    id          INT NOT NULL,
    firstname   VARCHAR(255),
    lastname    VARCHAR(255),
    fide_rating INT,
    fide_title  VARCHAR(55),
    gender      CHAR,
    birthdate   DATE,
    status      VARCHAR(55),
    PRIMARY KEY (id)
);

CREATE TABLE rounds
(
    id           INT NOT NULL,
    round_number INT,
    status       VARCHAR(55),
    begin        DATETIME,
    PRIMARY KEY (id)

);

CREATE TABLE games
(
    id           INT NOT NULL,
    result       INT,
    start        DATETIME,
    board_number INT,
    round_id     INT,
    CONSTRAINT fk_round
        FOREIGN KEY (round_id)
            REFERENCES rounds (id)
            ON DELETE CASCADE,
    player_white INT,
    CONSTRAINT fk_player_white
        FOREIGN KEY (player_white)
            REFERENCES players (id),
    player_black INT,
    CONSTRAINT fk_player_black
        FOREIGN KEY (player_black)
            REFERENCES players (id),
    PRIMARY KEY (id)

);

CREATE TABLE games_moves
(
    move_number INT,
    move        VARCHAR(55),
    games_id    INT,
    CONSTRAINT fk_games
        FOREIGN KEY (games_id)
            REFERENCES games (id)
            ON DELETE CASCADE
);