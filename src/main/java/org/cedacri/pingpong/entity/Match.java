package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @NotNull
    @Column(name = "round", nullable = false)
    private Integer round;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "left_player_id", nullable = false)
    private Player leftPlayer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "right_player_id", nullable = false)
    private Player rightPlayer;

    @Size(max = 50)
    @Column(name = "score", length = 50)
    private String score;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "winner_id")
    private Player winner;

}
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "matches")
//public class Match {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Integer id;
//
//    @NotNull
//    @Column(name = "tournament_id", nullable = false)
//    private Integer tournamentId;
//
//    @NotNull
//    @Column(name = "round", nullable = false)
//    private Integer round;
//
//    @NotNull
//    @Column(name = "left_player_id", nullable = false)
//    private Integer leftPlayerId;
//
//    @NotNull
//    @Column(name = "right_player_id", nullable = false)
//    private Integer rightPlayerId;
//
//    @Column(name = "score", length = 50)
//    private String score;
//
//    @Column(name = "winner_id")
//    private Integer winnerId;
//
//    @ManyToOne
//    private Tournament tournaments;
//}
