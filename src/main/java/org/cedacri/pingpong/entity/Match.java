package org.cedacri.pingpong.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Round cannot be null or empty")
    @Min(value = 1, message = "Round cannot be less than 1")
    @Column(name = "round", nullable = false)
    private Integer round;

    @Column(name = "position")
    private Integer position;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "match_scores", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "score")
    private List<Score> score;

    @Column(name = "setsToPlay", nullable = false)
    private Integer setsToPlay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    @NotNull(message = "TournamentId cannot be null")
    private BaseTournament tournament;

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

