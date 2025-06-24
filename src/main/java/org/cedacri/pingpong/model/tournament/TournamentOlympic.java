package org.cedacri.pingpong.model.tournament;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.model.enums.SetsTypesEnum;
import org.cedacri.pingpong.utils.Constants;

@Getter
@Setter
@Entity
@Table(name = "tournaments_olympic")
@DiscriminatorValue("OLYMPIC")
public class TournamentOlympic extends BaseTournament // implements ITournament
{

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament semifinals sets cannot be null")
    private SetsTypesEnum semifinalsSetsToWin;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament final sets cannot be null")
    private SetsTypesEnum finalsSetsToWin;

    @Override
    protected void calculateMaxPlayers() {
        int currentPlayers = this.getPlayers().size();

        if (currentPlayers > 128) {
            throw new IllegalArgumentException(Constants.TO_MUCH_PLAYERS_MESSAGE);
        } else if (currentPlayers > 64) {
            this.setMaxPlayers(128);
        } else if (currentPlayers > 32) {
            this.setMaxPlayers(64);
        } else if (currentPlayers > 16) {
            this.setMaxPlayers(32);
        } else if (currentPlayers > 8) {
            this.setMaxPlayers(16);
        } else {
            this.setMaxPlayers(8);
        }
    }
}

