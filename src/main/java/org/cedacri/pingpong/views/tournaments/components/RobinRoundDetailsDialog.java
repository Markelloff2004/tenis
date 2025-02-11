package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RobinRoundDetailsDialog extends Dialog {

    private Grid<Player> playerRatingGrid = new Grid<>(Player.class, false);

    private Tournament tournament;

    public RobinRoundDetailsDialog(Tournament tournament) {

        log.info("Initializing {} Rating ", tournament.getTournamentName());

        this.tournament = tournament;

        setHeaderTitle(tournament.getTournamentName() + " Rating");
        setWidth("80%");

        configureGrid();

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, playerRatingGrid));
        loadPlayersIntoGrid();

        Button closeDialog = ViewUtils.createButton("Cancel","button", () -> {
            log.info("Cancel button clicked. Closing TournamentDeleteDialog.");
            close();
        });

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, closeDialog));

    }


    private void configureGrid() {
        playerRatingGrid.addColumn(this::calculateNewRating)
                .setHeader("Rating")
                .setSortable(true);

        playerRatingGrid.addColumn(Player::getName).setHeader("Name");
        playerRatingGrid.addColumn(Player::getSurname).setHeader("Surname");

        playerRatingGrid.addColumn(this::calculateNewGoalsScored)
                .setHeader("Goals Scored")
                .setSortable(true);

        playerRatingGrid.addColumn(this::calculateNewGoalsLost)
                .setHeader("Goals Lost")
                .setSortable(true);

        playerRatingGrid.addColumn(this::calculateNewWonMatches)
                .setHeader("Won Matches")
                .setSortable(true);

        playerRatingGrid.addColumn(this::calculateNewLostMatches)
                .setHeader("Lost Matches")
                .setSortable(true);
    }

    private int calculateNewRating(Player player) {
        int newWonMatches = calculateNewWonMatches(player);
        int newLostMatches = calculateNewLostMatches(player);
        int newGoalsScored = calculateNewGoalsScored(player);
        int newGoalsLost = calculateNewGoalsLost(player);

        return (5 * newWonMatches - 3 * newLostMatches) + (2 * newGoalsScored - newGoalsLost);
    }

    private int calculateNewGoalsScored(Player player) {
        return tournament.getMatches().stream()
                .filter(match -> match.getTopPlayer().equals(player) || match.getBottomPlayer().equals(player))
                .flatMapToInt(match -> match.getScore().stream()
                                .mapToInt(score -> match.getTopPlayer().equals(player)
                                        ? score.getTopPlayerScore()
                                        : score.getBottomPlayerScore()
                                )
                )
                .sum();
    }

    private int calculateNewGoalsLost(Player player) {
        return tournament.getMatches().stream()
                .filter(match -> match.getTopPlayer().equals(player) || match.getBottomPlayer().equals(player))
                .flatMapToInt(match -> match.getScore().stream()
                        .mapToInt(score -> match.getTopPlayer().equals(player)
                                ? score.getBottomPlayerScore()
                                : score.getTopPlayerScore()
                        )
                )
                .sum();
    }

    private int calculateNewWonMatches(Player player) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getWinner() != null && match.getWinner().equals(player))
                .count();
    }

    private int calculateNewLostMatches(Player player) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getWinner() != null
                        && !match.getWinner().equals(player)
                        && (match.getTopPlayer().equals(player) || match.getBottomPlayer().equals(player)))
                .count();
    }

    private void loadPlayersIntoGrid() {
        List<Player> players = new ArrayList<>(tournament.getPlayers());
        playerRatingGrid.setItems(players);
    }


}
