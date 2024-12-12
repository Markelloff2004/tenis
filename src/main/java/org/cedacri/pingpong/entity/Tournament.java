package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();
}


//package org.cedacri.pingpong.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.sql.Timestamp;
//import java.util.List;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "tournaments")
//public class Tournament {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Integer id;
//
//    @NotNull
//    @Column(name = "tournament_name", nullable = false, length = 100)
//    private String tournamentName;
//
//    @NotNull
//    @Column(name = "max_players", nullable = false)
//    private Integer maxPlayers;
//
//    @Column(name = "rules", length = 200)
//    private String rules;
//
//    @NotNull
//    @Column(name = "tournament_status", length = 8)
//    private String tournamentStatus;
//
//    @NotNull
//    @Column(name = "tournament_type", length = 11)
//    private String tournamentType;
//
//    @NotNull
//    @Column(name = "created_at", nullable = false)
//    private Timestamp createdAt;
//
//    @ManyToMany(mappedBy = "tournaments")
//    private List<Player> players;
//
//    @OneToMany(fetch = FetchType.EAGER)
//    private List<Match> matches;
//}
