package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.TournamentOlympic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<TournamentOlympic, Integer>
{

}
