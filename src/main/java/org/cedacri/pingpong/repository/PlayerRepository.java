package org.cedacri.pingpong.repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.speedment.jpastreamer.projection.Projection;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Player$;
import org.cedacri.pingpong.utils.Constraints;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class PlayerRepository {

    private final JPAStreamer jpaStreamer;
    private final EntityManager em;

    public Optional<Player> findById(Long id){

        return jpaStreamer.stream(Player.class)
                .filter(Player$.id.equal(id))
                .findFirst();
    }

    public Stream<Player> paged(long page){
        List<Player> players = jpaStreamer.stream(
                        Projection.select(
                                Player$.id,
                                Player$.name,
                                Player$.surname,
                                Player$.birthDate, Player$.email, Player$.rating,
                                Player$.hand, Player$.wonMatches,
                                Player$.lostMatches, Player$.goalsScored,
                                Player$.goalsLost)
                )
                .sorted(Player$.id.reversed())
                .skip(page * Constraints.PAGE_SIZE)
                .limit(Constraints.PAGE_SIZE)
                .toList();

        players.forEach(player -> Hibernate.initialize(player.getTournaments()));

        return players.stream();
    }

    public Stream<Player> getAll(){
        List<Player> players = jpaStreamer.stream(
                        Projection.select(
                                Player$.id,
                                Player$.name,
                                Player$.surname,
                                Player$.birthDate, Player$.email,
                                Player$.address, Player$.rating,
                                Player$.hand, Player$.wonMatches,
                                Player$.lostMatches, Player$.goalsScored,
                                Player$.goalsLost)
                )
                .sorted(Player$.id.reversed())
                .toList();

        players.forEach(player -> Hibernate.initialize(player.getTournaments()));

        return players.stream();
    }

    public Player save(Player player){

        if(player.getId() == null){
            em.persist(player);
        }
        else {
            player = em.merge(player);
        }

        return player;
    }

    public void delete(Long id) {
        Player player = em.find(Player.class, id);

        if (player != null) {
            em.remove(player);
        }
    }


}
