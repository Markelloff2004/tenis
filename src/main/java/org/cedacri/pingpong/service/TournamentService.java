package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.exception.tournament.NotEnoughPlayersException;
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
    public Stream<TournamentOlympic> findAllTournaments()
    {
        List<TournamentOlympic> tournamentOlympics = tournamentRepository.findAll();
        tournamentOlympics.forEach(tournament -> Hibernate.initialize(tournament.getPlayers()));
        return tournamentOlympics.stream().sorted(Comparator.comparing(TournamentOlympic::getCreatedAt).reversed());
    }

    public TournamentOlympic findTournamentById(Integer id)
    {
        validateTournamentId(id);

        TournamentOlympic tournamentOlympic = tournamentRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + id + " not found"));

        Hibernate.initialize(tournamentOlympic.getPlayers());
        return tournamentOlympic;
    }

    @Transactional
    public TournamentOlympic saveTournament(TournamentOlympic tournamentOlympic)
    {
        if (tournamentOlympic == null)
        {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        return tournamentRepository.save(tournamentOlympic);
    }

    @Transactional
    public void deleteTournamentById(Integer id)
    {
        validateTournamentId(id);
        TournamentOlympic tournamentOlympicToDelete = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        tournamentOlympicToDelete.getPlayers().forEach(player -> player.getTournamentOlympics().remove(tournamentOlympicToDelete));
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
    public void startTournament(TournamentOlympic tournamentOlympic) throws NotEnoughPlayersException
    {

        int minAmountPlayers = TournamentUtils.getMinimalPlayersRequired(tournamentOlympic.getTournamentType());

        if (tournamentOlympic.getPlayers().size() < minAmountPlayers)
        {
            throw new NotEnoughPlayersException(tournamentOlympic.getPlayers().size(), minAmountPlayers);
        }

        tournamentOlympic.setStartedAt(LocalDate.now());
        tournamentRepository.save(tournamentOlympic);

        MatchGenerator matchGenerator = createMatchGenerator(tournamentOlympic);
        matchGenerator.generateMatches(tournamentOlympic);
    }

    MatchGenerator createMatchGenerator(TournamentOlympic tournamentOlympic)
    {
        return new MatchGenerator(tournamentOlympic.getSetsToWin(), tournamentOlympic.getSemifinalsSetsToWin(),
                tournamentOlympic.getFinalsSetsToWin(), tournamentOlympic.getTournamentType(), new PlayerDistributer(), this);
    }
}
