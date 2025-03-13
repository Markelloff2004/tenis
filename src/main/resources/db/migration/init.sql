create table if not exists tournament_db.players
(
    id           bigint auto_increment
        primary key,
    address      varchar(50)   not null,
    birth_date   date          null,
    created_at   date          null,
    email        varchar(100)  not null,
    goals_lost   int default 0 not null,
    goals_scored int default 0 not null,
    hand         varchar(255)  not null,
    lost_matches int default 0 not null,
    name         varchar(50)   not null,
    rating       int default 0 not null,
    surname      varchar(50)   not null,
    won_matches  int default 0 not null,
    check (`goals_lost` >= 0),
    check (`goals_scored` >= 0),
    check (`lost_matches` >= 0),
    check (`rating` >= 0),
    check (`won_matches` >= 0)
);

create table if not exists tournament_db.roles
(
    id   bigint auto_increment
        primary key,
    name enum ('ADMIN', 'MANAGER', 'USER') not null,
    constraint UKofx66keruapi6vyqpv6f2or37
        unique (name)
);

create table if not exists tournament_db.tournaments
(
    id                     int auto_increment
        primary key,
    created_at             date                                                                   null,
    finals_sets_to_win     enum ('BEST_OF_FIVE', 'BEST_OF_ONE', 'BEST_OF_SEVEN', 'BEST_OF_THREE') not null,
    max_players            int                                                                    not null,
    semifinals_sets_to_win enum ('BEST_OF_FIVE', 'BEST_OF_ONE', 'BEST_OF_SEVEN', 'BEST_OF_THREE') not null,
    sets_to_win            enum ('BEST_OF_FIVE', 'BEST_OF_ONE', 'BEST_OF_SEVEN', 'BEST_OF_THREE') not null,
    tournament_name        varchar(100)                                                           not null,
    tournament_status      enum ('FINISHED', 'ONGOING', 'PENDING')                                not null,
    tournament_type        enum ('OLYMPIC', 'ROBIN_ROUND')                                        not null,
    winner_id              bigint                                                                 null,
    constraint FKkw1rk5tkdcn29ipnrie2chqlq
        foreign key (winner_id) references tournament_db.players (id)
);

create table if not exists tournament_db.matches
(
    id               int    auto_increment primary key,
    position         int    null,
    round            int    not null,
    bottom_player_id bigint null,
    next_match_id    int    null,
    top_player_id    bigint null,
    tournament_id    int    not null,
    winner_id        bigint null,
    constraint FK1l0hcopaysw1kyoc52ckdnmg6
        foreign key (winner_id) references tournament_db.players (id),
    constraint FK7tafcn2nqamuh4kq2fbhju8yc
        foreign key (next_match_id) references tournament_db.matches (id),
    constraint FKeeniokyjgo5k6rmhjujatn27i
        foreign key (tournament_id) references tournament_db.tournaments (id),
    constraint FKgws1g4alkxmt7e67f15ipltu0
        foreign key (bottom_player_id) references tournament_db.players (id),
    constraint FKt1bbpjefg139fbvjxxjq877aj
        foreign key (top_player_id) references tournament_db.players (id),
    check (`round` >= 1)
);

create table if not exists tournament_db.match_scores
(
    match_id            int not null,
    bottom_player_score int null,
    top_player_score    int null,
    constraint FKk9sxw5nwn3qejk2mkwc9c88bt
        foreign key (match_id) references tournament_db.matches (id)
);

create table if not exists tournament_db.tournament_players
(
    tournament_id int    not null,
    player_id     bigint not null,
    primary key (tournament_id, player_id),
    constraint FK5wly4mkm6bgcge42bkdnt1ujg
        foreign key (tournament_id) references tournament_db.tournaments (id),
    constraint FKmeedwyshf0r8brn8awbfhmsa3
        foreign key (player_id) references tournament_db.players (id)
);

