package org.cedacri.pingpong.service.tournament_round_robin;

import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.entity.TournamentRoundRobin;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.cedacri.pingpong.service.TournamentService;

import java.util.List;

public class TournamentRoundRobinService extends TournamentService {

    public TournamentRoundRobinService(BaseTournamentRepository tournamentRepository) {
        super(tournamentRepository);
    }

    public List<TournamentOlympic> findAllTournamentsOlympic() {
        List<BaseTournament> baseTournamentList = super.findAllTournamentsByType(TournamentTypeEnum.OLYMPIC);

        return baseTournamentList
                .stream()
                .filter(TournamentOlympic.class::isInstance)
                .map(TournamentOlympic.class::cast)
                .toList();
    }

    @Override
    public TournamentRoundRobin findTournamentById(Long id) {
        validateTournamentId(id);

        if (super.findTournamentById(id) instanceof TournamentRoundRobin tournamentRoundRobin) {
            return tournamentRoundRobin;
        } else {
            throw new IllegalArgumentException("Tournament with ID " + id + " is not a Round Robin tournament");
        }
    }

    public BaseTournament startTournament(BaseTournament tournament) {

        // Method will be written in the next commits
        throw new UnsupportedOperationException("Starting a tournament is not yet implemented for Olympic tournaments.");

    }

}
