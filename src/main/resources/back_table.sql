create table game_bak select * from game;
create table game_players_bak select * from game_players;
create table sheet_bak select * from sheet;

alter table game_bak add column delete_at timestamp;
alter table game_players_bak add column delete_at timestamp;
alter table sheet_bak add column delete_at timestamp;



DELIMITER $$
CREATE TRIGGER before_game_delete
BEFORE DELETE ON game
FOR EACH ROW
BEGIN
INSERT INTO
game_bak (
    id,
    course,
    hole,
    play_date,
    removed,
    round,
    status,
    filed,
    host,
    competition,
    delete_at
)
VALUES (
    OLD.id,
    OLD.course,
    OLD.hole,
    OLD.play_date,
    OLD.removed,
    OLD.round,
    OLD.status,
    OLD.filed,
    OLD.host,
    OLD.competition,
    NOW()
);
END$$

DELIMITER ;



DELIMITER $$
CREATE TRIGGER before_game_players_delete
BEFORE DELETE ON game_players
FOR EACH ROW
BEGIN
INSERT INTO
game_players_bak (
    game_id,
    players_id,
    delete_at
)
VALUES (
    OLD.game_id,
    OLD.players_id,
    NOW()
);
END$$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER before_sheet_delete
BEFORE DELETE ON sheet
FOR EACH ROW
BEGIN
INSERT INTO
sheet_bak (
   course,
   hole,
   hit,
   round,
   player,
   game_id,
   delete_at
)
VALUES (
    OLD.course,
    OLD.hole,
    OLD.hit,
    OLD.round,
    OLD.player,
    OLD.game_id,
    NOW()
);
END$$
DELIMITER ;