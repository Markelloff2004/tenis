package org.cedacri.pingpong.service.interactions;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.primary.PlayerService;
import org.cedacri.pingpong.service.primary.TournamentService;

public class TournamentPlayerService {

    private final TournamentService tournamentService;
    private final PlayerService playerService;

    public TournamentPlayerService(TournamentService tournamentService, PlayerService playerService) {
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }

    public void addPlayerToTournament(Long playerId, Long tournamentId) {
        Player player = playerService.findPlayerById(playerId);
        Tournament tournament = tournamentService.findTournamentById(tournamentId);

        tournament.getPlayers().add(player);
        tournamentService.saveTournament(tournament);
    }

    public void removePlayerFromTournament(Long playerId, Long tournamentId) {
        Player player = playerService.findPlayerById(playerId);
        Tournament tournament = tournamentService.findTournamentById(tournamentId);

        tournament.getPlayers().remove(player);
        tournamentService.saveTournament(tournament);
    }





}
