package org.cedacri.pingpong.repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private final JPAStreamer jpaStreamer;
    private final EntityManager em;

    public Optional<Match> findById(Integer id) {
        return jpaStreamer.stream(Match.class)
                .filter(match -> match.getId().equals(id))
                .findFirst();
    }

    public Stream<Match> findAll() {
        return jpaStreamer.stream(Match.class)
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()));
    }

    public Stream<Match> findByTournament(Tournament tournament) {
        return jpaStreamer.stream(Match.class)
                .filter(match -> match.getTournament().equals(tournament));
    }

    @Transactional
    public Match save(Match match){
        if(match.getId() == null){
            em.persist(match);
        }
        else {
            match = em.merge(match);
        }

        return match;
    }


}
