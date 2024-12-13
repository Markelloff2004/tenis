package org.cedacri.pingpong.repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.speedment.jpastreamer.projection.Projection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Player$;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.entity.Tournament$;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class PlayerRepository {

    private final JPAStreamer jpaStreamer;
    private final EntityManager em;

    private final Integer PAGE_SIZE = 10;

    public Optional<Player> findById(Integer id){
        Optional<Player> player = jpaStreamer.stream(Player.class)
                .filter(Player$.id.equal(id))
                .findFirst();

        return player;
    }

    public Stream<Player> paged(long page){
//        List<Player> players =
//                jpaStreamer.stream(
//                        Projection.select(Player$.id, Player$.playerName, Player$.age, Player$.email)
//                        )
//                .sorted(Player$.id.reversed())
//                .skip(page * PAGE_SIZE)
//                .limit(PAGE_SIZE)
//                .collect(Collectors.toList());

        List<Player> players = jpaStreamer.stream(
                        Projection.select(
                                Player$.id,
                                Player$.playerName,
                                Player$.age, Player$.email, Player$.rating,
                                Player$.playingHand, Player$.winnedMatches,
                                Player$.losedMatches, Player$.goalsScored,
                                Player$.goalsLosed)
                )
                .sorted(Player$.id.reversed())
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());

        players.forEach(player -> Hibernate.initialize(player.getTournaments()));

        return players.stream();
    }

    public Stream<Player> getAll(){
//        List<Player> players =
//                jpaStreamer.stream(
//                        Projection.select(Player$.id, Player$.playerName, Player$.age, Player$.email)
//                        )
//                .sorted(Player$.id.reversed())
//                .skip(page * PAGE_SIZE)
//                .limit(PAGE_SIZE)
//                .collect(Collectors.toList());

        List<Player> players = jpaStreamer.stream(
                        Projection.select(
                                Player$.id,
                                Player$.playerName,
                                Player$.age, Player$.email, Player$.rating,
                                Player$.playingHand, Player$.winnedMatches,
                                Player$.losedMatches, Player$.goalsScored,
                                Player$.goalsLosed)
                )
                .sorted(Player$.id.reversed())
                .collect(Collectors.toList());

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

    public void delete(Integer id) {
        Player player = em.find(Player.class, id);

        if (player != null) {
            em.remove(player);
        }
    }


}
