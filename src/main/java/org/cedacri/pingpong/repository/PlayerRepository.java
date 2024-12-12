package org.cedacri.pingpong.repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Player$;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.entity.Tournament$;
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
        return jpaStreamer.stream(Player.class)
                .filter(Player$.id.equal(id))
                .findFirst();
    }

    public Stream<Player> paged(long page){
        return jpaStreamer.stream(Player.class)
                .sorted(Player$.id.reversed())
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
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

    public void delete(Integer id){
        Optional<Player> player = findById(id);

        if(player.isPresent()){
            em.remove(player.get());
        }
    }


}
