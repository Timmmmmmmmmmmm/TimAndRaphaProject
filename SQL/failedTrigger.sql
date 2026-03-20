
CREATE TRIGGER new_round
    AFTER INSERT
    ON rounds
    FOR EACH ROW
BEGIN
    DECLARE tournament_player_count INT;
    DECLARE tournament_round_count INT;
    DECLARE max_rounds INT;
    DECLARE current_tournament_id INT;
    DECLARE done INT DEFAULT 0;
    DECLARE current_player_id INT;
    DECLARE opponent_player_id INT;
    DECLARE current_score DECIMAL(4,1);
    DECLARE game_counter INT DEFAULT 0;

    DECLARE player_cursor CURSOR FOR
    SELECT p.id, p.score
    FROM players p
    WHERE p.tournament_id = NEW.tournament_id
      AND p.id NOT IN (SELECT player_white
                       FROM games
                       WHERE round_id = NEW.id
                       UNION
                       SELECT player_black
                       FROM games
                       WHERE round_id = NEW.id)
    ORDER BY p.score DESC, RAND();

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    UPDATE players SET firstname = 'Käse' WHERE id = 1;

    SET current_tournament_id = NEW.tournament_id;

    SELECT COUNT(*)
    INTO tournament_player_count
    FROM players
    WHERE tournament_id = current_tournament_id;

    SELECT COUNT(*)
    INTO tournament_round_count
    FROM rounds
    WHERE tournament_id = current_tournament_id;

    SET max_rounds = FLOOR(LOG2(tournament_player_count)) + 2;

    IF tournament_round_count <= max_rounds THEN
        OPEN player_cursor;

        player_loop: LOOP
            FETCH player_cursor INTO current_player_id, current_score;
            IF done THEN
                LEAVE player_loop;
END IF;

SET
opponent_player_id = NULL;

SELECT p.id
INTO opponent_player_id
FROM players p
WHERE p.tournament_id = current_tournament_id
  AND p.id != current_player_id
              AND p.id NOT IN (
                  SELECT player_white FROM games WHERE round_id = NEW.id
                  UNION
                  SELECT player_black FROM games WHERE round_id = NEW.id
              )
              AND p.score = current_score
ORDER BY RAND()
    LIMIT 1;

IF
opponent_player_id IS NULL THEN
SELECT p.id
INTO opponent_player_id
FROM players p
WHERE p.tournament_id = current_tournament_id
  AND p.id != current_player_id
                  AND p.id NOT IN (
                      SELECT player_white FROM games WHERE round_id = NEW.id
                      UNION
                      SELECT player_black FROM games WHERE round_id = NEW.id
                  )
ORDER BY ABS(p.score - current_score), RAND()
    LIMIT 1;
END IF;

            IF
opponent_player_id IS NOT NULL THEN
                SET game_counter = game_counter + 1;

INSERT INTO games (id, result, start, board_number,
                   round_id, player_white, player_black, tournament_id)
VALUES ((SELECT IFNULL(MAX(id), 0) + 1 FROM games),
        NULL,
        NEW.begin,
        game_counter,
        NEW.id,
        current_player_id,
        opponent_player_id,
        current_tournament_id);
END IF;

END LOOP;

CLOSE player_cursor;
END IF;
END;