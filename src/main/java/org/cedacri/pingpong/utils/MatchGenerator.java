package org.cedacri.pingpong.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;
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

    public void generateMatches(TournamentOlympic tournamentOlympicRef)
    {
        TournamentOlympic tournamentOlympic = tournamentService.findTournamentById(tournamentOlympicRef.getId());

        switch (tournamentType)
        {
            case OLYMPIC -> generateOlympicTournament(tournamentOlympic);

            case ROBIN_ROUND -> generateRobinRoundTournament(tournamentOlympic);
            default ->
            {
                NotificationManager.showErrorNotification("Tournament type " + tournamentType + " not supported!");
                log.error("Tournament type {} not supported", tournamentType);
            }
        }

    }

    private void generateRobinRoundTournament(TournamentOlympic tournamentOlympic)
    {
        List<Player> players = new ArrayList<>(tournamentOlympic.getPlayers());
        int numPlayers = players.size();

        int numRounds = (numPlayers % 2 == 0) ? numPlayers - 1 : numPlayers;
        int matchesPerRound = numPlayers / 2;
        int totalMatches = numRounds * matchesPerRound;

        // Создаём список матчей для каждого игрока против каждого
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < totalMatches; i++)
        {
            Match match = createMatch(0, i+1, null, tournamentOlympic);
            matches.add(match);
        }

        tournamentOlympic.getMatches().addAll(matches);
        tournamentOlympic.setTournamentStatus(TournamentStatusEnum.ONGOING);

        // Распределение игроков в отдельном методе PlayerDistributer
        playerDistributer.distributePlayersInRobinRound(matches, players);

        tournamentService.saveTournament(tournamentOlympic);
    }

    private void generateOlympicTournament(TournamentOlympic tournamentOlympic)
    {

        tournamentOlympic.setTournamentStatus(TournamentStatusEnum.ONGOING);

        int maxPlayers = tournamentOlympic.getMaxPlayers();
        int totalRounds = TournamentUtils.calculateNumberOfRounds(maxPlayers);


        generateOlympicMatches(tournamentOlympic, totalRounds);
        playerDistributer.distributePlayersInFirstRound(maxPlayers, tournamentOlympic);
        TournamentUtils.movePlayersWithoutOpponent(1, tournamentOlympic);

        tournamentService.saveTournament(tournamentOlympic);
    }

    private void generateOlympicMatches(TournamentOlympic tournamentOlympic, int totalRounds)
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

                    List<Match> previousRoundMatches = tournamentOlympic.getMatches().stream()
                            .filter(m -> m.getRound() == (currentRound + 1))
                            .toList();

                    int nextMatchIndex = position / 2;

                    Optional<Match> nextMatch = previousRoundMatches.stream().filter(m -> m.getPosition() == nextMatchIndex).findFirst();

                    if (nextMatch.isPresent())
                    {
                        Match match = createMatch(round, position, nextMatch.orElse(null), tournamentOlympic);
                        currentRoundMatches.add(match);
                    }
                    else
                    {
                        log.warn("Failed to generate match: No match found for position {} in round {} of tournament {}",
                                nextMatchIndex, round, tournamentOlympic.getTournamentName());

                    }
                }
                tournamentOlympic.getMatches().addAll(currentRoundMatches);
            }
            else
            {
                Match finalMatch = createMatch(totalRounds, position, null, tournamentOlympic);
                tournamentOlympic.getMatches().add(finalMatch);
            }
        }
    }

    private Match createMatch(int round, int position, Match nextMatch, TournamentOlympic tournamentOlympic)
    {
        return Match.builder()
                .round(round)
                .position(position)
                .nextMatch(nextMatch)
                .tournament(tournamentOlympic)
                .build();

    }
}