create table if not exists tournament_db.users
(
    id       bigint auto_increment
        primary key,
    password varchar(255) not null,
    username varchar(255) not null,
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table if not exists tournament_db.user_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint FKh8ciramu9cc9q3qcqiv4ue8a6
        foreign key (role_id) references tournament_db.roles (id),
    constraint FKhfh9dx7w3ubf1co1vdev94g3f
        foreign key (user_id) references tournament_db.users (id)
);


########################################################################################################################
################################## INSERTING # DATA ####################################################################
########################################################################################################################

INSERT INTO tournament_db.roles (id, name) VALUES (3, 'ADMIN');
INSERT INTO tournament_db.roles (id, name) VALUES (2, 'MANAGER');
INSERT INTO tournament_db.roles (id, name) VALUES (1, 'USER');

INSERT INTO tournament_db.users (id, password, username) VALUES (1, '$2a$12$Rb1824dn553ed/pik0K7uej7WHEVrCBRg8u.YuesIbtIG5oryIVD2', 'user');
INSERT INTO tournament_db.users (id, password, username) VALUES (2, '$2a$12$4cvsXLfXFsMXV.2mP7zTeukWVycfo.iFeg3WmNCH5n6S2E534Ote2', 'manager');
INSERT INTO tournament_db.users (id, password, username) VALUES (3, '$2a$12$53DrOtZ0lM517o9Dh8fZ/edj/qzQ/h1tXPX6tBSPqBY0zeYtSlJ8S', 'admin');

INSERT INTO tournament_db.user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO tournament_db.user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO tournament_db.user_roles (user_id, role_id) VALUES (3, 3);

INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (1, '123 Street, NY', '1990-01-15', '2025-02-12', 'john.doe@example.com', 2521, 3916, 'Right', 5, 'John', 7056, 'Doe', 143);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (2, '456 Avenue, NY', '1989-05-25', '2025-02-12', 'jane.smith@example.com', 2460, 3096, 'Left', 80, 'Jane', 4944, 'Smith', 72);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (3, '789 Boulevard, NY', '1995-03-10', '2025-02-12', 'alice.johnson@example.com', 3349, 3356, 'Right', 101, 'Alice', 4293, 'Johnson', 50);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (4, '101 Street, NY', '1988-07-20', '2025-02-12', 'bob.brown@example.com', 2878, 3855, 'Right', 41, 'Bob', 6363, 'Brown', 115);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (5, '202 Avenue, NY', '1992-11-05', '2025-02-12', 'charlie.davis@example.com', 3361, 2295, 'Left', 81, 'Charlie', 2386, 'Davis', 71);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (6, '303 Boulevard, NY', '1994-09-18', '2025-02-12', 'david.miller@example.com', 2840, 2833, 'Right', 84, 'David', 3881, 'Miller', 66);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (7, '404 Street, NY', '1991-12-12', '2025-02-12', 'emily.wilson@example.com', 2883, 1654, 'Left', 80, 'Emily', 1637, 'Wilson', 75);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (8, '505 Avenue, NY', '1993-02-28', '2025-02-12', 'frank.taylor@example.com', 3859, 3388, 'Right', 105, 'Frank', 3757, 'Taylor', 46);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (9, '606 Boulevard, NY', '1990-06-15', '2025-02-12', 'grace.white@example.com', 60, 160, 'Right', 2, 'Grace', 1450, 'White', 22);
INSERT INTO tournament_db.players (id, address, birth_date, created_at, email, goals_lost, goals_scored, hand, lost_matches, name, rating, surname, won_matches) VALUES (10, '707 Street, NY', '1987-04-30', '2025-02-12', 'henry.harris@example.com', 50, 170, 'Left', 1, 'Henry', 1500, 'Harris', 25);

