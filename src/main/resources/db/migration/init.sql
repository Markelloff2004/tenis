DROP DATABASE IF EXISTS tournament_db;

CREATE DATABASE IF NOT EXISTS tournament_db;
USE tournament_db;

CREATE TABLE IF NOT EXISTS players (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       player_name VARCHAR(100) NOT NULL,
                                       age INT DEFAULT NULL,
                                       email VARCHAR(100) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       rating INT DEFAULT 0,
                                       playing_hand VARCHAR(5) CHECK (playing_hand = 'RIGHT' OR playing_hand = 'LEFT'),
                                       winned_matches INT DEFAULT 0,
                                       losed_matches INT DEFAULT 0,
                                       goals_scored INT DEFAULT 0,
                                       goals_losed INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tournaments (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           tournament_name VARCHAR(100) NOT NULL,
                                           max_players INT NOT NULL,
#                                            rules VARCHAR(200) NOT NULL,
                                           tournament_status VARCHAR(15) CHECK (tournament_status = 'PENDING' OR tournament_status = 'ONGOING' OR tournament_status = 'FINISHED'),
                                           tournament_type VARCHAR(15) CHECK (tournament_type = 'BESTOFTHREE' OR tournament_type = 'BESTOFFIVE' OR tournament_type = 'BESTOFSEVEN'),
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
                                       round VARCHAR(15) CHECK (round IN ('Stage 3', 'Stage 2', 'Stage 1', 'Quarterfinals', 'Semifinals', 'Final')) NOT NULL,
                                       position INT,
                                       left_player_id INT,
                                       right_player_id INT,
                                       score VARCHAR(50) DEFAULT NULL,
                                       winner_id INT DEFAULT NULL,
                                       FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
                                       FOREIGN KEY (left_player_id) REFERENCES players(id) ON DELETE CASCADE,
                                       FOREIGN KEY (right_player_id) REFERENCES players(id) ON DELETE CASCADE,
                                       FOREIGN KEY (winner_id) REFERENCES players(id) ON DELETE SET NULL
);

# CREATE USER 'admin'@'%' IDENTIFIED BY 'Cedacri132!#@';
# GRANT ALL PRIVILEGES ON tournament_db.* TO 'admin'@'%';
#
# FLUSH PRIVILEGES;
