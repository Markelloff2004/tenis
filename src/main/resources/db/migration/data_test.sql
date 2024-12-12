USE tournament_db;

INSERT INTO players (player_name, age, email, rating, playing_hand, winned_matches, losed_matches, goals_scored, goals_losed)
VALUES
    ('John Doe', 25, 'johndoe@example.com', 1200, 'RIGHT', 15, 5, 60, 40),
    ('Alice Smith', 22, 'alicesmith@example.com', 1300, 'LEFT', 20, 3, 75, 25),
    ('Bob Johnson', 28, 'bobjohnson@example.com', 1100, 'RIGHT', 10, 10, 50, 50),
    ('Diana Lee', 30, 'dianalee@example.com', 1400, 'LEFT', 25, 5, 80, 30),
    ('Chris Evans', 27, 'chrisevans@example.com', 1250, 'RIGHT', 18, 8, 70, 45);

INSERT INTO tournaments (tournament_name, max_players, tournament_status, tournament_type)
VALUES
    ('Spring Championship', 16, 'PENDING', 'BESTOFTHREE'),
    ('Summer Cup', 8, 'ONGOING', 'BESTOFFIVE'),
    ('Autumn Tournament', 32, 'PENDING', 'BESTOFSEVEN'),
    ('Winter League', 16, 'ONGOING', 'BESTOFTHREE'),
    ('Pro League Finals', 8, 'FINISHED', 'BESTOFFIVE');

INSERT INTO tournament_players (tournament_id, player_id)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (2, 2), (2, 3), (2, 5),
    (3, 1), (3, 4), (3, 5),
    (4, 1), (4, 3), (4, 5),
    (5, 2), (5, 4);

INSERT INTO matches (tournament_id, round, left_player_id, right_player_id, score, winner_id)
VALUES
    (2, 1, 2, 3, '3-1', 2),
    (2, 1, 5, 3, '3-2', 5),
    (4, 1, 1, 5, '3-0', 1),
    (5, 1, 4, 2, '3-2', 4),
    (5, 2, 4, 1, '3-1', 4);
