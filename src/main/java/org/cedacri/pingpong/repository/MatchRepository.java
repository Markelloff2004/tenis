package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    // Add custom queries if required
}
