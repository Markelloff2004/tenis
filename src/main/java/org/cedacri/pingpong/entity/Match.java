package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "round", nullable = false)
    private Integer round;

    @Column(name = "position")
    private Integer position;

    /*
    score is in formate topPlayer:bottomPlayer;
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "match_scores", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "score")
    private List<Score> score;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "top_player_id")
    private Player topPlayer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bottom_player_id")
    private Player bottomPlayer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private Player winner;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "next_match_id")
    private Match nextMatch;
}

