package org.cedacri.pingpong.service.matches;

import org.cedacri.pingpong.model.match.Match;
import org.cedacri.pingpong.model.match.Score;


public interface IMatchService {

    Match getMatchById(Integer matchId);

    Match createMatch(Match match);
    void updateMatch(Match match);
    void updateMatchScore(Long matchId, Score score);
    void setWinner(Long matchId, Long playerIdWinner);
    void updateNextMatch(Long matchId, Match nextMatch);
}
