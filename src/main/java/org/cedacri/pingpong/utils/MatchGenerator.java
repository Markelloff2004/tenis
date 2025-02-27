package org.cedacri.pingpong.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;

import java.util.*;

@Slf4j
@Getter
@Setter
public class MatchGenerator {

    private SetTypesEnum simpleRoundSets;
    private SetTypesEnum semifinalsRoundSets;
    private SetTypesEnum finalsRoundSets;
    private TournamentTypeEnum tournamentType;

    private final PlayerDistributer playerDistributer;

    private final TournamentService tournamentService;
    private final MatchService matchService;

    public MatchGenerator(SetTypesEnum simpleRoundSets, SetTypesEnum semifinalsRoundSets,
                          SetTypesEnum finalsRoundSets, TournamentTypeEnum tournamentType,
                          PlayerDistributer playerDistributer, TournamentService tournamentService, MatchService matchService) {
        this.simpleRoundSets = simpleRoundSets;
        this.semifinalsRoundSets = semifinalsRoundSets;
        this.finalsRoundSets = finalsRoundSets;
        this.tournamentType = tournamentType;
        this.playerDistributer = playerDistributer;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    public void generateMatches(Tournament tournamentRef) {
        Tournament tournament = tournamentService.find(tournamentRef.getId());

        switch(tournamentType){
            case OLYMPIC -> {
                List<Player> sortedPlayers = new ArrayList<>(tournament.getPlayers());
                sortedPlayers.sort(Comparator.comparingInt(Player::getRating).reversed());

                generateOlympicTournament(tournament);
            }
            case ROBIN_ROUND -> generateRobinRoundTournament(tournament);
            default -> {
                NotificationManager.showErrorNotification("Tournament type " + tournamentType + " not supported!");
                log.error("Tournament type {} not supported", tournamentType);
            }
        }

    }

    private void generateRobinRoundTournament(Tournament tournament) {
        List<Player> players = new ArrayList<>(tournament.getPlayers());
        int numPlayers = players.size();

        // Создаём список матчей для каждого игрока против каждого
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            for (int j = i + 1; j < numPlayers; j++) {
                Match match = createMatch(1, matches.size() + 1, null, tournament);
                matches.add(match);
            }
        }

        tournament.getMatches().addAll(matches);

        // Распределение игроков в отдельном методе PlayerDistributer
        playerDistributer.distributePlayersInRobinRound(matches, players);
    }

    private void generateOlympicTournament(Tournament tournament) {

        tournament.setTournamentStatus(TournamentStatusEnum.ONGOING);
        tournamentService.saveTournament(tournament);

        int numPlayers = tournament.getPlayers().size();
        int maxPlayers = calculateMaxPlayers(numPlayers);
        int totalRounds = TournamentUtils.calculateNumberOfRounds(maxPlayers);


        generatePreviousRounds(tournament, totalRounds);
        playerDistributer.distributePlayersInFirstRound(maxPlayers, tournament);
        TournamentUtils.updateWinnersAfterRound(1, tournament);

    }

    private void generatePreviousRounds(Tournament tournament, int totalRounds) {
        int currentMatches = 1;
        for (int round = totalRounds; round > 0; round--) {
            if(round != totalRounds) {
                currentMatches *= 2;

                List<Match> currentRoundMatches = new ArrayList<>();

                for (int i = 0; i < currentMatches; i++) {
                    int position = i + 1;

                    Match nextMatch;

                    int currentRound = round;
                    List<Match> previousRoundMatches = tournament.getMatches().stream()
                            .filter(m -> m.getRound() == (currentRound + 1))
                            .toList();

                        int nextMatchIndex = (((i + 1) / 2) + ((i + 1) % 2));
                        nextMatch = previousRoundMatches.stream().filter(m -> m.getPosition() == nextMatchIndex).findFirst().get();

                    Match match = createMatch(round, position, nextMatch, tournament);
                    currentRoundMatches.add(match);
                }
                tournament.getMatches().addAll(currentRoundMatches);
            }
            else {
                Match finalMatch = createMatch(totalRounds, 1, null, tournament);
                    tournament.getMatches().add(finalMatch);
            }
        }
    }

    private int calculateMaxPlayers(int numPlayers) {
        int maxPlayers = 1;
        while (maxPlayers < numPlayers) {
            maxPlayers *= 2;
        }
        return maxPlayers;
    }

    private Match createMatch(int round, int position, Match nextMatch, Tournament tournament) {
        return Match.builder()
                .round(round)
                .position(position)
                .nextMatch(nextMatch)
                .tournament(tournament)
                .build();

    }
}
