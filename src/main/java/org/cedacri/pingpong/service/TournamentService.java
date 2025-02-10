package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.cedacri.pingpong.utils.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    private final MatchService matchService;

    public TournamentService(TournamentRepository tournamentRepository, MatchService matchService) {
        this.tournamentRepository = tournamentRepository;
        this.matchService = matchService;
    }

    @Transactional
    public Stream<Tournament> findAll() {
        List<Tournament> tournaments = tournamentRepository.findAll();

        return tournaments.stream().sorted(Comparator.comparing(Tournament::getCreatedAt).reversed());
    }

    public Tournament find(Integer id) {
        return tournamentRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament)  {
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteById(Integer id)
    {
        Tournament tournamentToDelete = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        for(Player player : tournamentToDelete.getPlayers()){
            player.getTournaments().remove(tournamentToDelete);
        }

        tournamentRepository.deleteById(id);
    }

    @Transactional
    public void startTournament(Tournament tournament) throws NotEnoughPlayersException {
        int playersCount = tournament.getPlayers().size();

        if(playersCount < 8) {
            NotificationManager.showErrorNotification(Constraints.NOT_ENOUGH_PLAYERS_MESSAGE);
            throw new NotEnoughPlayersException(playersCount);
        }

        MatchGenerator matchGenerator = new MatchGenerator(tournament.getSetsToWin(), tournament.getSemifinalsSetsToWin(),
                tournament.getFinalsSetsToWin(), tournament.getTournamentType(), new PlayerDistributer(), this, matchService);

        matchGenerator.generateMatches(tournament);
    }

    @Transactional
    public Tournament createAndSaveTournament(
            String name, TournamentTypeEnum type, SetTypesEnum setsToWin, SetTypesEnum semiSetsToWin, SetTypesEnum finalSetsToWin,
            Set<Player> players, boolean startNow
    ) throws NotEnoughPlayersException {
        Tournament tournament = new Tournament();
        tournament.setTournamentName(name);
        tournament.setTournamentType(type);
        tournament.setSetsToWin(setsToWin);
        tournament.setSemifinalsSetsToWin(semiSetsToWin);
        tournament.setFinalsSetsToWin(finalSetsToWin);

        return saveTournamentWithPlayers(tournament, players, startNow);
    }

    @Transactional
    public Tournament saveTournamentWithPlayers(Tournament tournament, Set<Player> players, boolean startNow) throws NotEnoughPlayersException {
        tournament.setPlayers(players);
        Tournament savedTournament = tournamentRepository.save(tournament);

        if (startNow) {
            startTournament(savedTournament);
        }

        return savedTournament;
    }

    @Transactional
    public Stream<Tournament> findTournamentsByStatus(TournamentStatusEnum status) {
        List<Tournament> tournaments = tournamentRepository.findAll()
                .stream()
                .filter(tournament -> tournament.getTournamentStatus()
                        .equals(status))
                .sorted(Comparator.comparing(Tournament::getCreatedAt).reversed())
                .toList();

        return tournaments.stream();
    }

    public Player getTournamentWinner(Tournament tournament) {
        return tournament.getMatches().stream()
                .filter(match -> match.getNextMatch() == null)
                .map(Match::getWinner)
                .findFirst()
                .orElse(null);
    }
}
