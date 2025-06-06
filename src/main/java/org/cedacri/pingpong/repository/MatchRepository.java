package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>
{

    List<Match> findByTournament(Tournament tournament);

    List<Match> findByTournamentAndRound(Tournament tournament, int round);


}