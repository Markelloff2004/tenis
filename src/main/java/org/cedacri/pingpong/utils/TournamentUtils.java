package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;

import java.util.ArrayList;
import java.util.List;

public class TournamentUtils {

    public static int calculateMaxPlayers(int num) {
        if (num < 1) {
            throw new IllegalArgumentException("Number of players must be at least 1.");
        }

        int maxPlayers = 1;
        while (maxPlayers < num) {
            maxPlayers *= 2;
        }

        return maxPlayers;
    }

    // This method returns the number of sets required to win a match based on the tournament type.
    // For example:
    // - BESTOFTHREE means a match is played as the best of 3 sets.
    // - BESTOFFIVE means a match is played as the best of 5 sets.
    // - BESTOFSEVEN means a match is played as the best of 7 sets.
    public static Integer getSetsCount(String type) {
        if (type == null || type.isEmpty())
        {
            throw new IllegalArgumentException("Tournament Type cannot be null or empty");
        }

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
            throw new IllegalArgumentException("Tournament cannot be null or have less than 8 players.");
        }

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

        return allRounds.get(currentRoundIndex + 1);
    }

    public static void generateTournamentMatches(MatchService matchService, Tournament tournament)
    {

        //Prepare players for tournament
        List<Player> players = new ArrayList<>(tournament.getPlayers());

        while (players.size() < tournament.getMaxPlayers())
        {
            players.add(null);
        }

        String initialRound = getRoundsCount(tournament.getMaxPlayers()).get(0);
//        int matchCount = getMatchCount(initialRound);
        int matchCount = tournament.getMaxPlayers() / 2;

        for (int position = 1; position <= matchCount; position++)
        {
            Match match = createMatch(tournament, initialRound, position, players);

            if ( match.getTopPlayer() == null )
            {
                match.setWinner(match.getBottomPlayer());
            }
            if ( match.getBottomPlayer() == null )
            {
                match.setWinner(match.getTopPlayer());
            }

            try
            {
                matchService.saveOrUpdateMatch(match);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            determinateWinner(matchService, match, tournament.getMaxPlayers());
        }
    }

    public static void determinateWinner(MatchService matchService, Match match, Integer maxPlayers)
    {
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

    }

    private static void moveWinnerToNextRound(MatchService matchService, Match match)
    {
        String nextRound = getNextRound(match.getRound());
        int nextPosition = match.getPosition()/2 + match.getPosition()%2;

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
        }

        matchService.saveOrUpdateMatch(nextRoundMatch);
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
                System.out.println("ERROR: Tie detected, no winner decided");
                return null;
            }

        }
        // Cannot find the winner
        return null;
    }

    private static Match createMatch(Tournament tournament, String round, int position, List<Player> players)
    {
        Match match = new Match();
        match.setTournament(tournament);
        match.setRound(round);
        match.setPosition(position);
        //TODO check handling la score - null

        match.setTopPlayer(players.get(position - 1));
        match.setBottomPlayer(players.get(tournament.getMaxPlayers() - position));

        return match;
    }
}
