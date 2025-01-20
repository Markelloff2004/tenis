package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.cedacri.pingpong.interfaces.ITournament;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments")
@Data
public class Tournament implements ITournament {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "tournament_name", nullable = false, length = 100)
    private String tournamentName;

    @NotNull
    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Size(max = 15)
    @Column(name = "tournament_status", length = 15)
    private String tournamentStatus;

    @Size(max = 15)
    @Column(name = "tournament_type", length = 15)
    private String tournamentType;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Match> matches = new HashSet<>();
}
