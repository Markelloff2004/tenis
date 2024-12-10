package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlayerRepository extends JpaRepository<Player, Long>,
                                          JpaSpecificationExecutor<Player> {
}
