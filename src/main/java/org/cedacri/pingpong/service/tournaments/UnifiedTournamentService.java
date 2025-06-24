package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnifiedTournamentService {

    private final BaseTournamentService defaultCrudService;
    private final TournamentOperationFactory operationFactory;

    public UnifiedTournamentService(@Qualifier("baseTournamentService") BaseTournamentService tournamentService,
                                    TournamentOperationFactory operationFactory) {
        this.defaultCrudService = tournamentService;
        this.operationFactory = operationFactory;
    }

    public BaseTournamentService getDefaultCrudService() {
        return defaultCrudService;
    }

    public BaseTournament startTournament(BaseTournament tournament) {
        ITournamentOperations operation = operationFactory.getOperation(tournament);
        tournament = operation.startTournament(tournament);
        tournament = operation.updateTournament(tournament);

        return tournament;
    }

    public void endTournament(BaseTournament tournament) {
        ITournamentOperations operation = operationFactory.getOperation(tournament);
        operation.endTournament(tournament);
    }

    public boolean isReadyToEndTournament(BaseTournament baseTournament) {
        ITournamentOperations operation = operationFactory.getOperation(baseTournament);
        return operation.allMatchesHasBeenPlayed(baseTournament);
    }
}
