package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments")
@Data
public class Tournament {

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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToMany(mappedBy = "tournaments", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Player> players = new HashSet<>();
}
