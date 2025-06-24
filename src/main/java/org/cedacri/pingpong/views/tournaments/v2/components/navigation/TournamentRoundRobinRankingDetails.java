package org.cedacri.pingpong.views.tournaments.v2.components.navigation;

import com.vaadin.flow.component.dialog.Dialog;
import org.cedacri.pingpong.model.tournament.TournamentRoundRobin;

public class TournamentRoundRobinRankingDetails extends Dialog {
    public TournamentRoundRobinRankingDetails(TournamentRoundRobin tournamentRoundRobin) {
    add("Ancora non lo fatto: " + tournamentRoundRobin.getTournamentName());
    }
}
