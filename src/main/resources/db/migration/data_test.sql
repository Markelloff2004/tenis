
INSERT INTO players (name, surname, birth_date, address, email, created_at, rating, hand, won_matches, lost_matches, goals_scored, goals_lost)
VALUES
    ('John', 'Doe', '1990-01-15', '123 Street, NY', 'john.doe@example.com', CURRENT_DATE, 1200, 'Right', 10, 5, 100, 80),
    ('Jane', 'Smith', '1989-05-25', '456 Avenue, NY', 'jane.smith@example.com', CURRENT_DATE, 1300, 'Left', 15, 4, 110, 75),
    ('Alice', 'Johnson', '1995-03-10', '789 Boulevard, NY', 'alice.johnson@example.com', CURRENT_DATE, 1150, 'Right', 12, 6, 105, 85),
    ('Bob', 'Brown', '1988-07-20', '101 Street, NY', 'bob.brown@example.com', CURRENT_DATE, 1400, 'Right', 20, 3, 150, 70),
    ('Charlie', 'Davis', '1992-11-05', '202 Avenue, NY', 'charlie.davis@example.com', CURRENT_DATE, 1250, 'Left', 14, 5, 120, 90),
    ('David', 'Miller', '1994-09-18', '303 Boulevard, NY', 'david.miller@example.com', CURRENT_DATE, 1100, 'Right', 9, 8, 95, 88),
    ('Emily', 'Wilson', '1991-12-12', '404 Street, NY', 'emily.wilson@example.com', CURRENT_DATE, 1350, 'Left', 18, 4, 130, 65),
    ('Frank', 'Taylor', '1993-02-28', '505 Avenue, NY', 'frank.taylor@example.com', CURRENT_DATE, 1000, 'Right', 8, 10, 80, 95),
    ('Grace', 'White', '1990-06-15', '606 Boulevard, NY', 'grace.white@example.com', CURRENT_DATE, 1450, 'Right', 22, 2, 160, 60),
    ('Henry', 'Harris', '1987-04-30', '707 Street, NY', 'henry.harris@example.com', CURRENT_DATE, 1500, 'Left', 25, 1, 170, 50);

#
# INSERT INTO tournaments (tournament_name, max_players, tournament_status, tournament_type, sets_to_win, semifinals_sets_to_win, finals_sets_to_win, created_at)
# VALUES
#     ('Spring Open', 8, 'ONGOING', 'OLIMPIC', 'BEST_OF_THREE', 'BEST_OF_FIVE', 'BEST_OF_SEVEN', CURRENT_DATE),
#     ('Summer Cup', 8, 'FINISHED', 'ROBIN_ROUND', 'BEST_OF_FIVE', 'BEST_OF_FIVE', 'BEST_OF_SEVEN', CURRENT_DATE),
#     ('Autumn Championship', 8, 'ONGOING', 'OLIMPIC', 'BEST_OF_THREE', 'BEST_OF_FIVE', 'BEST_OF_SEVEN', CURRENT_DATE),
#     ('Winter Tournament', 8, 'FINISHED', 'ROBIN_ROUND', 'BEST_OF_FIVE', 'BEST_OF_FIVE', 'BEST_OF_SEVEN', CURRENT_DATE),
#     ('Grand Slam', 8, 'PENDING', 'OLIMPIC', 'BEST_OF_SEVEN', 'BEST_OF_SEVEN', 'BEST_OF_SEVEN', CURRENT_DATE);
#
#
# INSERT INTO tournament_players (player_id, tournament_id)
# VALUES
#     (1, 1), (2, 1), (3, 1), (4, 1), (5, 1),
#     (6, 2), (7, 2), (8, 2), (9, 2), (10, 2),
#     (1, 3), (2, 3), (6, 3), (7, 3), (8, 3),
#     (3, 4), (4, 4), (9, 4), (10, 4), (5, 4),
#     (1, 5), (2, 5), (3, 5), (4, 5), (5, 5);
#
#
# INSERT INTO matches(position, tournament_id, bottom_player_id, top_player_id, winner_id, score, round)
# VALUES
#     (1, 2, 6, 7, 6, '11:2', 1),
#     (1, 2, 8, null, 8, null, 1),
#     (2, 2, 9, null, 9, null, 1),
#     (2, 2, 10, null, 10, null, 1),
#     (3, 2, 6, 8, 6, '11:1', 2),
#     (3, 2, 9, 10, null, null, 2),
#     (4, 2, 6, null, null, null, 3);



