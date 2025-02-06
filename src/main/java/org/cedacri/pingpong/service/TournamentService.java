package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
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

        return tournaments.stream();
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
    public Tournament saveTournamentWithPlayers(Tournament tournament, Set<Player> selectedPlayers, boolean startNow) throws NotEnoughPlayersException {

        tournament.setTournamentStatus(TournamentStatusEnum.PENDING);
        tournament.setMaxPlayers(TournamentUtils.determineMaxPlayers(selectedPlayers));

        tournament = saveTournament(tournament);

        for(Player player : selectedPlayers){
            player.getTournaments().add(tournament);
            tournament.getPlayers().add(player);
        }

        tournament = saveTournament(tournament);

        if(startNow) {
            startTournament(tournament);
        }

        return tournament;
    }
}
