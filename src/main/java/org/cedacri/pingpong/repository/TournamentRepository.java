package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long>
{
}
