package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.interfaces.ITournamentOlympic;

import java.io.Serializable;

@Entity
@Table(name = "tournaments_olympic")
@Getter
@Setter
public class TournamentOlympic extends Tournament implements ITournamentOlympic, Serializable {

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament semifinals sets cannot be null")
    private SetTypesEnum semifinalsSetsToWin;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament final sets cannot be null")
    private SetTypesEnum finalsSetsToWin;

    @Override
    public SetTypesEnum getSemifinalsSetsToWin() {
        return semifinalsSetsToWin;
    }

    @Override
    public SetTypesEnum getFinalsSetsToWin() {
        return finalsSetsToWin;
    }
}
