package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PageTitle("View Tournament")
@Route(value = "tournament/general-details", layout = MainLayout.class)
public class TournamentDetailsView extends VerticalLayout implements HasUrlParameter<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TournamentDetailsView.class);

    private final TournamentService tournamentService;
    private Tournament tournament;

    public TournamentDetailsView(TournamentService tournamentService) {
        this.tournamentService = tournamentService;

        setWidthFull();
        setPadding(true);
        setSpacing(true);
        logger.debug("TournamentDetailsView initialized");
    }

    @Override
    public void setParameter(BeforeEvent event, Integer tournamentId) {
        logger.info("Received request to load tournament with ID: {}: ", tournamentId);
        this.tournament = tournamentService.find(tournamentId).orElse(null);

        if (this.tournament != null) {
            logger.info("Tournament found: {}", tournament.getTournamentName());
            initView();
        } else {
            logger.info("Tournament with ID {} not found", tournamentId);
            add(new Span("Tournament not found"));
        }
    }

    private void initView()
    {
        logger.debug("Initializing view for tournament {}", tournament.getTournamentName());
        add(createTournamentNameLabel(tournament));
//        add(createTournamentDetailsSection(tournament));
        add(createButtonsLayout());
        add(createPlayersGrid(tournament));
        logger.debug("View initialization completed for tournament {}", tournament.getTournamentName());
    }

    private Span createTournamentNameLabel(Tournament tournament)
    {
        logger.debug("Creating tournament name label for: {}", tournament.getTournamentName());
        Span tournamentNameLabel = new Span(tournament.getTournamentName());
        tournamentNameLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("margin-bottom", "10px");
        return tournamentNameLabel;
    }

    private HorizontalLayout createTournamentDetailsSection(Tournament tournament)
    {
        logger.debug("Creating tournament details section");
        TextField maxPlayersField = ViewUtils.createReadOnlyField("Maximum Players", String.valueOf(tournament.getMaxPlayers()));
        TextField statusField = ViewUtils.createReadOnlyField("Status", tournament.getTournamentStatus().toString());
        TextField typeField = ViewUtils.createReadOnlyField("Type", tournament.getTournamentType().toString());

        return ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, maxPlayersField, statusField, typeField);
    }

    private Grid<Player> createPlayersGrid(Tournament tournament)
    {
        logger.debug("Creating players grid for: {}", tournament.getTournamentName());
        Grid<Player> playersGrid = new Grid<>(Player.class, false);
        playersGrid.setItems(tournament.getPlayers());

        playersGrid.addColumn(Player::getName).setHeader("Player Name").setSortable(true);
        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true);
        playersGrid.addColumn(Player::getWonMatches).setHeader("Matches Won").setSortable(true);
        playersGrid.addColumn(Player::getGoalsScored).setHeader("Goals Scored").setSortable(true);

        logger.debug("Players grid initialized");

        return playersGrid;
    }

    private HorizontalLayout createButtonsLayout()
    {
        logger.debug("Creating buttons layout for tournament {}", tournament.getTournamentName());
        Button prevoiusPageButton = ViewUtils.createButton(
                "Next Page",
                "colored-button",
                () -> {
                    logger.info("Navigating to matches page for tournament: {}", tournament.getTournamentName());
                    getUI().ifPresent(ui -> ui.navigate("tournament/matches/" + tournament.getId()));
                }
        );

        return ViewUtils.createHorizontalLayout(
                JustifyContentMode.BETWEEN,
                createTournamentDetailsSection(tournament),
                prevoiusPageButton);
    }
}
