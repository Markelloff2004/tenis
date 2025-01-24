package org.cedacri.pingpong.utils;

import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class MatchGenerator {

    private SetTypesEnum simpleRoundSets;
    private SetTypesEnum semifinalsRoundSets;
    private SetTypesEnum finalsRoundSets;
    private TournamentTypeEnum tournamentType;

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    public MatchGenerator(SetTypesEnum simpleRoundSets, SetTypesEnum semifinalsRoundSets, SetTypesEnum finalsRoundSets, TournamentTypeEnum tournamentType) {
        this.simpleRoundSets = simpleRoundSets;
        this.semifinalsRoundSets = semifinalsRoundSets;
        this.finalsRoundSets = finalsRoundSets;
        this.tournamentType = tournamentType;
    }


    /***
     *
     * @param tournament - tournament for which matches should be generated
     * @return - list of generated matches
     *
     */
    public List<Match> generateMatches(Tournament tournament) {
        if (tournamentType == TournamentTypeEnum.OLIMPIC) {
            List<Player> sortedPlayers = new ArrayList<>(tournament.getPlayers());
            sortedPlayers.sort(Comparator.comparingInt(Player::getRating));

            return generateOlympicTournament(sortedPlayers, tournament);
        }
        logger.error("Tournament type {} not supported", tournamentType);
        throw new UnsupportedOperationException("Tournament type " + tournamentType + " not supported yet");
    }

    /*private List<Match> generateOlympicMatches(List<Player> sortedPlayers, Tournament tournament) {


//        int groupPosition = 1;
//        String roundName = getRoundName(maxPlayers);
//        for(int i = 0; i< maxPlayers; i +=2) {
//            Match match = createMatch(paddedPlayers.get(i), paddedPlayers.get(i+1), tournament, roundName, groupPosition);
//            matches.add(match);
//
//            if((i /2 + 1) % 2 == 0) {
//                groupPosition++;
//            }
//        }
        return null;
    }*/

    private List<Match> generateOlympicTournament(List<Player> sortedPlayers, Tournament tournament) {
        List<Match> allMatches = new ArrayList<>();
        int numPlayers = sortedPlayers.size();

        int maxPlayers = 1;
        while (maxPlayers < numPlayers) {
            maxPlayers *= 2;
        }

        int roundNumber = 1;
        int currentPlayers = maxPlayers;
        while (currentPlayers > 1) {
            generateRoundMatches(allMatches, tournament, roundNumber, currentPlayers);
            currentPlayers /= 2;
            roundNumber++;
        }
        //final round
        generateRoundMatches(allMatches, tournament, roundNumber, 1);

        //players distributing in first round
        distributePlayersToFirstRound(allMatches, sortedPlayers);

        processWinners(allMatches);

        return allMatches;
    }

    /***
     * empty matches generation for one round
     * @param allMatches represents all matches of tournament
     * @param tournament represents current working tournament
     * @param roundNumber represents round number
     * @param numMatches represents number of matches in current round
     */
    private void generateRoundMatches(List<Match> allMatches, Tournament tournament, int roundNumber, int numMatches) {
        int groupPosition = 1;
        String roundName = getRoundName(numMatches * 2);

        for(int i = 0; i < numMatches; i++) {
            Match match = createMatch(null, null, tournament, roundName, groupPosition);
            allMatches.add(match);
            if((i + 1) % 2 == 0) {
                groupPosition++;
            }
        }
    }

    /***
     * Distributes players in first round, filling the matches
     *
     * @param allMatches represents all tournament matches
     * @param sortedPlayers represents sorted by rating players
     */
    private void distributePlayersToFirstRound(List<Match> allMatches, List<Player> sortedPlayers) {
        int maxPlayers = sortedPlayers.size();
        List<Player> paddedPlayers = new ArrayList<>(sortedPlayers);

        //filling players list until their amount isn't a power of 2
        while(paddedPlayers.size() < maxPlayers) {
            paddedPlayers.add(null);
        }

        List<Match> firstRoundMatches = allMatches.stream()
                .filter(match -> "First Round".equals(match.getRound()))
                .toList();

        //distributing players through matches in first round
        for(int i = 0; i < firstRoundMatches.size(); i++) {
            Match match = firstRoundMatches.get(i);
            match.setTopPlayer(i * 2 < paddedPlayers.size() ? paddedPlayers.get(i * 2) : null);
            match.setBottomPlayer(i * 2 + 1 < paddedPlayers.size() ? paddedPlayers.get(i * 2 + 1) : null);

            //if match have only one player - set that player as winner
            if(match.getTopPlayer() != null && match.getBottomPlayer() == null) {
                match.setWinner(match.getTopPlayer());
            } else if(match.getTopPlayer() == null && match.getBottomPlayer() != null) {
                match.setWinner(match.getBottomPlayer());
            }
        }
    }

        /***
         * process winners of matches and distribute them through next rounds
         *
         * @param allMatches  represents all matches of working tournament
         */
        private void processWinners(List<Match> allMatches) {
            int roundNumber = 1;
            while (true) {
                int finalRoundNumber = roundNumber;
                List<Match> currentRoundMatches = allMatches.stream()
                        .filter(match -> match.getRound().equals(getRoundName((int) Math.pow(2, finalRoundNumber))))
                        .collect(Collectors.toList());

                List<Match> nextRoundMatches = allMatches.stream()
                        .filter(match -> match.getRound().equals(getRoundName((int) Math.pow(2, finalRoundNumber + 1))))
                        .collect(Collectors.toList());

                if (currentRoundMatches.isEmpty() || nextRoundMatches.isEmpty()) {
                    break;
                }

                int nextGroupPosition = 0;
                for (Match match : currentRoundMatches) {
                    Player winner = match.getWinner();
                    if (winner != null) {
                        Match nextMatch = nextRoundMatches.get(nextGroupPosition / 2);
                        if (nextGroupPosition % 2 == 0) {
                            nextMatch.setTopPlayer(winner);
                        } else {
                            nextMatch.setBottomPlayer(winner);
                        }
                        nextGroupPosition++;
                    }
                }

                roundNumber++;
            }
        }


    /***
     * @param playersInRound represents amount of players of current round
     * @return round name
     */
    private String getRoundName(int playersInRound) {
        return switch (playersInRound) {
            case 2 -> "Final";
            case 4 -> "Semifinal";
            case 8 -> "Quarterfinal";
            default -> "Round of " + playersInRound;
        };
    }

    private Match createMatch(Player topPlayer, Player bottomPlayer, Tournament tournament, String round, int position) {
    Match match = new Match();
    match.setTopPlayer(topPlayer);
    match.setBottomPlayer(bottomPlayer);
    match.setTournament(tournament);
    match.setRound(round);
    match.setPosition(position);
    match.setWinner(null);
    return match;
}

}
