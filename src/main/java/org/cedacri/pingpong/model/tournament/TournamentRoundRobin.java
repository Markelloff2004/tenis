package org.cedacri.pingpong.model.tournament;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
@Table(name = "tournaments_robin")
@DiscriminatorValue("ROUND_ROBIN")
public class TournamentRoundRobin extends BaseTournament {

    @Min(value = 2, message = "Players per group must be at least 2")
    @Column(name = "players_per_group", nullable = false)
    private int playersPerGroup;

    @Min(value = 1, message = "Stages number must be at least 1")
    @Column(name = "stages_number", nullable = false)
    private int stagesNumber;

    @Min(value = 2, message = "Final group size must be at least 2")
    @Column(name = "final_group_size", nullable = false)
    private int finalGroupSize;

    @Override
    protected void calculateMaxPlayers() {
        int maxPlayers = this.getPlayers().size();
        
        this.setMaxPlayers(maxPlayers);
    }
}
