package org.cedacri.pingpong.repository;

import jakarta.validation.constraints.NotNull;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.model.enums.TournamentTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaseTournamentRepository extends JpaRepository<BaseTournament, Long> {

    List<BaseTournament> findAllByTournamentType(
            @NotNull(message = "Tournament type cannot be null")
            TournamentTypeEnum tournamentType
    );

    List<BaseTournament> findAllByTournamentStatus(
            @NotNull(message = "Tournament status cannot be null")
            TournamentTypeEnum tournamentStatus
    );

    List<BaseTournament> findAllByTournamentTypeAndTournamentStatus(
            @NotNull(message = "Tournament type cannot be null")
            TournamentTypeEnum tournamentType,
            @NotNull(message = "Tournament status cannot be null")
            TournamentTypeEnum tournamentStatus
    );


}
