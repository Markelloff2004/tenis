package org.cedacri.pingpong.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String player1Name;

    @ElementCollection
    private List<Integer> player1Scores;

    private String player2Name;

    @ElementCollection
    private List<Integer> player2Scores;

    private String winnerName;

    private String winnerRank;

    // Constructor fără argumente (obligatoriu pentru JPA)
    public Match() {
    }

    // Constructor complet
    public Match(String player1Name, List<Integer> player1Scores, String player2Name, List<Integer> player2Scores, String winnerName, String winnerRank) {
        this.player1Name = player1Name;
        this.player1Scores = player1Scores;
        this.player2Name = player2Name;
        this.player2Scores = player2Scores;
        this.winnerName = winnerName;
        this.winnerRank = winnerRank;
    }

    // Getter și Setter pentru fiecare câmp
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public List<Integer> getPlayer1Scores() {
        return player1Scores;
    }

    public void setPlayer1Scores(List<Integer> player1Scores) {
        this.player1Scores = player1Scores;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public List<Integer> getPlayer2Scores() {
        return player2Scores;
    }

    public void setPlayer2Scores(List<Integer> player2Scores) {
        this.player2Scores = player2Scores;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getWinnerRank() {
        return winnerRank;
    }

    public void setWinnerRank(String winnerRank) {
        this.winnerRank = winnerRank;
    }
}