package org.cedacri.pingpong.repository;

import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer>
{

    List<Match> findByTournament(TournamentOlympic tournamentOlympic);

    List<Match> findByTournamentAndRound(TournamentOlympic tournamentOlympic, int round);

    Optional<Match> findByTournamentAndRoundAndPosition(TournamentOlympic tournamentOlympic, int round, int position);

    List<Match> findByTopPlayer(Player topPlayer);

    List<Match> findByBottomPlayer(Player bottomPlayer);

    List<Match> findByWinner(Player winner);

}