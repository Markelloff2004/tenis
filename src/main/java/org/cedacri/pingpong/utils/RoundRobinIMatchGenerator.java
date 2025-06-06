package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.entity.TournamentRoundRobin;
import org.cedacri.pingpong.interfaces.IMatchGenerator;
import org.cedacri.pingpong.service.primary.TournamentService;

public class RoundRobinIMatchGenerator implements IMatchGenerator<TournamentRoundRobin> {

    private final PlayerDistributer playerDistributer;
    private final TournamentService tournamentService;

    public RoundRobinIMatchGenerator(PlayerDistributer playerDistributer, TournamentService tournamentService) {
        this.playerDistributer = playerDistributer;
        this.tournamentService = tournamentService;
    }

    @Override
    public void generateMatches(TournamentRoundRobin tournamentRef) {

    }

    @Override
    public void distributePlayersInMatches(TournamentOlympic tournament) {

    }

    @Override
    public void validateTournament(TournamentOlympic tournament) {

    }

    @Override
    public void generateMatchStructure(TournamentOlympic tournament, int totalRounds) {

    }
}
