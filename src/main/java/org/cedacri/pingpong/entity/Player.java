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
@Table(name = "players")
public class Player {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "player_name", nullable = false, length = 100)
    private String playerName;

    @Column(name = "age")
    private Integer age;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "rating")
    private Integer rating;

    @Size(max = 5)
    @Column(name = "playing_hand", length = 5)
    private String playingHand;

    @ColumnDefault("0")
    @Column(name = "winned_matches")
    private Integer winnedMatches;

    @ColumnDefault("0")
    @Column(name = "losed_matches")
    private Integer losedMatches;

    @ColumnDefault("0")
    @Column(name = "goals_scored")
    private Integer goalsScored;

    @ColumnDefault("0")
    @Column(name = "goals_losed")
    private Integer goalsLosed;

    @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY)
    private Set<Tournament> tournaments = new HashSet<>();

}


//package org.cedacri.pingpong.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.ColumnDefault;
//
//import java.time.Instant;
//import java.util.List;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "players")
//public class Player {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Integer id;
//
//    @Size(max = 100)
//    @NotNull
//    @Column(name = "player_name", nullable = false, length = 100)
//    private String playerName;
//
//    @NotNull
//    @Column(name = "age")
//    private Integer age;
//
//    @Size(max = 100)
//    @NotNull
//    @Column(name = "email", nullable = false, length = 100)
//    private String email;
//
//    @ColumnDefault("CURRENT_TIMESTAMP")
//    @Column(name = "created_at")
//    private Instant createdAt;
//
//    @Column(name = "rating")
//    private Integer rating;
//
//    @Column(name = "playing_hand")
//    private String playingHand;
//
//    @Column(name = "winned_matches")
//    private Integer winnedMatches;
//
//    @Column(name = "losed_matches")
//    private Integer losedMatches;
//
//    @Column(name = "goals_scored")
//    private Integer goalsScored;
//
//    @Column(name = "goals_losed")
//    private Integer goalsLosed;
//
//    @ManyToMany
//    @JoinTable(
//            name = "tournament_players",
//            joinColumns = @JoinColumn(name = "player_id"),
//            inverseJoinColumns = @JoinColumn(name = "tournament_id")
//    )
//    private List<Tournament> tournaments;
//}
