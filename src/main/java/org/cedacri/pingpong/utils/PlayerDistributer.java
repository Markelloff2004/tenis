package org.cedacri.pingpong.utils;

import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class PlayerDistributer
{


    /***
     * Distributes players into pairs based on seeding logic.
     *
     * @param tournamentOlympic - represents tournament
     * @param maxPlayers - maximum number of players (nearest power of 2)
     * @return list of player pairs for matches
     */
    public List<Player[]> distributePlayersIntoOlympicPairs(TournamentOlympic tournamentOlympic, int maxPlayers)
    {
        log.info("Ordering players by rating in {} tournament", tournamentOlympic);
        List<Player[]> pairs = new ArrayList<>();
        List<Player> paddedPlayers =
                new ArrayList<>(tournamentOlympic.getPlayers().stream().sorted(Comparator.comparingInt(Player::getRating).reversed()).toList());

        while (paddedPlayers.size() < maxPlayers)
        {
            paddedPlayers.add(null);
        }

        log.info("Grouping players into pairs ");
        for (int i = 0; i < maxPlayers / 2; i++)
        {
            Player topPlayer = paddedPlayers.get(i);
            Player bottomPlayer = paddedPlayers.get(maxPlayers - i - 1);

            if (topPlayer == null && bottomPlayer == null)
            {
                log.warn("Skipping empty match pair: Index {}", i);
                continue;
            }

            pairs.add(new Player[]{topPlayer, bottomPlayer});
            log.info("Generated new pair: TopPlayer={}, BottomPlayer={}",
                    topPlayer != null ? topPlayer.getName() + " " + topPlayer.getSurname() : "null",
                    bottomPlayer != null ? bottomPlayer.getName() + " " + bottomPlayer.getSurname() : "null");
        }

        return pairs;
    }

    /**
     * Creates the first round of matches based on player pairs.
     *
     * @param maxPlayers - maximum number of players (nearest power of 2)
     * @param tournamentOlympic - tournament
     */
    public void distributePlayersInFirstRound(int maxPlayers, TournamentOlympic tournamentOlympic)
    {
        List<Player[]> pairs = distributePlayersIntoOlympicPairs(tournamentOlympic, maxPlayers);

        List<Match> firstRoundMatches = tournamentOlympic.getMatches().stream()
                .filter(m -> m.getRound() == 1)
                .sorted(Comparator.comparingInt(Match::getPosition).reversed())
                .toList();

        if (firstRoundMatches.size() != pairs.size())
        {
            log.error("Error while trying distributing pairs of players through first round matches: Amount of first round matches does not match with number of pairs!");
            throw new IllegalStateException("Amount of first round matches does not match with number of pairs!");
        }

        int[] positions = Constants.OLYMPIC_POSITIONS.get(maxPlayers);
        if (positions == null)
        {
            log.error("Error while trying distributing pairs of players through first round matches: Unsupported number of players {}", maxPlayers);
            throw new IllegalArgumentException("Unsupported number of players: " + maxPlayers);
        }

        for (int i = 0; i < firstRoundMatches.size(); i++)
        {
            int index = positions[i] - 1;

            Match match = firstRoundMatches.get(i);
            Player[] pair = pairs.get(index);

            match.setTopPlayer(pair[0]);
            match.setBottomPlayer(pair[1]);

            if (pair[0] != null && pair[1] == null)
            {
                match.setWinner(pair[0]);
            }
            else if (pair[0] == null && pair[1] != null)
            {
                match.setWinner(pair[1]);
            }
        }
    }

    public void distributePlayersInRobinRound(List<Match> matches, List<Player> players)
    {
        int numPlayers = players.size();

        // in caz de numar de jucatori impar - suplinim cu unul null pentru a avea posibilitatea de a forma perechile
        //  fara de careva erori

        if (numPlayers % 2 != 0)
        {
            players.add(null);
            numPlayers++;
        }

        int numRounds = numPlayers - 1;
        int matchIndex = 0;

        List<Player> rotatingPlayers = new ArrayList<>(players);

        for (int round = 1; round <= numRounds; round++)
        {
            int halfSize = numPlayers / 2;

            for (int i = 0; i < halfSize; i++)
            {
                Player player1 = rotatingPlayers.get(i);
                Player player2 = rotatingPlayers.get(numPlayers - 1 -i);

                if(player1 != null && player2 != null)
                {
                    Match match = matches.get(matchIndex++);
                    match.setTopPlayer(player1);
                    match.setBottomPlayer(player2);
                    match.setRound(round);
                }
            }

            List<Player> newRotatingPlayers = new ArrayList<>();

            newRotatingPlayers.add(rotatingPlayers.get(0));
            newRotatingPlayers.add(rotatingPlayers.get(numPlayers - 1));

            newRotatingPlayers.addAll(rotatingPlayers.subList(1, numPlayers - 1));
            rotatingPlayers = newRotatingPlayers;
        }
    }
}
