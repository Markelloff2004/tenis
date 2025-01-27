package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findByTournament(Tournament tournament);
    List<Match> findByTournamentAndRound(Tournament tournament, int round);
    Optional<Match> findByTournamentAndRoundAndPosition(Tournament tournament, int round, int position);
    List<Match> findByTopPlayer(Player topPlayer);
    List<Match> findByBottomPlayer(Player bottomPlayer);

    List<Match> findByWinner(Player winner);

}