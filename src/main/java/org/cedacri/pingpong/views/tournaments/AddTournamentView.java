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
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cedacri.pingpong.utils.TournamentUtils.calculateMaxPlayers;
import static org.springframework.util.ObjectUtils.isEmpty;

@PageTitle("Add Tournament")
@Route(value = "tournaments/add", layout = MainLayout.class)
public class AddTournamentView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(AddTournamentView.class);

    private final TournamentService tournamentService;
    private final MatchService matchService;

    private final TextField tournamentNameField = ViewUtils.createTextField("Tournament Name");
    private final ComboBox<String> tournamentStatusComboBox = ViewUtils.createComboBox("Tournament Status", Constraints.STATUS_OF_TOURNAMENTS);
    private final ComboBox<String> tournamentTypeComboBox = ViewUtils.createComboBox("Tournament Type", Arrays.asList(TournamentTypeEnum.values()).stream()
            .map(v -> v.toString())
            .collect(Collectors.toList()));
    private final Grid<Player> availablePlayersGrid = new Grid<>(Player.class, false);
    private final Grid<Player> selectedPlayersGrid = new Grid<>(Player.class, false);

    private final Set<Player> availablePlayers;
    private final Set<Player> selectedPlayers = new HashSet<>();

    public AddTournamentView(PlayerService playerService, TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;

        this.availablePlayers = playerService.getAll().collect(Collectors.toSet());

        logger.info("Initialized AddTournamentView");

        configureView();
        GridUtils.configureGrid(availablePlayersGrid);
        GridUtils.configureGrid(selectedPlayersGrid);
        logger.debug("Grids for available and selected players have been configured");
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
        logger.info("AddTournamentView UI components have been set up.");
    }

    private HorizontalLayout createTournamentDetailsLayout() {
        logger.debug("Creating tournament details layout.");
        return ViewUtils.createHorizontalLayout (JustifyContentMode.CENTER, tournamentNameField, tournamentStatusComboBox, tournamentTypeComboBox);
    }

    private HorizontalLayout createPlayerSelectionLayout()
    {
        logger.debug("Creating player selection layout.");
        GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayers, selectedPlayers, "Add", this::refreshGrids);
        GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayers, availablePlayers, "Delete", this::refreshGrids);

        return ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, availablePlayersGrid, selectedPlayersGrid);
    }

    private HorizontalLayout createButtonLayout() {
        logger.debug("Setting up button layout.");
        Button saveButton = ViewUtils.createButton("Save", "colored-button", this::saveTournament);
        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> {
            logger.info("Cancel button clicked. Redirecting to TournamentView.");
            getUI().ifPresent(ui -> ui.navigate("tournaments"));
        });

        return ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN, saveButton, cancelButton);
    }

    private void saveTournament() {
        logger.info("Attempting to save tournament.");
        if (!validateFields()) {
            logger.warn("Tournament saving aborted due to validation error.");
            return;
        }

        Tournament newTournament = new Tournament();
        int maxPlayers = calculateMaxPlayers(selectedPlayers.size());
        newTournament.setTournamentName(tournamentNameField.getValue());
        newTournament.setTournamentStatus(TournamentStatusEnum.valueOf(tournamentStatusComboBox.getValue().toUpperCase()));
        newTournament.setTournamentType(TournamentTypeEnum.valueOf(tournamentTypeComboBox.getValue().toUpperCase()));
        newTournament.setMaxPlayers(maxPlayers);
        newTournament.setPlayers(selectedPlayers);

        try {
            newTournament = tournamentService.saveTournament(newTournament);
            logger.info("Tournament successfully saved with ID: {}:", newTournament.getId());

//            TournamentUtils.generateTournamentMatches(matchService, newTournament);
            logger.info("Matches generated successfully");

            NotificationManager.showInfoNotification("Tournament created successfully! id: " + newTournament.getId());
            getUI().ifPresent(ui -> ui.navigate("tournaments"));

        } catch (Exception e) {
            logger.error("Failed to save tournament. Error: {}", e.getMessage(), e);
            NotificationManager.showInfoNotification("Failed to save tournament. Please try again.");
        }
    }

    private boolean validateFields() {
        if (isEmpty(tournamentNameField)) {
            logger.warn("Validation failed: Tournament name is empty.");
            return ViewUtils.showValidationError ("Tournament name is required!");
        }
        if (isEmpty(tournamentStatusComboBox)) {
            logger.warn("Validation failed: Tournament status is empty.");
            return ViewUtils.showValidationError("Tournament status is required!");
        }
        if (isEmpty(tournamentTypeComboBox)) {
            logger.warn("Validation failed: Tournament type is empty.");
            return ViewUtils.showValidationError("Tournament type is required!");
        }
        if (selectedPlayers.isEmpty()) {
            logger.warn("Validation failed: No players selected for the tournament");
            return ViewUtils.showValidationError("At least one player must be selected!");
        }
        return true;
    }

    private void refreshGrids() {
        logger.debug("Refreshing player grids.");
        availablePlayersGrid.setItems(availablePlayers);
        selectedPlayersGrid.setItems(selectedPlayers);
    }
}
