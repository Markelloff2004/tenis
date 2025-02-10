package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
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
    @NotNull(message = "Tournament type cannot be null")
    @Column(name = "tournament_type", nullable = false)
    private TournamentTypeEnum tournamentType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament sets cannot be null")
    @Column(name = "sets_to_win", nullable = false)
    private SetTypesEnum setsToWin;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament semifinals sets cannot be null")
    private SetTypesEnum semifinalsSetsToWin;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament final sets cannot be null")
    private SetTypesEnum finalsSetsToWin;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();


 @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    @OneToMany(mappedBy = "tournament", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Match> matches = new HashSet<>();
}
