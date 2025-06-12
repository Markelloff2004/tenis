package org.cedacri.pingpong.model.tournament;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cedacri.pingpong.model.enums.SetsTypesEnum;
import org.cedacri.pingpong.model.enums.TournamentStatusEnum;
import org.cedacri.pingpong.model.enums.TournamentTypeEnum;
import org.cedacri.pingpong.model.match.Match;
import org.cedacri.pingpong.model.player.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "tournaments")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tournament_type", discriminatorType = DiscriminatorType.STRING)
public abstract class BaseTournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotBlank(message = "Tournament name cannot be null or blank")
    @Column(name = "tournament_name", nullable = false, length = 100)
    private String tournamentName;

    @NotNull(message = "Max amount of players should be calculated")
    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament status cannot be null")
    @Column(name = "tournament_status", length = 15)
    private TournamentStatusEnum tournamentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", insertable = false, updatable = false)
    private TournamentTypeEnum tournamentType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament sets cannot be null")
    @Column(name = "sets_to_win", nullable = false)
    private SetsTypesEnum setsToWin;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "started_at")
    private LocalDate startedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "winner_id")
    private Player winner;

    public void addPlayer(Player playerToAdd) {
        players.add(playerToAdd);
    }
}
