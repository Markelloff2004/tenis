package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.enums.TournamentTypeEnum;
import org.cedacri.pingpong.model.tournament.TournamentOlympic;
import org.cedacri.pingpong.model.tournament.TournamentRoundRobin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TournamentServiceFactory {

    private final Map<TournamentTypeEnum, BaseTournamentService<?>> serviceMap;

    public TournamentServiceFactory(
            @Qualifier("tournamentOlympicService") BaseTournamentService<TournamentOlympic> olympicService,
            @Qualifier("tournamentRoundRobinService") BaseTournamentService<TournamentRoundRobin> roundRobinService
    ) {
        serviceMap = Map.of(
                TournamentTypeEnum.OLYMPIC, olympicService,
                TournamentTypeEnum.ROBIN_ROUND, roundRobinService
        );
    }

    public BaseTournamentService<?> getDefaultService(TournamentTypeEnum tournamentType) {
        return serviceMap.get(tournamentType);
    }

    // Overloaded method to get the Olympic tournament service by default
    public BaseTournamentService<?> getDefaultService() {
        return serviceMap.get(TournamentTypeEnum.OLYMPIC);
    }

}
