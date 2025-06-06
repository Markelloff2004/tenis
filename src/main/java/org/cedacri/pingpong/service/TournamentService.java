package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
import org.cedacri.pingpong.interfaces.ITournament;
import org.cedacri.pingpong.repository.TournamentRepository;
import org.cedacri.pingpong.utils.MatchGenerator;
import org.cedacri.pingpong.utils.PlayerDistributer;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TournamentService
{
    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository)
    {
        this.tournamentRepository = tournamentRepository;
    }

    @Transactional
    public Stream<Tournament> findAllTournaments()
    {
        List<Tournament> tournaments = tournamentRepository.findAll();
        tournaments.forEach(tournament -> Hibernate.initialize(tournament.getPlayers()));
        return tournaments.stream().sorted(Comparator.comparing(Tournament::getCreatedAt).reversed());
    }

    public Tournament findTournamentById(Integer id)
    {
        validateTournamentId(id);

        Tournament tournament = tournamentRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + id + " not found"));

        Hibernate.initialize(tournament.getPlayers());
        return tournament;
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament)
    {
        if (tournament == null)
        {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteTournamentById(Integer id)
    {
        validateTournamentId(id);
        Tournament tournamentToDelete = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        tournamentToDelete.getPlayers().forEach(player -> player.getTournaments().remove(tournamentToDelete));
        tournamentRepository.deleteById(id);
    }

    private static void validateTournamentId(Integer id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }
        else if (id <= 0)
        {
            throw new IllegalArgumentException("Tournament ID cannot be 0 (zero) or bellow");
        }
    }

    @Transactional
    public void startTournament(Tournament tournament) throws NotEnoughPlayersException
    {

        int minAmountPlayers = TournamentUtils.getMinimalPlayersRequired(tournament.getTournamentType());

        if (tournament.getPlayers().size() < minAmountPlayers)
        {
            throw new NotEnoughPlayersException(tournament.getPlayers().size(), minAmountPlayers);
        }

        tournament.setStartedAt(LocalDate.now());
        tournamentRepository.save(tournament);

        MatchGenerator matchGenerator = createMatchGenerator(tournament);
        matchGenerator.generateMatches(tournament);
    }

    MatchGenerator createMatchGenerator(Tournament tournament)
    {
        return null;
//        return new MatchGenerator(tournament.getSetsToWin(), tournament.getSemifinalsSetsToWin(),
//                tournament.getFinalsSetsToWin(), tournament.getTournamentType(), new PlayerDistributer(), this);
    }
}
