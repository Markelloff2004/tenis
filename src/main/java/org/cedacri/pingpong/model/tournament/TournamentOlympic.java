package org.cedacri.pingpong.model.tournament;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.model.enums.SetsTypesEnum;

@Getter
@Setter
@Entity
@Table(name = "tournaments_olympic")
public class TournamentOlympic extends BaseTournament // implements ITournament
{

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament semifinals sets cannot be null")
    private SetsTypesEnum semifinalsSetsToWin;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament final sets cannot be null")
    private SetsTypesEnum finalsSetsToWin;
}

