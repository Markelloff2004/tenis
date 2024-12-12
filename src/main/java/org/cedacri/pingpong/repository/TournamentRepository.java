package org.cedacri.pingpong.repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cedacri.pingpong.entity.Tournament;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class TournamentRepository {

    private final JPAStreamer jpaStreamer;
    private final EntityManager entityManager;

    private final Integer PAGE_SIZE = 10;

    public Optional<Tournament> getById(Integer id) {
        return jpaStreamer.stream(Tournament.class)
                .filter(tournament -> tournament.getId().equals(id))
                .findFirst();
    }

    public Stream<Tournament> getAll() {
        return jpaStreamer.stream(Tournament.class)
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()));
    }


    public Stream<Tournament> getPagedByMaxPlayers(long page, Integer maxPlayers) {
        return jpaStreamer.stream(Tournament.class)
                .filter(tournament -> tournament.getMaxPlayers() < maxPlayers)
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
    }

    public Stream<Tournament> getPagedByStatus(long page, String status) {
        return jpaStreamer.stream(Tournament.class)
                .filter(tournament -> tournament.getTournamentStatus().equalsIgnoreCase(status))
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
    }

    public Stream<Tournament> getPagedByType(long page, String type) {
        return jpaStreamer.stream(Tournament.class)
                .filter(tournament -> tournament.getTournamentType().equalsIgnoreCase(type))
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
    }

    public Stream<Tournament> getWithMinPlayers(int minPlayers) {
        return jpaStreamer.stream(Tournament.class)
                .filter(tournament -> tournament.getPlayers().size() >= minPlayers)
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()));
    }


    public Stream<Tournament> getPagedByDate(long page) {
        return jpaStreamer.stream(Tournament.class)
                .sorted((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
    }

    @Transactional
    public Tournament saveTournament(Tournament tournament) {
        if (tournament.getId() == null) {
            entityManager.persist(tournament);
        } else {
            tournament = entityManager.merge(tournament);
        }
        return tournament;
    }

    public void deleteTournamentById(Integer tournamentId) {
        Optional<Tournament> tournamentToDelete = getById(tournamentId);

        if (tournamentToDelete.isPresent()) {
            entityManager.remove(tournamentToDelete);
        }
    }


}