INSERT INTO tournament_db.tournaments (id, created_at, finals_sets_to_win, max_players, semifinals_sets_to_win, sets_to_win, tournament_name, tournament_status, tournament_type, winner_id) VALUES (6, '2025-02-12', 'BEST_OF_FIVE', 8, 'BEST_OF_THREE', 'BEST_OF_ONE', 'Chisinau Cup', 'PENDING', 'OLYMPIC', null);
INSERT INTO tournament_db.tournaments (id, created_at, finals_sets_to_win, max_players, semifinals_sets_to_win, sets_to_win, tournament_name, tournament_status, tournament_type, winner_id) VALUES (7, '2025-02-10', 'BEST_OF_FIVE', 8, 'BEST_OF_THREE', 'BEST_OF_THREE', 'MAIB ', 'ONGOING', 'OLYMPIC', null);
INSERT INTO tournament_db.tournaments (id, created_at, finals_sets_to_win, max_players, semifinals_sets_to_win, sets_to_win, tournament_name, tournament_status, tournament_type, winner_id) VALUES (8, '2025-02-07', 'BEST_OF_SEVEN', 16, 'BEST_OF_FIVE', 'BEST_OF_THREE', 'Eximbank', 'FINISHED', 'OLYMPIC', 6);
INSERT INTO tournament_db.tournaments (id, created_at, finals_sets_to_win, max_players, semifinals_sets_to_win, sets_to_win, tournament_name, tournament_status, tournament_type, winner_id) VALUES (9, '2025-02-15', 'BEST_OF_THREE', 8, 'BEST_OF_THREE', 'BEST_OF_THREE', 'Cedacri', 'FINISHED', 'ROBIN_ROUND', 1);
INSERT INTO tournament_db.tournaments (id, created_at, finals_sets_to_win, max_players, semifinals_sets_to_win, sets_to_win, tournament_name, tournament_status, tournament_type, winner_id) VALUES (10, '2025-02-03', 'BEST_OF_ONE', 8, 'BEST_OF_ONE', 'BEST_OF_ONE', 'WebZipper', 'ONGOING', 'ROBIN_ROUND', null);

INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 1);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 2);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (6, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 3);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (6, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 4);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (6, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 5);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (6, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 6);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (6, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 7);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (6, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (7, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (9, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (10, 8);
INSERT INTO tournament_db.tournament_players (tournament_id, player_id) VALUES (8, 9);

INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (59, 1, 3, null, null, null, 7, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (60, 1, 2, 6, 59, null, 7, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (61, 2, 2, 1, 59, null, 7, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (62, 2, 1, 7, 60, 6, 7, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (63, 4, 1, 5, 61, 1, 7, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (64, 1, 1, 4, 60, 8, 7, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (65, 3, 1, 2, 61, 3, 7, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (66, 1, 4, 2, null, 6, 8, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (67, 2, 3, 7, 66, 2, 8, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (68, 4, 2, 4, 67, 7, 8, 7);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (69, 7, 1, null, 68, 7, 8, 7);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (70, 3, 2, 5, 67, 2, 8, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (71, 1, 3, 3, 66, 6, 8, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (72, 1, 2, 8, 71, 6, 8, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (73, 2, 1, null, 72, 6, 8, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (74, 1, 1, null, 72, 8, 8, 8);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (75, 6, 1, null, 70, 2, 8, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (76, 2, 2, 3, 71, 1, 8, 3);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (77, 5, 1, null, 70, 5, 8, 5);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (78, 8, 1, 9, 68, 4, 8, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (79, 4, 1, null, 76, 1, 8, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (80, 3, 1, null, 76, 3, 8, 3);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (81, 12, 1, 7, null, 2, 9, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (82, 1, 1, 2, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (83, 21, 1, 7, null, 4, 9, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (84, 9, 1, 4, null, 2, 9, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (85, 6, 1, 7, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (86, 8, 1, 3, null, 2, 9, 3);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (87, 24, 1, 7, null, 5, 9, 7);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (88, 17, 1, 7, null, 3, 9, 7);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (89, 10, 1, 5, null, 2, 9, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (90, 19, 1, 5, null, 4, 9, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (91, 3, 1, 4, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (92, 14, 1, 4, null, 3, 9, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (93, 26, 1, 7, null, 6, 9, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (94, 23, 1, 6, null, 5, 9, 5);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (95, 4, 1, 5, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (96, 27, 1, 8, null, 6, 9, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (97, 13, 1, 8, null, 2, 9, 8);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (98, 7, 1, 8, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (99, 22, 1, 8, null, 4, 9, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (100, 25, 1, 8, null, 5, 9, 5);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (101, 2, 1, 3, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (102, 20, 1, 6, null, 4, 9, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (103, 18, 1, 8, null, 3, 9, 8);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (104, 5, 1, 6, null, 1, 9, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (105, 16, 1, 6, null, 3, 9, 3);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (106, 28, 1, 8, null, 7, 9, 7);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (107, 11, 1, 6, null, 2, 9, 6);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (108, 15, 1, 5, null, 3, 9, 5);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (109, 23, 1, 6, null, 5, 10, 5);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (110, 22, 1, 8, null, 4, 10, 4);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (111, 3, 1, 4, null, 1, 10, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (112, 11, 1, 6, null, 2, 10, 2);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (113, 1, 1, 2, null, 1, 10, 1);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (114, 14, 1, 4, null, 3, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (115, 15, 1, 5, null, 3, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (116, 17, 1, 7, null, 3, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (117, 9, 1, 4, null, 2, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (118, 7, 1, 8, null, 1, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (119, 25, 1, 8, null, 5, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (120, 5, 1, 6, null, 1, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (121, 4, 1, 5, null, 1, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (122, 26, 1, 7, null, 6, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (123, 8, 1, 3, null, 2, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (124, 28, 1, 8, null, 7, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (125, 2, 1, 3, null, 1, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (126, 20, 1, 6, null, 4, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (127, 24, 1, 7, null, 5, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (128, 10, 1, 5, null, 2, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (129, 19, 1, 5, null, 4, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (130, 13, 1, 8, null, 2, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (131, 6, 1, 7, null, 1, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (132, 18, 1, 8, null, 3, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (133, 16, 1, 6, null, 3, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (134, 21, 1, 7, null, 4, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (135, 12, 1, 7, null, 2, 10, null);
INSERT INTO tournament_db.matches (id, position, round, bottom_player_id, next_match_id, top_player_id, tournament_id, winner_id) VALUES (136, 27, 1, 8, null, 6, 10, null);

INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (62, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (62, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (62, 13, 15);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (63, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (63, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (63, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (78, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (78, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (78, 4, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (68, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (68, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (68, 13, 15);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (70, 15, 17);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (70, 14, 16);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (70, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (72, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (72, 4, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (72, 7, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (76, 11, 4);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (76, 11, 6);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (76, 8, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (71, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (71, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (71, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (67, 3, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (67, 4, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (67, 5, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (66, 9, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (66, 5, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (66, 6, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (66, 11, 6);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (66, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (84, 4, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (84, 11, 4);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (84, 13, 15);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (88, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (88, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (88, 11, 4);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (91, 7, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (91, 8, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (91, 9, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (92, 11, 6);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (92, 11, 7);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (92, 11, 8);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (108, 12, 10);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (108, 12, 10);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (108, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (107, 11, 9);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (107, 11, 9);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (107, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (106, 8, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (106, 8, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (106, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (105, 3, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (105, 6, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (105, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (104, 8, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (104, 5, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (104, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (103, 3, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (103, 11, 8);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (103, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (102, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (102, 7, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (102, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (101, 14, 16);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (101, 14, 16);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (101, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (100, 13, 15);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (100, 13, 15);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (100, 11, 1);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (99, 12, 14);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (99, 12, 14);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (99, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (98, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (98, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (98, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (97, 11, 9);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (97, 11, 8);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (97, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (96, 7, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (96, 9, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (96, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (95, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (95, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (95, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (94, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (94, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (94, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (93, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (93, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (93, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (90, 9, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (90, 9, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (90, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (89, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (89, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (89, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (87, 11, 2);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (87, 11, 2);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (87, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (86, 11, 4);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (86, 11, 6);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (86, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (85, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (85, 4, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (85, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (83, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (83, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (83, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (82, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (82, 11, 13);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (82, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (81, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (81, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (81, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (109, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (110, 1, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (111, 7, 11);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (112, 15, 17);
INSERT INTO tournament_db.match_scores (match_id, bottom_player_score, top_player_score) VALUES (113, 14, 16);