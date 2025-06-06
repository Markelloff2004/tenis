package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.interfaces.ITournamentRoundRobin;

import java.io.Serializable;

@Entity
@Table(name = "tournaments_round_robin")
@Getter
@Setter
public class TournamentRoundRobin extends Tournament implements ITournamentRoundRobin, Serializable {

    @Column
    @Min(2)
    @NotNull(message = "Tournament amount of players in group cannot be null")
    private Integer maxAmountOfPlayersOnGroup;

    @Column
    @Min(2)
    @NotNull(message = "Tournament amount of players in finals cannot be null")
    private Integer maxAmountOfPlayersInFinals;

    @Column
    @Min(2)
    @NotNull(message = "Tournament numbers of steps cannot be null")
    private Integer numOfSteps;

    @Override
    public Integer getMaxAmountOfPlayersInGroup() {
        return maxAmountOfPlayersOnGroup;
    }

    @Override
    public Integer getMaxAmountOfPlayersInFinals() {
        return maxAmountOfPlayersInFinals;
    }

    @Override
    public Integer getNumOfSteps() {
        return numOfSteps;
    }
}
