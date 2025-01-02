package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cedacri.pingpong.utils.TournamentUtils.calculateMaxPlayers;

@PageTitle("Add Tournament")
@Route(value = "tournaments/add", layout = MainLayout.class)
public class AddTournamentView extends VerticalLayout {

    private final PlayerService playerService;
    private final TournamentService tournamentService;

    private final TextField tournamentNameField = createTextField("Tournament Name");
    private final ComboBox<String> tournamentStatusComboBox = createComboBox("Tournament Status", "PENDING", "ONGOING", "FINISHED");
    private final ComboBox<String> tournamentTypeComboBox = createComboBox("Tournament Type", "BESTOFTHREE", "BESTOFFIVE", "BESTOFSEVEN");
    private final Grid<Player> availablePlayersGrid = new Grid<>(Player.class, false);
    private final Grid<Player> selectedPlayersGrid = new Grid<>(Player.class, false);

    private final Set<Player> availablePlayers;
    private final Set<Player> selectedPlayers = new HashSet<>();

    public AddTournamentView(PlayerService playerService, TournamentService tournamentService) {
        this.playerService = playerService;
        this.tournamentService = tournamentService;

        this.availablePlayers = playerService.getAll().collect(Collectors.toSet());

        configureView();
        configureGrids();
    }

    private void configureView() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);

        add(new H1("Add Tournament"), createTournamentDetailsLayout(), createPlayerSelectionLayout(), createButtonLayout());
        setAlignItems(Alignment.STRETCH);
    }

    private HorizontalLayout createTournamentDetailsLayout() {
        HorizontalLayout layout = new HorizontalLayout(tournamentNameField, tournamentStatusComboBox, tournamentTypeComboBox);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        return layout;
    }

    private HorizontalLayout createPlayerSelectionLayout() {
        configureAvailablePlayersGrid();
        configureSelectedPlayersGrid();

        HorizontalLayout layout = new HorizontalLayout(availablePlayersGrid, selectedPlayersGrid);
        layout.setSpacing(true);
        layout.setWidthFull();
        return layout;
    }

    private void configureAvailablePlayersGrid() {
        setupGridColumns(availablePlayersGrid);
        availablePlayersGrid.setItems(availablePlayers);

        availablePlayersGrid.addColumn(new ComponentRenderer<>(player -> {
                    Button addButton = new Button("Add", click -> {
                        availablePlayers.remove(player);
                        selectedPlayers.add(player);
                        refreshGrids();
                    });
                    addButton.addClassName("compact-button");
                    return addButton;
                })).setHeader("Actions")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true)
                .setResizable(false);
    }

    private void configureSelectedPlayersGrid() {
        setupGridColumns(selectedPlayersGrid);
        selectedPlayersGrid.setItems(selectedPlayers);

        selectedPlayersGrid.addColumn(new ComponentRenderer<>(player -> {
                    Button deleteButton = new Button("Delete", click -> {
                        selectedPlayers.remove(player);
                        availablePlayers.add(player);
                        refreshGrids();
                    });
                    deleteButton.addClassName("compact-button");
                    return deleteButton;
                })).setHeader("Actions")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true)
                .setResizable(false);
    }


    private void configureGrids() {
        availablePlayersGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        selectedPlayersGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        availablePlayersGrid.setWidth("45%");
        selectedPlayersGrid.setWidth("45%");
    }

    private void setupGridColumns(Grid<Player> grid) {
        grid.addColumn(Player::getRating).setHeader("Rating").setSortable(true);
        grid.addColumn(Player::getPlayerName).setHeader("Name").setSortable(true);
    }

    private HorizontalLayout createButtonLayout() {
        Button saveButton = new Button("Save", event -> saveTournament());
        saveButton.addClassName("colored-button");

        Button cancelButton = new Button("Cancel", event -> getUI().ifPresent(ui -> ui.navigate("tournaments")));
        cancelButton.addClassName("button");

        HorizontalLayout layout = new HorizontalLayout(saveButton, cancelButton);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.getStyle().set("margin-top", "auto");
        return layout;
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

        tournamentService.create(newTournament);

        selectedPlayers.forEach(p -> {
            p.getTournaments().add(newTournament);
            playerService.save(p);
        });

        for(Player p : selectedPlayers) {
            p.getTournaments().add(newTournament);
            playerService.save(p);
        }

        Notification.show("Tournament created successfully!");
        getUI().ifPresent(ui -> ui.navigate("tournaments"));
    }

    private boolean validateFields() {
        if (tournamentNameField.isEmpty()) {
            showNotification("Tournament name is required!");
            return false;
        }

        if (tournamentStatusComboBox.isEmpty()) {
            showNotification("Tournament status is required!");
            return false;
        }

        if (tournamentTypeComboBox.isEmpty()) {
            showNotification("Tournament type is required!");
            return false;
        }

        if (selectedPlayers.isEmpty()) {
            showNotification("At least one player must be selected!");
            return false;
        }

        return true;
    }

    private void refreshGrids() {
        availablePlayersGrid.setItems(availablePlayers);
        selectedPlayersGrid.setItems(selectedPlayers);
    }

    private void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    private static TextField createTextField(String label) {
        TextField textField = new TextField(label);
        textField.setWidth("250px");
        return textField;
    }

    private static ComboBox<String> createComboBox(String label, String... items) {
        ComboBox<String> comboBox = new ComboBox<>(label);
        comboBox.setItems(items);
        comboBox.setWidth("250px");
        return comboBox;
    }
}
