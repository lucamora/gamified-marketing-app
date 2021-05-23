CREATE TRIGGER reset_points
    AFTER DELETE ON answers
    FOR EACH ROW
BEGIN
    DECLARE section VARCHAR(20);
    DECLARE score INT DEFAULT 0;

    SELECT q.section INTO section
        FROM questions AS q
        WHERE q.id = OLD.question_id;

    IF (section = 'STATISTICAL') THEN
        SET score = 2; -- statistical question
    ELSEIF (section = 'MARKETING') THEN
        SET score = 1; -- marketing question
    END IF;

    UPDATE users AS u
        SET u.points = u.points - score
        WHERE u.id = OLD.user_id;
END;