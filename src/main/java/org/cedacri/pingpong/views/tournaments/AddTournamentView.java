package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.util.GridUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cedacri.pingpong.utils.TournamentUtils.calculateMaxPlayers;
import static org.springframework.util.ObjectUtils.isEmpty;

@PageTitle("Add Tournament")
@Route(value = "tournaments/add", layout = MainLayout.class)
public class AddTournamentView extends VerticalLayout {

    private final TournamentService tournamentService;
    private final MatchService matchService;

    private final TextField tournamentNameField = ViewUtils.createTextField("Tournament Name");
    private final ComboBox<String> tournamentStatusComboBox = ViewUtils.createComboBox("Tournament Status", Constraints.STATUS_OF_TOURNAMENTS);
    private final ComboBox<String> tournamentTypeComboBox = ViewUtils.createComboBox("Tournament Type", Constraints.TYPES_OF_TOURNAMENTS);
    private final Grid<Player> availablePlayersGrid = new Grid<>(Player.class, false);
    private final Grid<Player> selectedPlayersGrid = new Grid<>(Player.class, false);

    private final Set<Player> availablePlayers;
    private final Set<Player> selectedPlayers = new HashSet<>();

    public AddTournamentView(PlayerService playerService, TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;

        this.availablePlayers = playerService.getAll().collect(Collectors.toSet());

        configureView();
        GridUtils.configureGrid(availablePlayersGrid);
        GridUtils.configureGrid(selectedPlayersGrid);
    }

    private void configureView() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        add(
                new H1("Add Tournament"),
                createTournamentDetailsLayout(),
                createPlayerSelectionLayout(),
                createButtonLayout()
        );
    }

    private HorizontalLayout createTournamentDetailsLayout() {
        return ViewUtils.createHorizontalLayout (JustifyContentMode.CENTER, tournamentNameField, tournamentStatusComboBox, tournamentTypeComboBox);
    }

    private HorizontalLayout createPlayerSelectionLayout()
    {
        GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayers, selectedPlayers, "Add", this::refreshGrids);
        GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayers, availablePlayers, "Delete", this::refreshGrids);

        return ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, availablePlayersGrid, selectedPlayersGrid);
    }

    private HorizontalLayout createButtonLayout() {
        Button saveButton = ViewUtils.createButton("Save", "colored-button", this::saveTournament);
        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> getUI().ifPresent(ui -> ui.navigate("tournaments")));

        return ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN, saveButton, cancelButton);
    }

    private void saveTournament() {
        if (!validateFields()) return;

        Tournament newTournament = new Tournament();
        int maxPlayers = calculateMaxPlayers(selectedPlayers.size());
        newTournament.setTournamentName(tournamentNameField.getValue());
        newTournament.setTournamentStatus(tournamentStatusComboBox.getValue());
        newTournament.setTournamentType(tournamentTypeComboBox.getValue());
        newTournament.setMaxPlayers(maxPlayers);
        newTournament.setPlayers(selectedPlayers);

        newTournament = tournamentService.saveTournament(newTournament);

        TournamentUtils.generateTournamentMatches(matchService, newTournament);

        NotificationManager.showInfoNotification("Tournament created successfully! id: ");
        getUI().ifPresent(ui -> ui.navigate("tournaments"));
    }

    private boolean validateFields() {
        if (isEmpty(tournamentNameField)) return ViewUtils.showValidationError ("Tournament name is required!");
        if (isEmpty(tournamentStatusComboBox)) return ViewUtils.showValidationError("Tournament status is required!");
        if (isEmpty(tournamentTypeComboBox)) return ViewUtils.showValidationError("Tournament type is required!");
        if (selectedPlayers.isEmpty()) return ViewUtils.showValidationError("At least one player must be selected!");
        return true;
    }

    private void refreshGrids() {
        availablePlayersGrid.setItems(availablePlayers);
        selectedPlayersGrid.setItems(selectedPlayers);
    }
}
