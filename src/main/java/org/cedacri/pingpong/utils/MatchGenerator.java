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
import java.util.List;


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
     * @param sortedPlayers - list of players sorted by their rating
     * @param tournament - tournament for which matches should be generated
     * @return - list of generated matches
     *
     */
    public List<Match> generateMatches(List<Player> sortedPlayers, Tournament tournament) {
        if (tournamentType == TournamentTypeEnum.OLIMPIC) {
            return generateOlympicMatches(sortedPlayers, tournament);
        }
        logger.error("Tournament type {} not supported", tournamentType);
        throw new UnsupportedOperationException("Tournament type " + tournamentType + " not supported yet");
    }

    private List<Match> generateOlympicMatches(List<Player> sortedPlayers, Tournament tournament) {
        List<Match> matches = new ArrayList<>();
        int numPlayers = sortedPlayers.size();

        int maxPlayers = 1;
        while(maxPlayers < numPlayers) {
            maxPlayers *= 2;
        }

        List<Player> paddedPlayers = new ArrayList<>(sortedPlayers);
        while(paddedPlayers.size() < maxPlayers) {
            paddedPlayers.add(null);
        }

        int groupPosition = 1;
        String roundName = getRoundName(maxPlayers);
        for(int i = 0; i< maxPlayers; i +=2) {
            Match match = createMatch(paddedPlayers.get(i), paddedPlayers.get(i+1), tournament, roundName, groupPosition);
            matches.add(match);

            if((i /2 + 1) % 2 == 0) {
                groupPosition++;
            }
        }

        return matches;
    }

    /***
     * Creates a new match
     * @param topPlayer represents at top player
     * @param bottomPlayer represents at bottom player
     * @param tournament represents tournament
     * @param round represents round name (Stage 1, Semifinals, Finals...)
     * @param groupPosition represents group number of round
     * @return match object
     */
    private Match createMatch(Player topPlayer, Player bottomPlayer, Tournament tournament, String round, int groupPosition) {
        Match match = new Match();
        match.setTopPlayer(topPlayer);
        match.setBottomPlayer(bottomPlayer);
        match.setTournament(tournament);
        match.setRound(round);
        match.setPosition(groupPosition);
        return match;
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

}
