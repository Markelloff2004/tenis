package org.cedacri.pingpong.views.test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("new-tournament")
public class TournamentCreationView extends VerticalLayout {

    private List<PlayerTest> allPlayers;
    private List<PlayerTest> selectedPlayers;
    private Grid<PlayerTest> allPlayersGrid;
    private Grid<PlayerTest> selectedPlayersGrid;

    public TournamentCreationView() {
        // Title
        add(new HorizontalLayout(new Button("Back to Main", e -> getUI().ifPresent(ui -> ui.navigate("")))));

        TextField tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setPlaceholder("Enter tournament name");
        TextField rulesField = new TextField("Rules");
        rulesField.setPlaceholder("Enter rules (e.g., Best of Three)");

        // Initialize player lists
        allPlayers = getDemoPlayers();
        selectedPlayers = new ArrayList<>();

        // Create grids
        allPlayersGrid = createPlayersGrid("Add", allPlayers, this::addPlayerToTournament);
        selectedPlayersGrid = createPlayersGrid("Remove", selectedPlayers, this::removePlayerFromTournament);

        // Layout
        HorizontalLayout gridsLayout = new HorizontalLayout(allPlayersGrid, selectedPlayersGrid);
        gridsLayout.setWidthFull();

        Button saveButton = new Button("Save Tournament", e -> saveTournament(tournamentNameField, rulesField));

        add(tournamentNameField, rulesField, gridsLayout, saveButton);
    }

    private Grid<PlayerTest> createPlayersGrid(String buttonLabel, List<PlayerTest> players, PlayerAction action) {
        Grid<PlayerTest> grid = new Grid<>(PlayerTest.class, false);
        grid.addColumn(PlayerTest::getName).setHeader("Name");
        grid.addColumn(PlayerTest::getEmail).setHeader("Email");

        // Add action button to each row
        grid.addComponentColumn(player -> {
            Button actionButton = new Button(buttonLabel);
            actionButton.addClickListener(e -> action.perform(player));
            return actionButton;
        }).setHeader(buttonLabel);

        grid.setItems(players);
        return grid;
    }

    private void addPlayerToTournament(PlayerTest player) {
        if (!selectedPlayers.contains(player)) {
            selectedPlayers.add(player);
            allPlayers.remove(player);
            updateGrids();
        }
    }

    private void removePlayerFromTournament(PlayerTest player) {
        if (!allPlayers.contains(player)) {
            allPlayers.add(player);
            selectedPlayers.remove(player);
            updateGrids();
        }
    }

    private void updateGrids() {
        allPlayersGrid.setItems(allPlayers);
        selectedPlayersGrid.setItems(selectedPlayers);
    }

    private void saveTournament(TextField nameField, TextField rulesField) {
        String name = nameField.getValue();
        String rules = rulesField.getValue();

        if (name.isEmpty() || rules.isEmpty() || selectedPlayers.isEmpty()) {
            Notification.show("Please fill in all fields and select at least one player.");
            return;
        }

        // Save logic (e.g., send to backend)
        Notification.show("Tournament '" + name + "' with " + selectedPlayers.size() + " players saved.");
    }

    private List<PlayerTest> getDemoPlayers() {
        return List.of(
                new PlayerTest("John Doe", "john@example.com", 1200),
                new PlayerTest("Jane Smith", "jane@example.com", 1400),
                new PlayerTest("Mike Brown", "mike@example.com", 1100),
                new PlayerTest("Emily White", "emily@example.com", 1300)
        );
    }

    @FunctionalInterface
    interface PlayerAction {
        void perform(PlayerTest player);
    }
}

class PlayerTest {
    private String name;
    private String email;
    private int rating;

    public PlayerTest(String name, String email, int rating) {
        this.name = name;
        this.email = email;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getRating() {
        return rating;
    }
}