use tournament_db;

INSERT INTO tournament_db.tournaments (created_at, id, max_players, tournament_name, finals_sets_to_win, semifinals_sets_to_win, sets_to_win, tournament_status, tournament_type, winner_id) VALUES ('2025-02-05', 1, 16, 'cedacri', 'BEST_OF_THREE', 'BEST_OF_THREE', 'BEST_OF_THREE', 'FINISHED', 'OLYMPIC', 6);
INSERT INTO tournament_db.tournaments (created_at, id, max_players, tournament_name, finals_sets_to_win, semifinals_sets_to_win, sets_to_win, tournament_status, tournament_type, winner_id) VALUES ('2025-02-05', 2, 16, 'Test1', 'BEST_OF_ONE', 'BEST_OF_ONE', 'BEST_OF_ONE', 'FINISHED', 'OLYMPIC', 3);
INSERT INTO tournament_db.tournaments (created_at, id, max_players, tournament_name, finals_sets_to_win, semifinals_sets_to_win, sets_to_win, tournament_status, tournament_type, winner_id) VALUES ('2025-02-05', 3, 16, 'Test2', 'BEST_OF_ONE', 'BEST_OF_ONE', 'BEST_OF_ONE', 'PENDING', 'OLYMPIC', null);

INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (1, 9);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 9);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (3, 9);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (2, 10);

INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (1, null, 1, 4, 1, 2, 6, 6);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (2, 1, 1, 3, 1, 3, 6, 6);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (3, 2, 2, 2, 1, 1, 3, 3);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (4, 3, 3, 1, 1, null, 3, 3);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (5, 1, 2, 3, 1, 7, 2, 2);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (6, 5, 4, 2, 1, 4, 7, 7);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (7, 6, 7, 1, 1, null, 7, 7);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (8, 6, 8, 1, 1, 9, 4, 4);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (9, 2, 1, 2, 1, 8, 6, 6);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (10, 9, 2, 1, 1, null, 6, 6);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (11, 5, 3, 2, 1, 5, 2, 2);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (12, 11, 6, 1, 1, null, 2, 2);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (13, 3, 4, 1, 1, null, 1, 1);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (14, 11, 5, 1, 1, null, 5, 5);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (15, 9, 1, 1, 1, null, 8, 8);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (16, null, 1, 4, 2, 2, 3, 3);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (17, 16, 1, 3, 2, 1, 3, 3);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (18, 17, 2, 2, 2, 6, 1, 1);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (19, 16, 2, 3, 2, 7, 2, 2);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (20, 19, 4, 2, 2, 4, 7, 7);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (21, 19, 3, 2, 2, 5, 2, 2);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (22, 21, 6, 1, 2, null, 2, 2);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (23, 18, 3, 1, 2, null, 1, 1);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (24, 21, 5, 1, 2, null, 5, 5);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (25, 20, 8, 1, 2, 9, 4, 4);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (26, 18, 4, 1, 2, null, 6, 6);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (27, 17, 1, 2, 2, 8, 3, 3);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (28, 27, 2, 1, 2, null, 3, 3);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (29, 20, 7, 1, 2, 10, 7, 7);
INSERT INTO tournament_db.matches (id, next_match_id, position, round, tournament_id, bottom_player_id, top_player_id, winner_id) VALUES (30, 27, 1, 1, 2, null, 8, 8);

INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (1, 1, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (6, 1, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (1, 2, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (2, 2, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (1, 3, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (1, 3, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (1, 5, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (5, 5, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (4, 6, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (5, 6, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (1, 8, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (10, 8, 12);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (0, 9, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (3, 9, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (2, 11, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (3, 11, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (6, 16, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (5, 17, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (4, 18, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (5, 19, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (5, 20, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (2, 21, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (3, 25, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (3, 27, 11);
INSERT INTO tournament_db.match_scores (bottom_player_score, match_id, top_player_score) VALUES (3, 29, 11);

