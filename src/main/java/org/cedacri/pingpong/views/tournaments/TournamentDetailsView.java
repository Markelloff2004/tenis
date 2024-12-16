package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.test.TournamentCreationView;

import java.util.Set;

@PageTitle("View Tournament")
@Route(value = "tournament/general-details", layout = MainLayout.class)
public class TournamentDetailsView extends VerticalLayout {

    private final TournamentService tournamentService;
//    private final TournamentCreationView tournament;

    public TournamentDetailsView(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
        setWidthFull();
        setPadding(true);
        setSpacing(true);

        Tournament tournament = fetchTournamentDetails();

        add(createTournamentNameLabel(tournament));
        add(createButtonsLayout());

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
        return tournamentService.findAll().findFirst().orElse(null);
    }

    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonLayout.setAlignItems(Alignment.BASELINE);

        Button prevoiusPageButton = new Button("Next Page");
        prevoiusPageButton.addClassName("colored-button");
        prevoiusPageButton.addClickListener(e -> getUI().ifPresent(ui ->
                ui.navigate("tournaments/" + fetchTournamentDetails().getId().toString())));

        buttonLayout.add(createTournamentDetailsSection(fetchTournamentDetails()));
        buttonLayout.add(prevoiusPageButton);
        return buttonLayout;
    }
}
