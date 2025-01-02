package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
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
import org.cedacri.pingpong.views.MainLayout;

@PageTitle("View Tournament")
@Route(value = "tournament/general-details", layout = MainLayout.class)
public class TournamentDetailsView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final TournamentService tournamentService;
    private Tournament tournament;

    public TournamentDetailsView(TournamentService tournamentService) {
        this.tournamentService = tournamentService;

        setWidthFull();
        setPadding(true);
        setSpacing(true);
    }

    @Override
    public void setParameter(BeforeEvent event, Integer tournamentId) {
        this.tournament = tournamentService.find(tournamentId).orElse(null);

        if (this.tournament != null) {
            initView();
        } else {
            add(new Label("Tournament not found"));
        }
    }

    private void initView() {
        add(createTournamentNameLabel(tournament));
//        add(createTournamentDetailsSection(tournament));
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

    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonLayout.setAlignItems(Alignment.BASELINE);

        Button prevoiusPageButton = new Button("Next Page");
        prevoiusPageButton.addClassName("colored-button");
        prevoiusPageButton.addClickListener(e -> getUI().ifPresent(ui ->
                ui.navigate("tournament/matches/" + tournament.getId())));

        buttonLayout.add(createTournamentDetailsSection(tournament));
        buttonLayout.add(prevoiusPageButton);
        return buttonLayout;
    }
}
