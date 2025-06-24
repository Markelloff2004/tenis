package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.model.tournament.TournamentOlympic;
import org.cedacri.pingpong.model.tournament.TournamentRoundRobin;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TournamentOperationFactory {
    private final Map<Class<?>, ITournamentOperations> operations = new HashMap<>();

    public TournamentOperationFactory(
            TournamentOlympicService olympicOp,
            TournamentRoundRobinService roundRobinOp) {
        operations.put(TournamentOlympic.class, olympicOp);
        operations.put(TournamentRoundRobin.class, roundRobinOp);

        // Add more tournament types and their corresponding operations as needed
        // operations.put(AnotherTournamentType.class, anotherTournamentService);
    }

    public ITournamentOperations getOperation(BaseTournament tournament) {
        return operations.get(tournament.getClass());
    }
}
