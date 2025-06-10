package org.cedacri.pingpong.service.tournament_olympic;

import jakarta.transaction.Transactional;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.repository.BaseTournamentRepository;
import org.cedacri.pingpong.service.TournamentService;

import java.util.List;

public class TournamentOlympicService extends TournamentService implements ITournamentOlympicServiceInterface  {

    public TournamentOlympicService(BaseTournamentRepository baseTournamentRepository) {
        super(baseTournamentRepository);
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
    public TournamentOlympic findTournamentById(Long id) {
        validateTournamentId(id);

        if (super.findTournamentById(id) instanceof TournamentOlympic tournamentOlympic) {
            return tournamentOlympic;
        } else {
            throw new IllegalArgumentException("Tournament with ID " + id + " is not an Olympic tournament");
        }
    }



    @Transactional
    @Override
    public BaseTournament startTournament(BaseTournament tournamentOlympic){

        // Method will be written in the next commits
        throw new UnsupportedOperationException("Starting a tournament is not yet implemented for Olympic tournaments.");

    }




}
