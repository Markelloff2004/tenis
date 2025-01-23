package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cedacri.pingpong.enums.SetTypes;
import org.cedacri.pingpong.enums.TournamentType;
import org.cedacri.pingpong.interfaces.ITournament;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "tournaments")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", nullable = false)
    private TournamentType tournamentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sets_to_win", nullable = false)
    private SetTypes setsToWin;

    @Enumerated(EnumType.STRING)
    @Column(name = "semifinals_sets_to_win", nullable = false)
    private SetTypes semifinalsSetsToWin;

    @Enumerated(EnumType.STRING)
    @Column(name = "finals_sets_to_win", nullable = false)
    private SetTypes finalsSetsToWin;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Match> matches = new HashSet<>();
}
