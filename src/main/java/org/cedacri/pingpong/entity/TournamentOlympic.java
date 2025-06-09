package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.interfaces.ITournament;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tournaments_olympic")
public class TournamentOlympic extends BaseTournament // implements ITournament
{

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament semifinals sets cannot be null")
    private SetTypesEnum semifinalsSetsToWin;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tournament final sets cannot be null")
    private SetTypesEnum finalsSetsToWin;
}

