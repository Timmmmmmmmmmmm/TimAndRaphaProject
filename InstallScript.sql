DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS games_moves;

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

INSERT INTO tournaments(id, name, date, city, base_consider_time, move_consider_time, status)
VALUES (1, 'Adrians Turnier', CURRENT_DATE, 'Münster', 1800, 30, 'planned');

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

INSERT INTO players(id, firstname, lastname, fide_rating, fide_title, gender, birthdate, status)
VALUES (1, 'Magnus', 'Carlsen', 2840, 'gm', 'm', '1990-11-30', 'disqualified'),
       (2, 'Hikaru', 'Nakamura', 2810, 'gm', 'm', '1987-12-09', 'disqualified'),
       (3, 'Tim', 'Kaiser', 1423, 'none', 'm', '2003-05-13', 'winner');


CREATE TABLE rounds
(
    id           INT,
    round_number INT,
    status       VARCHAR(55),
    begin        DATE TIME

);

INSERT INTO rounds(id, round_number, status, begin)
VALUES (1, 1, 'pending', '2026-05-13 14:00');


CREATE TABLE games
(
    id           INT NOT NULL,
    result       INT,
    start        DATE TIME,
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
            REFERENCES players (id)

);

INSERT INTO games (id, result, start, round_id, player_white, player_black)
VALUES (1, 1, '2026-05-13 14:00', 1, 3, 1)

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
