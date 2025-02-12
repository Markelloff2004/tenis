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
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final MatchService matchService;
    private final PlayerService playerService;

    public TournamentService(TournamentRepository tournamentRepository, MatchService matchService, PlayerService playerService) {
        this.tournamentRepository = tournamentRepository;
        this.matchService = matchService;
        this.playerService = playerService;
    }

    @Transactional
    public Stream<Tournament> findAll() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        tournaments.forEach(tournament -> Hibernate.initialize(tournament.getPlayers()));
        return tournaments.stream().sorted(Comparator.comparing(Tournament::getCreatedAt).reversed());
    }

    public Tournament find(Integer id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow();
        Hibernate.initialize(tournament.getPlayers());
        return tournament;
    }

    @Transactional
    public void saveTournament(Tournament tournament) {
        tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteById(Integer id) {
        Tournament tournamentToDelete = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        tournamentToDelete.getPlayers().forEach(player -> player.getTournaments().remove(tournamentToDelete));
        tournamentRepository.deleteById(id);
    }

    @Transactional
    public void startTournament(Tournament tournament) throws NotEnoughPlayersException {
        if (tournament.getPlayers().size() < 8) {
            NotificationManager.showErrorNotification(Constants.NOT_ENOUGH_PLAYERS_MESSAGE);
            throw new NotEnoughPlayersException(tournament.getPlayers().size());
        }

        MatchGenerator matchGenerator = new MatchGenerator(tournament.getSetsToWin(), tournament.getSemifinalsSetsToWin(),
                tournament.getFinalsSetsToWin(), tournament.getTournamentType(), new PlayerDistributer(), this, matchService);

        matchGenerator.generateMatches(tournament);
    }

    @Transactional
    public Tournament createAndSaveTournament(
            String name, TournamentTypeEnum type, SetTypesEnum setsToWin, SetTypesEnum semiSetsToWin, SetTypesEnum finalSetsToWin,
            Set<Player> players, boolean startNow) throws NotEnoughPlayersException {
        Tournament tournament = new Tournament();
        setTournamentParams(tournament, name, type, setsToWin, semiSetsToWin, finalSetsToWin, players);
        return saveTournamentWithPlayers(tournament, new ArrayList<>(players), startNow);
    }

    @Transactional
    public Tournament saveTournamentWithPlayers(Tournament tournament, List<Player> players, boolean startNow) throws NotEnoughPlayersException {
        Tournament managedTournament = tournament.getId() != null ?
                tournamentRepository.findById(tournament.getId()).orElseThrow(() -> new EntityNotFoundException("Tournament not found")) :
                tournament;

        Set<Player> managedPlayers = new HashSet<>();
        for (Player player : players) {
            Player managedPlayer = playerService.findById(player.getId());
            managedPlayer.getTournaments().add(managedTournament);
            managedPlayers.add(managedPlayer);
        }
        if(startNow) { managedTournament.setTournamentStatus(TournamentStatusEnum.ONGOING); }
        managedTournament.setPlayers(managedPlayers);
        Tournament savedTournament = tournamentRepository.save(managedTournament);

        if (startNow) {
            startTournament(savedTournament);
        }

        return savedTournament;
    }

    @Transactional
    public Stream<Tournament> findTournamentsByStatus(TournamentStatusEnum status) {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return tournaments.stream()
                .filter(tournament -> tournament.getTournamentStatus().equals(status))
                .sorted(Comparator.comparing(Tournament::getCreatedAt).reversed());
    }

    public Player getTournamentWinner(Tournament tournament) {
        return tournament.getWinner();
    }

    @Transactional
    public Tournament updateTournament(Tournament tournament, String name, TournamentTypeEnum tournamentType,
                                       SetTypesEnum setsToWin, SetTypesEnum semiSetsToWin, SetTypesEnum finalSetsToWin,
                                       Set<Player> players, boolean startNow) throws NotEnoughPlayersException {
        Tournament managedTournament = find(tournament.getId());
        setTournamentParams(managedTournament, name, tournamentType, setsToWin, semiSetsToWin, finalSetsToWin, players);
        return saveTournamentWithPlayers(managedTournament, new ArrayList<>(players), startNow);
    }

    private void setTournamentParams(Tournament tournament, String name, TournamentTypeEnum tournamentType, SetTypesEnum setsToWin, SetTypesEnum semiSetsToWin, SetTypesEnum finalSetsToWin, Set<Player> players) {
        tournament.setTournamentName(name);
        tournament.setTournamentType(tournamentType);
        tournament.setSetsToWin(setsToWin);
        tournament.setSemifinalsSetsToWin(semiSetsToWin);
        tournament.setFinalsSetsToWin(finalSetsToWin);
        tournament.setTournamentStatus(TournamentStatusEnum.PENDING);
        tournament.setMaxPlayers(TournamentUtils.determineMaxPlayers(players));
        tournament.setPlayers(players);
    }
}
