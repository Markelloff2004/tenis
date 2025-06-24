//package org.cedacri.pingpong.views.tournaments.v1.components;
//
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.dialog.Dialog;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import lombok.extern.slf4j.Slf4j;
//import org.cedacri.pingpong.entity.Player;
//import org.cedacri.pingpong.entity.TournamentRoundRobin;
//import org.cedacri.pingpong.service.tournament_round_robin.TournamentRoundRobinService;
//import org.cedacri.pingpong.utils.TournamentUtils;
//import org.cedacri.pingpong.utils.ViewUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//public class TournamentRoundRobinDetails extends Dialog {
//
//    private final Grid<Player> playerRatingGrid = new Grid<>(Player.class, false);
//
//    private final TournamentRoundRobin tournamentRoundRobin;
//
//    public TournamentRoundRobinDetails(Long tournamentId, TournamentRoundRobinService tournamentRoundRobinService) {
//        log.info("Initializing tournament {} Rating ", tournamentId);
//
//        this.tournamentRoundRobin = tournamentRoundRobinService.findTournamentById(tournamentId);
//
//        setHeaderTitle(tournamentRoundRobin.getTournamentName() + " Rating");
//        setWidth("80%");
//
//        configureGrid();
//
//        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, playerRatingGrid));
//        loadPlayersIntoGrid();
//
//        Button closeDialog = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, () ->
//        {
//            log.info("Cancel button clicked. Closing TournamentDeleteDialog.");
//            close();
//        });
//
//        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.CENTER, closeDialog));
//
//    }
//
//
//    private void configureGrid() {
//        playerRatingGrid.addColumn(player ->
//                        TournamentUtils.calculateNewRating(
//                                TournamentUtils.calculateNewWonMatches(player, tournamentRoundRobin),
//                                TournamentUtils.calculateNewLostMatches(player, tournamentRoundRobin),
//                                TournamentUtils.calculateNewGoalsScored(player, tournamentRoundRobin),
//                                TournamentUtils.calculateNewGoalsLost(player, tournamentRoundRobin))
//                )
//                .setHeader("Rating")
//                .setSortable(true);
//
//        playerRatingGrid.addColumn(Player::getName).setHeader("Name");
//        playerRatingGrid.addColumn(Player::getSurname).setHeader("Surname");
//
//        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewGoalsScored(player, tournamentRoundRobin))
//                .setHeader("Goals Scored")
//                .setSortable(true);
//
//        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewGoalsLost(player, tournamentRoundRobin))
//                .setHeader("Goals Lost")
//                .setSortable(true);
//
//        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewWonMatches(player, tournamentRoundRobin))
//                .setHeader("Won Matches")
//                .setSortable(true);
//
//        playerRatingGrid.addColumn(player -> TournamentUtils.calculateNewLostMatches(player, tournamentRoundRobin))
//                .setHeader("Lost Matches")
//                .setSortable(true);
//    }
//
//
//    private void loadPlayersIntoGrid() {
//        List<Player> players = new ArrayList<>(tournamentRoundRobin.getPlayers());
//        playerRatingGrid.setItems(players);
//    }
//
//
//}
