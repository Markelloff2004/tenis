CREATE TABLE IF NOT EXISTS players (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
                                       seed INT DEFAULT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tournaments (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(100) NOT NULL,
                                           max_players INT NOT NULL,
                                           rules VARCHAR(200) NOT NULL,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tournament_players (
                                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                                  tournament_id INT NOT NULL,
                                                  player_id INT NOT NULL,
                                                  FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
                                                  FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS matches (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       tournament_id INT NOT NULL,
                                       round INT NOT NULL,
                                       left_player_id INT NOT NULL,
                                       right_player_id INT NOT NULL,
                                       score VARCHAR(50) DEFAULT NULL,
                                       winner_id INT DEFAULT NULL,
                                       FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
                                       FOREIGN KEY (left_player_id) REFERENCES players(id) ON DELETE CASCADE,
                                       FOREIGN KEY (right_player_id) REFERENCES players(id) ON DELETE CASCADE,
                                       FOREIGN KEY (winner_id) REFERENCES players(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS brackets (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        tournament_id INT NOT NULL,
                                        match_id INT NOT NULL,
                                        position INT NOT NULL,
                                        FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
                                        FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE
);

INSERT INTO players (name, seed) VALUES ('Player 1', 1), ('Player 2', 2);
INSERT INTO tournaments (name, max_players, rules) VALUES ('Example Tournament', 16, 'Best of Three');
