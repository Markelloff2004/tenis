
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
