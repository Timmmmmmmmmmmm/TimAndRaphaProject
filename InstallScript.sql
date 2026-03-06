DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS players;

CREATE TABLE tournaments(
    id INT NOT NULL,
    name VARCHAR(255),
    date DATE,
    city VARCHAR(255),
    base_consider_time INT,
    move_consider_time INT,
    status VARCHAR(50),
    PRIMARY KEY (id));

INSERT INTO tournaments(id, name, date, city, base_consider_time, move_consider_time, status)
    VALUES(1, 'Adrians Turnier', CURRENT_DATE, 'Münster', 1800, 30, 'planned');

CREATE TABLE players(
    id INT NOT NULL,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    fide_rating INT,
    fide_title VARCHAR(50),
    gender CHAR,
    birthdate DATE,
    status VARCHAR(50),
    PRIMARY KEY (id));

INSERT INTO players(id, firstname, lastname, fide_rating, fide_title, gender, birthdate, status)
    VALUES(1, 'Magnus', 'Carlsen', 2840, 'gm', 'm', '1990-11-30', 'disqualified');

INSERT INTO players(id, firstname, lastname, fide_rating, fide_title, gender, birthdate, status)
    VALUES(2, 'Hikaru', 'Nakamura', 2810, 'gm', 'm', '1987-12-09', 'disqualified');

INSERT INTO players(id, firstname, lastname, fide_rating, fide_title, gender, birthdate, status)
    VALUES(3, 'Tim', 'Kaiser', 1423, 'none', 'm', '2003-05-13', 'epic win');
