package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;

@PageTitle("View Tournament")
@Route(value = "tournament/view", layout = MainLayout.class)
public class ViewTournamentView extends VerticalLayout {

    private final TournamentService tournamentService;

    public ViewTournamentView(TournamentService tournamentService) {
        this.tournamentService = tournamentService;

        setWidthFull();
        setPadding(true);
        setSpacing(true);

        Tournament tournament = fetchTournamentDetails(); // Replace with real fetching logic

        add(createTournamentNameLabel(tournament));
        add(createHeaderButtons());
        add(createTournamentDetailsSection(tournament));
        add(createPlayersGrid(tournament));
    }

    private Label createTournamentNameLabel(Tournament tournament) {
        Label tournamentNameLabel = new Label(tournament.getTournamentName());
        tournamentNameLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("margin-bottom", "10px");
        return tournamentNameLabel;
    }

    private HorizontalLayout createHeaderButtons() {
        Button generalInfoButton = new Button("General Info", event -> {
            Notification.show("General Info Clicked!");
            // Add logic for navigating to general info or handling action
        });

        Button matchesButton = new Button("Matches", event -> {
            Notification.show("Matches Clicked!");
            // Add logic for navigating to matches or handling action
        });

        generalInfoButton.addClassName("colored-button");
        matchesButton.addClassName("colored-button");

        HorizontalLayout headerLayout = new HorizontalLayout(generalInfoButton, matchesButton);
        headerLayout.setSpacing(true);
        headerLayout.setPadding(false);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.START);

        return headerLayout;
    }

    private HorizontalLayout createTournamentDetailsSection(Tournament tournament) {
        TextField maxPlayersField = createReadOnlyField("Maximum Players", String.valueOf(tournament.getMaxPlayers()));
        TextField statusField = createReadOnlyField("Status", tournament.getTournamentStatus());
        TextField typeField = createReadOnlyField("Type", tournament.getTournamentType());

        HorizontalLayout detailsLayout = new HorizontalLayout(maxPlayersField, statusField, typeField);
        detailsLayout.setSpacing(true);
        detailsLayout.setPadding(true);
        detailsLayout.setWidthFull();

        return detailsLayout;
    }

    private Grid<Player> createPlayersGrid(Tournament tournament) {
        Grid<Player> playersGrid = new Grid<>(Player.class, false);
        playersGrid.setItems(tournament.getPlayers());

        playersGrid.addColumn(Player::getPlayerName).setHeader("Player Name").setSortable(true);
        playersGrid.addColumn(Player::getRating).setHeader("Rating").setSortable(true);
        playersGrid.addColumn(Player::getWinnedMatches).setHeader("Matches Won").setSortable(true);
        playersGrid.addColumn(Player::getGoalsScored).setHeader("Goals Scored").setSortable(true);

        return playersGrid;
    }

    private TextField createReadOnlyField(String label, String value) {
        TextField textField = new TextField(label);
        textField.setValue(value != null ? value : "N/A");
        textField.setReadOnly(true);
        textField.setWidth("300px");
        return textField;
    }

    private Tournament fetchTournamentDetails() {
        // Replace this logic with actual fetching from the service
        // Here we use the first tournament as an example
        return tournamentService.findAll().findFirst().orElse(null);
    }
}
