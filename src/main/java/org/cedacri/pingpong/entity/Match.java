package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "tournament_id", nullable = false)
    private Integer tournamentId;

    @NotNull
    @Column(name = "round", nullable = false)
    private Integer round;

    @NotNull
    @Column(name = "left_player_id", nullable = false)
    private Integer leftPlayerId;

    @NotNull
    @Column(name = "right_player_id", nullable = false)
    private Integer rightPlayerId;

    @Column(name = "score", length = 50)
    private String score;

    @Column(name = "winner_id")
    private Integer winnerId;

    @ManyToMany(mappedBy = "matches")
    private List<Tournament> tournaments;
}
