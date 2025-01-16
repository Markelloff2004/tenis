package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TournamentUtils {

    private static final Logger logger = LoggerFactory.getLogger(TournamentUtils.class);

    public static int calculateMaxPlayers(int num) {

        logger.debug("calculateMaxPlayers called with arg: {}", num);

        if (num < 1) {
            logger.error("Invalid number of players: {}", num);
            throw new IllegalArgumentException("Number of players must be at least 1.");
        }

        int maxPlayers = 1;
        while (maxPlayers < num) {
            maxPlayers *= 2;
        }

        logger.debug("Calculated max players: {}", maxPlayers);
        return maxPlayers;
    }

    // This method returns the number of sets required to win a match based on the tournament type.
    // - BESTOFTHREE means a match is played as the best of 3 sets.
    // - BESTOFFIVE means a match is played as the best of 5 sets.
    // - BESTOFSEVEN means a match is played as the best of 7 sets.
    public static Integer getSetsCount(String type) {
        if (type == null || type.isEmpty())
        {
            logger.error("Tournament type cannot be null or empty");
            throw new IllegalArgumentException("Tournament Type cannot be null or empty");
        }

        logger.debug("getSetsCount called with type: {}", type);

        return switch (type.toUpperCase()) {
            case "BESTOFTHREE" -> 3;
            case "BESTOFFIVE" -> 5;
            case "BESTOFSEVEN" -> 7;
            default -> 1;
        };
    }

    // This method is intended to return the number of rounds (sets played) in a match.
    // Rounds are individual sets within a match, such as 11-3; 4-11; 11-8 (example for BESTOFTHREE).
    public static List<String> getRoundsCount(Integer players) {
        if (players == null || players < 4)
        {
            logger.error("Invalid number of players: {}", players);
            throw new IllegalArgumentException("Tournament cannot be null or have less than 8 players.");
        }

        logger.debug("getRoundsCount called with players: {}", players);

        return switch (players) {
            case 8 -> List.of("Quarterfinals", "Semifinals", "Final");
            case 16 -> List.of("Stage 1", "Quarterfinals", "Semifinals", "Final");
            case 32 -> List.of("Stage 2", "Stage 1", "Quarterfinals", "Semifinals", "Final");
            case 64 -> List.of("Stage 3", "Stage 2", "Stage 1", "Quarterfinals", "Semifinals", "Final");
            default -> List.of("Semifinals", "Final");
        };
    }

    public static String getNextRound(String currentRound)
    {
        List<String> allRounds = List.of("Stage 3", "Stage 2", "Stage 1", "Quarterfinals", "Semifinals", "Final");

        int currentRoundIndex = allRounds.indexOf(currentRound);

        logger.debug("getNextRound called with currentRound: {}, returning: {}", currentRoundIndex, allRounds.get(currentRoundIndex + 1));

        return allRounds.get(currentRoundIndex + 1);
    }

    public static void generateTournamentMatches(MatchService matchService, Tournament tournament)
    {
        logger.info("Generating tournament matches for tournament: {}", tournament);

        //Prepare players for tournament
        List<Player> players = new ArrayList<>(tournament.getPlayers());

        while (players.size() < tournament.getMaxPlayers())
        {
            players.add(null);
        }

        String initialRound = getRoundsCount(tournament.getMaxPlayers()).get(0);
        int matchCount = tournament.getMaxPlayers() / 2;

        logger.debug("Generating {} matches for round: {}", matchCount, initialRound);

        for (int position = 1; position <= matchCount; position++)
        {
            Match match = createMatch(tournament, initialRound, position, players);

            if ( match.getTopPlayer() == null )
            {
                match.setWinner(match.getBottomPlayer());
                logger.debug("Match {}: No top player, winner set to bottom player", match.getId());
            }
            if ( match.getBottomPlayer() == null )
            {
                match.setWinner(match.getTopPlayer());
                logger.debug("Match {}: No bottom player, winner set to top player", match.getId());
            }

            try
            {
                matchService.saveOrUpdateMatch(match);
                logger.info("Match {} saved or updated successfully", match.getId());
            }
            catch (Exception e)
            {
                logger.error("Error saving or updating match {}: {}", match.getId(), e.getMessage());
                System.out.println(e.getMessage());
            }

            determinateWinner(matchService, match, tournament.getMaxPlayers());
        }
    }

    public static void determinateWinner(MatchService matchService, Match match, Integer maxPlayers)
    {
        logger.debug("Determining winner for match: {} in round {}", match.getId(), match.getRound());
        if ( getRoundsCount(maxPlayers).indexOf(match.getRound()) == 0 )
        {
            if (match.getTopPlayer() == null && match.getBottomPlayer() != null)
            {
                match.setWinner(match.getBottomPlayer());
            }
            else if (match.getBottomPlayer() == null && match.getTopPlayer() != null)
            {
                match.setWinner(match.getTopPlayer());
            }
            else
            {
                match.setWinner(calculateWinnerFromScore(match));
            }
        }
        else
        {
            match.setWinner(calculateWinnerFromScore(match));
        }

        if (match.getWinner() != null)
        {
            moveWinnerToNextRound(matchService, match);
        }
        else {
            logger.error("Match {} ended in a tie, no winner determined", match.getId());
        }

    }

    private static void moveWinnerToNextRound(MatchService matchService, Match match)
    {
        String nextRound = getNextRound(match.getRound());
        int nextPosition = match.getPosition()/2 + match.getPosition()%2;

        logger.debug("Moving winner of match {} to next round: {}", match.getId(), nextRound);

        Match nextRoundMatch = matchService.getMatchByTournamentRoundAndPosition(match.getTournament(), nextRound, nextPosition).orElse(null);

        if (nextRoundMatch != null)
        {
            if (nextRoundMatch.getTopPlayer() == null)
            {
                nextRoundMatch.setTopPlayer(match.getWinner());
            }

            if (nextRoundMatch.getBottomPlayer() == null)
            {
                nextRoundMatch.setBottomPlayer(match.getWinner());
            }

            logger.debug("Updated next round match: {}", nextRoundMatch);

        }
        else
        {
            nextRoundMatch = new Match();
            nextRoundMatch.setTournament(match.getTournament());
            nextRoundMatch.setRound(nextRound);
            nextRoundMatch.setPosition(nextPosition);

            if ( match.getPosition() % 2 == 1)
            {
                nextRoundMatch.setTopPlayer(match.getWinner());
            }
            else
            {
                nextRoundMatch.setBottomPlayer(match.getWinner());
            }

            logger.debug("Created new next round match: {}", nextRoundMatch);
        }

        matchService.saveOrUpdateMatch(nextRoundMatch);
        logger.info("Next round match saved or updated successfully: {}", nextRoundMatch);
    }

    private static Player calculateWinnerFromScore(Match match)
    {
        String score = match.getScore();
        if (score != null && !score.isEmpty())
        {
            String[] sets = score.split(";");
            int topPlayerWins = 0;
            int bottomPlayerWins = 0;

            for (String set : sets) {
                if (!set.equals("-:-")) {
                    String[] points = set.split(":");
                    int topPoints = Integer.parseInt(points[0].trim());
                    int bottomPoints = Integer.parseInt(points[1].trim());

                    if (topPoints > bottomPoints) topPlayerWins++;
                    else if (bottomPoints > topPoints) bottomPlayerWins++;
                }
            }

            if (topPlayerWins > bottomPlayerWins) {
                return match.getTopPlayer();
            } else if (bottomPlayerWins > topPlayerWins) {
                return match.getBottomPlayer();
            } else {
                logger.error("ERROR: Tie detected, no winner decided for match {}", match);
                return null;
            }
        }
        // Cannot find the winner
        logger.error("ERROR: Score not available for match {}", match);
        return null;
    }

    private static Match createMatch(Tournament tournament, String round, int position, List<Player> players)
    {
        logger.debug("Creating match for tournament {} in round {} at position {}", tournament, round, position);
        Match match = new Match();
        match.setTournament(tournament);
        match.setRound(round);
        match.setPosition(position);
        //TODO check handling la score - null

        match.setTopPlayer(players.get(position - 1));
        match.setBottomPlayer(players.get(tournament.getMaxPlayers() - position));

        logger.debug("Match created: {}", match);
        return match;
    }
}
