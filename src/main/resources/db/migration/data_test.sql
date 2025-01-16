USE tournament_db;

INSERT INTO players (player_name, age, email, rating, playing_hand, winned_matches, losed_matches, goals_scored, goals_losed)
VALUES
    ('John Doe', 25, 'johndoe@example.com', 1200, 'RIGHT', 15, 5, 60, 40),
    ('Alice Smith', 22, 'alicesmith@example.com', 1300, 'LEFT', 20, 3, 75, 25),
    ('Bob Johnson', 28, 'bobjohnson@example.com', 1100, 'RIGHT', 10, 12, 50, 60),
    ('Diana Lee', 30, 'dianalee@example.com', 1400, 'LEFT', 25, 5, 80, 30),
    ('Chris Evans', 27, 'chrisevans@example.com', 1250, 'RIGHT', 18, 8, 70, 45),
    ('Emily Brown', 24, 'emilybrown@example.com', 1150, 'LEFT', 12, 9, 55, 50),
    ('Michael Green', 29, 'michaelgreen@example.com', 1350, 'RIGHT', 22, 6, 85, 40),
    ('Sarah White', 26, 'sarahwhite@example.com', 1280, 'RIGHT', 17, 8, 65, 38),
    ('Tom Hardy', 23, 'tomhardy@example.com', 1100, 'LEFT', 9, 10, 50, 55),
    ('Olivia Brown', 31, 'oliviabrown@example.com', 1450, 'RIGHT', 27, 4, 90, 20);

INSERT INTO tournaments (tournament_name, max_players, tournament_status, tournament_type)
VALUES
    ('Spring Championship', 8, 'PENDING', 'BESTOFTHREE'),
     ('Summer Cup', 16, 'ONGOING', 'BESTOFFIVE'),
     ('Autumn Tournament', 32, 'PENDING', 'BESTOFSEVEN'),
     ('Winter League', 16, 'FINISHED', 'BESTOFTHREE'),
     ('Pro League Finals', 8, 'FINISHED', 'BESTOFFIVE'),
     ('Junior Cup', 4, 'PENDING', 'BESTOFTHREE');

INSERT INTO tournament_players (tournament_id, player_id)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
    (2, 1), (2, 3), (2, 5), (2, 7), (2, 9), (2, 10),
    (3, 2), (3, 4), (3, 6), (3, 8), (3, 10),
    (4, 3), (4, 5), (4, 7), (4, 9),
    (5, 1), (5, 4), (5, 7), (5, 10),
    (6, 2), (6, 5), (6, 8), (6, 9);

# INSERT INTO matches (tournament_id, round, left_player_id, right_player_id, score, winner_id)
# VALUES
#     -- Runda 1: 8 jucători (4 meciuri)
#     (1, 1, 1, 2, '11:7;9:11;11:8', 1),  -- Jucător 1 câștigă
#     (1, 1, 3, 4, '11:6;11:9', 3),       -- Jucător 3 câștigă
#     (1, 1, 5, 6, '9:11;11:7;11:6', 5),  -- Jucător 5 câștigă
#     (1, 1, 7, 8, '11:8;11:5', 7),       -- Jucător 7 câștigă
#
#     -- Runda 2: Semifinale (2 meciuri)
#     (1, 2, 1, 3, '11:9;11:7', 1),       -- Jucător 1 câștigă
#     (1, 2, 5, 7, '9:11;11:8;11:6', 5),  -- Jucător 5 câștigă
#
#     -- Runda 3: Finala
#     (1, 3, 1, 5, '11:6;9:11;11:7', 1),  -- Jucător 1 câștigă turneul
#
#     -- Meci pentru locul 3
#     (1, 3, 3, 7, '11:8;10:12;11:9', 3); -- Jucător 3 câștigă locul 3

-- Inserarea meciurilor cu date mai veridice
INSERT INTO matches (tournament_id, round, position, left_player_id, right_player_id, score, winner_id)
VALUES
    -- Turneu 1: Spring Championship
    -- Runda 1: 8 jucători (4 meciuri)
    (1, 'Quarterfinals', 1, 1, 2, '11:9;8:11;11:7', 1),  -- John câștigă cu Alice
    (1, 'Quarterfinals', 2, 3, 4, '11:6;12:10', 3),     -- Bob câștigă cu Diana
    (1, 'Quarterfinals', 3, 5, 6, '9:11;11:8;11:9', 5), -- Chris câștigă cu Emily
    (1, 'Quarterfinals', 4, 7, 8, '11:7;11:5', 7),      -- Michael câștigă cu Sarah

    -- Runda 2: Semifinale
    (1, 'Semifinals', 1, 1, 3, '11:7;9:11;11:8', 1),    -- John câștigă cu Bob
    (1, 'Semifinals', 2, 5, 7, '8:11;11:9;11:7', 5),    -- Chris câștigă cu Michael

    -- Runda 3: Finala
    (1, 'Final', 1, 1, 5, '11:8;10:12;11:9', 1);      -- John câștigă turneul

#     -- Turneu 2: Summer Cup
#     -- Runda 1: 16 jucători (8 meciuri)
#     (2, 'Stage 1', 1, 9, '11:7;11:6', 1),            -- John câștigă cu Tom
#     (2, 'Stage 1', 2, 10, '8:11;12:10;11:9', 2),     -- Alice câștigă cu Olivia
#     (2, 'Stage 1', 3, 7, '11:9;11:8', 3),            -- Bob câștigă cu Michael
#     (2, 'Stage 1', 5, 4, '10:12;11:7;11:9', 5),      -- Chris câștigă cu Diana
#
#     -- Runda 2: Sferturi de finală
#     (2, 'Quarterfinals', 1, 2, '11:8;11:9', 1),      -- John câștigă cu Alice
#     (2, 'Quarterfinals', 3, 5, '8:11;11:9;12:10', 3), -- Bob câștigă cu Chris
#
#     -- Runda 3: Semifinale
#     (2, 'Semifinals', 1, 3, '9:11;11:8;11:6', 1),    -- John câștigă cu Bob
#
#     -- Finala
#     (2, 'Final', 1, 5, '11:7;11:9', 1),              -- John câștigă turneul
#
#     -- Turneu 3: Autumn Tournament
#     -- Runda 1: 32 jucători (16 meciuri)
#     (3, 'Stage 1', 2, 4, '11:8;9:11;11:7', 2),       -- Alice câștigă cu Diana
#     (3, 'Stage 1', 5, 6, '11:7;11:9', 5),            -- Chris câștigă cu Emily
#     (3, 'Stage 1', 8, 9, '10:12;11:9;11:6', 8),      -- Sarah câștigă cu Tom
#     (3, 'Stage 1', 3, 10, '11:7;11:5', 3),           -- Bob câștigă cu Olivia
#
#     -- Runda 2: Optimi de finală
#     (3, 'Quarterfinals', 2, 5, '11:9;12:10', 2),     -- Alice câștigă cu Chris
#     (3, 'Quarterfinals', 8, 3, '9:11;11:7;11:9', 8), -- Sarah câștigă cu Bob
#
#     -- Semifinale
#     (3, 'Semifinals', 2, 8, '8:11;11:9;11:8', 2),    -- Alice câștigă cu Sarah
#
#     -- Finala
#     (3, 'Final', 2, 10, '11:7;11:8', 2);             -- Alice câștigă turneul
