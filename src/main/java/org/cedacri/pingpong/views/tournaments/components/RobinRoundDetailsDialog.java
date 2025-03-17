package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RobinRoundDetailsDialog extends Dialog {

    private final Grid<Player> playerRatingGrid = new Grid<>(Player.class, false);

    private final Tournament tournament;

    public RobinRoundDetailsDialog(Integer tournamentId, TournamentService tournamentService) {
        log.info("Initializing tournament {} Rating ", tournamentId);

        this.tournament = tournamentService.findTournamentById(tournamentId);

        setHeaderTitle(tournament.getTournamentName() + " Rating");
        setWidth("80%");

        configureGrid();

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, playerRatingGrid));
        loadPlayersIntoGrid();

        Button closeDialog = ViewUtils.createButton("Cancel",ViewUtils.BUTTON, () -> {
            log.info("Cancel button clicked. Closing TournamentDeleteDialog.");
            close();
        });

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, closeDialog));

    }


    private void configureGrid() {
        playerRatingGrid.addColumn(player ->
                TournamentUtils.calculateNewRating(
                        TournamentUtils.calculateNewWonMatches(player, tournament),
                        TournamentUtils.calculateNewLostMatches(player, tournament),
                        TournamentUtils.calculateNewGoalsScored(player, tournament),
                        TournamentUtils.calculateNewGoalsLost(player, tournament))
        )
        .setHeader("Rating")
        .setSortable(true);

        playerRatingGrid.addColumn(Player::getName).setHeader("Name");
        playerRatingGrid.addColumn(Player::getSurname).setHeader("Surname");

        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewGoalsScored(player, tournament))
                .setHeader("Goals Scored")
                .setSortable(true);

        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewGoalsLost(player, tournament))
                .setHeader("Goals Lost")
                .setSortable(true);

        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewWonMatches(player, tournament))
                .setHeader("Won Matches")
                .setSortable(true);

        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewLostMatches(player, tournament))
                .setHeader("Lost Matches")
                .setSortable(true);
    }


    private void loadPlayersIntoGrid() {
        List<Player> players = new ArrayList<>(tournament.getPlayers());
        playerRatingGrid.setItems(players);
    }


}
