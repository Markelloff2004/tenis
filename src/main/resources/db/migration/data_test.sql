USE tournament_db;

INSERT INTO players (player_name, age, email, rating, playing_hand, winned_matches, losed_matches, goals_scored, goals_losed)
VALUES
    ('John Doe', 25, 'johndoe@example.com', 1200, 'RIGHT', 15, 5, 60, 40),
    ('Alice Smith', 22, 'alicesmith@example.com', 1300, 'LEFT', 20, 3, 75, 25),
    ('Bob Johnson', 28, 'bobjohnson@example.com', 1100, 'RIGHT', 10, 10, 50, 50),
    ('Diana Lee', 30, 'dianalee@example.com', 1400, 'LEFT', 25, 5, 80, 30),
    ('Chris Evans', 27, 'chrisevans@example.com', 1250, 'RIGHT', 18, 8, 70, 45),
    ('Emily Brown', 24, 'emilybrown@example.com', 1150, 'LEFT', 12, 7, 55, 35),
    ('Michael Green', 29, 'michaelgreen@example.com', 1350, 'RIGHT', 22, 6, 85, 40),
    ('Sarah White', 26, 'sarahwhite@example.com', 1280, 'RIGHT', 17, 8, 65, 38);

INSERT INTO tournaments (tournament_name, max_players, tournament_status, tournament_type)
VALUES
    ('Spring Championship', 8, 'PENDING', 'BESTOFTHREE'),
    ('Summer Cup', 8, 'ONGOING', 'BESTOFFIVE'),
    ('Autumn Tournament', 32, 'PENDING', 'BESTOFSEVEN'),
    ('Winter League', 16, 'ONGOING', 'BESTOFTHREE'),
    ('Pro League Finals', 8, 'FINISHED', 'BESTOFFIVE');

INSERT INTO tournament_players (tournament_id, player_id)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),(1, 5), (1, 6), (1, 7), (1, 8),
    (2, 2), (2, 3), (2, 5),
    (3, 1), (3, 4), (3, 5),
    (4, 1), (4, 3), (4, 5),
    (5, 2), (5, 4);

INSERT INTO matches (tournament_id, round, left_player_id, right_player_id, score, winner_id)
VALUES
    -- Runda 1: 8 jucători (4 meciuri)
    (1, 1, 1, 2, '11:7;9:11;11:8', 1),  -- Jucător 1 câștigă
    (1, 1, 3, 4, '11:6;11:9', 3),       -- Jucător 3 câștigă
    (1, 1, 5, 6, '9:11;11:7;11:6', 5),  -- Jucător 5 câștigă
    (1, 1, 7, 8, '11:8;11:5', 7),       -- Jucător 7 câștigă

    -- Runda 2: Semifinale (2 meciuri)
    (1, 2, 1, 3, '11:9;11:7', 1),       -- Jucător 1 câștigă
    (1, 2, 5, 7, '9:11;11:8;11:6', 5),  -- Jucător 5 câștigă

    -- Runda 3: Finala
    (1, 3, 1, 5, '11:6;9:11;11:7', 1),  -- Jucător 1 câștigă turneul

    -- Meci pentru locul 3
    (1, 3, 3, 7, '11:8;10:12;11:9', 3); -- Jucător 3 câștigă locul 3
