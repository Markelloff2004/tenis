CREATE DATABASE IF NOT EXISTS tournament_db;

CREATE TABLE IF NOT EXISTS players (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
                                       email VARCHAR(100) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tournaments (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(100) NOT NULL,
                                           max_players INT NOT NULL,
                                           rules VARCHAR(200) NOT NULL,
                                           status VARCHAR(8) CHECK (status = 'PENDING' OR status = 'ONGOING' OR status = 'FINISHED'),
                                           type VARCHAR(11) CHECK (type = 'BESTOFTHREE' OR type = 'BESTOFFIVE' OR type = 'BESTOFSEVEN'),
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

CREATE TABLE IF NOT EXISTS characteristics (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        rating INT,
                                        playing_hand char CHECK ( playing_hand = 'RIGHT' or playing_hand = 'LEFT' ),
                                        winned_matches INT DEFAULT NULL,
                                        losed_matches INT DEFAULT NULL,
                                        goals_scored INT DEFAULT NULL,
                                        player_id INT NOT NULL,
                                        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

# CREATE USER 'admin'@'%' IDENTIFIED BY 'Cedacri132!#@';
# GRANT ALL PRIVILEGES ON tournament_db.* TO 'admin'@'%';
#
# FLUSH PRIVILEGES;