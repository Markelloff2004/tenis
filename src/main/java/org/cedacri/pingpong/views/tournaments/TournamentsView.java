package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PageTitle("TournamentsView")
@Route(value = "tournaments", layout = MainLayout.class)
//@Menu(order = 1, icon = LineAwesomeIconUrl.PEOPLE_CARRY_SOLID)
@Uses(Icon.class)
public class TournamentsView extends VerticalLayout {

    private final Button addTournamentButton;
    private final Grid<Tournament> tournamentsGrid;
    private final TournamentService tournamentService;

    public TournamentsView(TournamentService tournamentService) {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Tournaments list");
        title.addClassName("tournaments-title");

        addTournamentButton = new Button("Add tournament");
        addTournamentButton.addClassName("colored-button");
        addTournamentButton.addClickListener(e -> {
//            openNewTournametDialog();
            openTournamentDialog();
//            openAddPlayerDialog();
            System.out.println("Add tournament button clicked");
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(addTournamentButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        tournamentsGrid = new Grid<>(Tournament.class, false);
        tournamentsGrid.addClassName("tournaments-grid");
        tournamentsGrid.setSizeFull();
        configureGrid();

        add(title, buttonLayout, tournamentsGrid);
        this.tournamentService = tournamentService;

        refreshGridData();
    }


//    private void openTournamentDialog() {
//        // Create dialog
//        Dialog dialog = new Dialog();
//
//        // Form fields
//        TextField nameField = new TextField("Tournament Name");
//        nameField.setPlaceholder("Enter tournament name");
//
//        TextField rulesField = new TextField("Rules");
//        rulesField.setPlaceholder("E.g., Standard Rules");
//
//        ComboBox<String> tournamentTypeComboBox = new ComboBox<>("Select Tournament Type");
//        List<String> playersList = generatePlayersList(100); // Generate a list of 100 players
//        tournamentTypeComboBox.setItems(playersList);
//        tournamentTypeComboBox.setPlaceholder("Select Tournament Type");
//        tournamentTypeComboBox.setWidthFull();
//
//        // Layout
//        FormLayout formLayout = new FormLayout();
//        formLayout.add(nameField, rulesField, tournamentTypeComboBox);
//
//        // Buttons
//        Button saveButton = new Button("Save", event -> {
//            // Get form data
//            String name = nameField.getValue();
//            String rules = rulesField.getValue();
//            selectedPlayers = new ArrayList<>(tournamentTypeComboBox.getValue());
//
//            // Validate
//            if (name.isEmpty() || selectedPlayers.isEmpty()) {
//                Notification.show("Please fill in all required fields and select players.");
//                return;
//            }
//
//            // Simulate player mapping
//            List<Player> players = selectedPlayers.stream()
//                    .map(Player::new) // Assuming Player has a constructor for name
//                    .collect(Collectors.toList());
//
//            // Create Tournament
//            Tournament newTournament = new Tournament(name, null, rules, null, "Knockout", players);
//
//            // Log the result or send to backend
//            Notification.show("Tournament created: " + newTournament.getName() + " with " + players.size() + " players");
//
//            // Close the dialog
//            dialog.close();
//        });
//
//        Button cancelButton = new Button("Cancel", event -> dialog.close());
//
//        // Button layout
//        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
//
//        // Add components to dialog
//        dialog.add(formLayout, buttonLayout);
//        dialog.open();
//    }

    private void openTournamentDialog() {
        // Create dialog
        Dialog dialog = new Dialog();

        // Form fields
        TextField nameField = new TextField("Tournament Name");
        nameField.setPlaceholder("Enter tournament name");

        TextField rulesField = new TextField("Rules");
        rulesField.setPlaceholder("E.g., Standard Rules");

        // ComboBox for Tournament Type
        ComboBox<String> tournamentTypeComboBox = new ComboBox<>("Select Tournament Type");
        tournamentTypeComboBox.setItems("Knockout", "Round Robin", "Double Elimination", "Swiss System");
        tournamentTypeComboBox.setPlaceholder("Select Tournament Type");
        tournamentTypeComboBox.setWidthFull();

        // Layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, rulesField, tournamentTypeComboBox);

        // Buttons
        Button saveButton = new Button("Save", event -> {
            // Get form data
            String name = nameField.getValue();
            String rules = rulesField.getValue();
            String tournamentType = tournamentTypeComboBox.getValue();

            // Validate
            if (name.isEmpty() || tournamentType == null || tournamentType.isEmpty()) {
                Notification.show("Please fill in all required fields.");
                return;
            }

            // Create Tournament
            Tournament newTournament = new Tournament(name, null, rules, null, tournamentType, new ArrayList<>());

            // Log the result or send to backend
            Notification.show("Tournament created: " + newTournament.getName() + " (" + tournamentType + ")");

            // Close the dialog
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        // Add components to dialog
        dialog.add(formLayout, buttonLayout);
        dialog.open();
    }

    private List<String> generatePlayersList(int count) {
        return IntStream.range(1, count + 1)
                .mapToObj(i -> "Player " + i)
                .collect(Collectors.toList());
    }

    private void configureGrid()
    {
        tournamentsGrid.addColumn(Tournament::getName).setHeader("Name").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getMaxPlayers).setHeader("MaxPlayers").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getRules).setHeader("Rules").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getType).setHeader("Type").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getCreatedAt).setHeader("CreatedAt").setSortable(true);

        tournamentsGrid.addColumn(new ComponentRenderer<>(player -> {
            HorizontalLayout actionsLayout = new HorizontalLayout();

            Button viewButton = new Button("View", click -> {
//                openViewPlayerDialog(player);
            });
            viewButton.addClassName("compact-button");

            Button editButton = new Button("Edit", click -> {
//                openEditPlayerDialog(player);
            });
            editButton.addClassName("compact-button");

            Button deleteButton = new Button("Delete", click -> {
//                deletePlayer(player);
            });
            deleteButton.addClassName("compact-button");

            actionsLayout.add(viewButton, editButton, deleteButton);
            return actionsLayout;
        })).setHeader("Actions").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
    }

    private void refreshGridData() {
//        playersGrid.getDataProvider().refreshAll();
        // playerService.getAllPlayers()
        tournamentsGrid.setItems(getDemoTournaments());
    }

    private List<Tournament> getDemoTournaments() {
        return Arrays.asList(
                new Tournament("Summer Cup", 16, "Standard Rules", "Active", "Knockout", Timestamp.valueOf("2022-06-22 2:00:00")),
                new Tournament("Winter League", 8, "Pro Rules", "Completed", "Round Robin", Timestamp.valueOf("2023-01-15 12:00:00")),
                new Tournament("Spring Open", 16, "Custom Rules", "Pending", "Double Elimination", Timestamp.valueOf("2023-04-10 14:00:00"))
        );
    }

}
