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
import org.cedacri.pingpong.service.TournamentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
public class MatchGenerator
{

    private SetTypesEnum simpleRoundSets;
    private SetTypesEnum semifinalsRoundSets;
    private SetTypesEnum finalsRoundSets;
    private TournamentTypeEnum tournamentType;

    private final PlayerDistributer playerDistributer;

    private final TournamentService tournamentService;

    public MatchGenerator(SetTypesEnum simpleRoundSets, SetTypesEnum semifinalsRoundSets,
                          SetTypesEnum finalsRoundSets, TournamentTypeEnum tournamentType,
                          PlayerDistributer playerDistributer, TournamentService tournamentService)
    {
        this.simpleRoundSets = simpleRoundSets;
        this.semifinalsRoundSets = semifinalsRoundSets;
        this.finalsRoundSets = finalsRoundSets;
        this.tournamentType = tournamentType;
        this.playerDistributer = playerDistributer;
        this.tournamentService = tournamentService;
    }

    public void generateMatches(Tournament tournamentRef)
    {
        Tournament tournament = tournamentService.findTournamentById(tournamentRef.getId());

        switch (tournamentType)
        {
            case OLYMPIC -> generateOlympicTournament(tournament);

            case ROBIN_ROUND -> generateRobinRoundTournament(tournament);
            default ->
            {
                NotificationManager.showErrorNotification("Tournament type " + tournamentType + " not supported!");
                log.error("Tournament type {} not supported", tournamentType);
            }
        }

    }

    private void generateRobinRoundTournament(Tournament tournament)
    {
        List<Player> players = new ArrayList<>(tournament.getPlayers());
        int numPlayers = players.size();

        // Создаём список матчей для каждого игрока против каждого
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++)
        {
            for (int j = i + 1; j < numPlayers; j++)
            {
                Match match = createMatch(1, matches.size() + 1, null, tournament);
                matches.add(match);
            }
        }

        tournament.getMatches().addAll(matches);
        tournament.setTournamentStatus(TournamentStatusEnum.ONGOING);


        // Распределение игроков в отдельном методе PlayerDistributer
        playerDistributer.distributePlayersInRobinRound(matches, players);
        tournamentService.saveTournament(tournament);
    }

    private void generateOlympicTournament(Tournament tournament)
    {

        tournament.setTournamentStatus(TournamentStatusEnum.ONGOING);

        int maxPlayers = tournament.getMaxPlayers();
        int totalRounds = TournamentUtils.calculateNumberOfRounds(maxPlayers);


        generateOlympicMatches(tournament, totalRounds);
        playerDistributer.distributePlayersInFirstRound(maxPlayers, tournament);
        TournamentUtils.movePlayersWithoutOpponent(1, tournament);

        tournamentService.saveTournament(tournament);
    }

    private void generateOlympicMatches(Tournament tournament, int totalRounds)
    {
        int currentMatches = 1;
        int position = 1;
        for (int round = totalRounds; round > 0; round--)
        {
            if (round != totalRounds)
            {
                currentMatches *= 2;

                List<Match> currentRoundMatches = new ArrayList<>();

                for (int i = 0; i < currentMatches; i++)
                {
                    position++;

                    int currentRound = round;

                    List<Match> previousRoundMatches = tournament.getMatches().stream()
                            .filter(m -> m.getRound() == (currentRound + 1))
                            .toList();

                    int nextMatchIndex = position / 2;

                    Optional<Match> nextMatch = previousRoundMatches.stream().filter(m -> m.getPosition() == nextMatchIndex).findFirst();

                    if (nextMatch.isPresent())
                    {
                        Match match = createMatch(round, position, nextMatch.orElse(null), tournament);
                        currentRoundMatches.add(match);
                    }
                    else
                    {
                        log.warn("Failed to generate match: No match found for position {} in round {} of tournament {}",
                                nextMatchIndex, round, tournament.getTournamentName());

                    }
                }
                tournament.getMatches().addAll(currentRoundMatches);
            }
            else
            {
                Match finalMatch = createMatch(totalRounds, position, null, tournament);
                tournament.getMatches().add(finalMatch);
            }
        }
    }

    private Match createMatch(int round, int position, Match nextMatch, Tournament tournament)
    {
        return Match.builder()
                .round(round)
                .position(position)
                .nextMatch(nextMatch)
                .tournament(tournament)
                .build();

    }
}
