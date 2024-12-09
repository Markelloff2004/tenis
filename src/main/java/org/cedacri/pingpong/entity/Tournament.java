package org.cedacri.pingpong.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournaments") // Specifică numele tablei, dacă diferă de numele clasei
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID generat automat de baza de date
    private Long id;

    private String name;
    private String status;
    private int players;
    private String rules;
    private String winner;

    // Constructor complet
    public Tournament(Long id, String name, String status, int players, String rules, String winner) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.players = players;
        this.rules = rules;
        this.winner = winner;
    }

    // Constructor implicit (necesar pentru JPA)
    public Tournament() {}

    // Getteri și setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
