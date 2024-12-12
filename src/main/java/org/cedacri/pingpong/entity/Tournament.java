package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "rules", length = 200)
    private String rules;

    @NotNull
    @Column(name = "status", length = 8)
    private String status;

    @NotNull
    @Column(name = "type", length = 11)
    private String type;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToMany(mappedBy = "tournaments")
    private List<Player> players;

    @ManyToMany
    @JoinTable(
            name = "brackets",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    private List<Match> matches;

    // Constructor
    public Tournament(String name, Integer maxPlayers, String rules, String status, String type, Timestamp createdAt) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.rules = rules;
        this.status = status;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Tournament(String name, Integer maxPlayers, String rules, String status, String type, List<Player> players) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.rules = rules;
        this.status = status;
        this.type = type;
        this.players = players;
        createdAt = new Timestamp(System.currentTimeMillis());
    }

}
