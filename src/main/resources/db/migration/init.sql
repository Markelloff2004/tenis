create database tournament_db;

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
    id               int    primary key,
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