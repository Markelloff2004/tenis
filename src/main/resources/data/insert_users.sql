########################################################################################################################
################################## INSERTING # DATA ####################################################################
########################################################################################################################


INSERT INTO tournament_db.roles (id, name) VALUES (1, 'USER');
INSERT INTO tournament_db.roles (id, name) VALUES (2, 'MANAGER');
INSERT INTO tournament_db.roles (id, name) VALUES (3, 'ADMIN');

INSERT INTO tournament_db.users (id, password, username)
--                                   user123
VALUES (1, '$2a$10$Ft9mP99.rFBNzd0imERKSe5GTAdX5IsANSwdqY3YgPGAYdX5sFuVq', 'user');
INSERT INTO tournament_db.users (id, password, username)
--                                   manager123
VALUES (2, '$2a$10$MCURUQMj72IBLxYySpVX5OwU5RoOnQaNfhZMFOS79JtcDsPH8yQFu', 'manager');
INSERT INTO tournament_db.users (id, password, username)
--                                   admin123
VALUES (3, '$2a$10$jzmvuf/fAc4JZksKcjoWk.FWvWNcFY7nkkeaPkggcIV2SkHaqsSVm', 'admin');

INSERT INTO tournament_db.user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO tournament_db.user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO tournament_db.user_roles (user_id, role_id) VALUES (3, 3);
