package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import java.util.ArrayList;
import java.util.List;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Add Tournament")
@Route(value = "tournaments/add", layout = MainLayout.class)
public class AddTournamentView extends VerticalLayout {

    private final PlayerService playerService;
    private final TournamentService tournamentService;

    public AddTournamentView(PlayerService playerService, TournamentService tournamentService) {
        this.playerService = playerService;
        this.tournamentService = tournamentService;

        setWidthFull();
        setPadding(true);
        setSpacing(true);

        // Page title
        H1 title = new H1("Add Tournament");
        add(title);

        // Fields for tournament details
        TextField tournamentNameField = new TextField("Tournament Name");
        tournamentNameField.setWidth("250px");

        TextField rulesField = new TextField("Rules");
        rulesField.setWidth("250px");

        ComboBox<String> tournamentTypeComboBox = new ComboBox<>("Tournament Type");
        tournamentTypeComboBox.setItems("Knockout", "Round Robin", "Double Elimination", "Swiss System");
        tournamentTypeComboBox.setWidth("250px");

        // Layout for the fields in one line
        HorizontalLayout tournamentDetailsLayout = new HorizontalLayout(tournamentNameField, rulesField, tournamentTypeComboBox);
        tournamentDetailsLayout.setSpacing(true);
        tournamentDetailsLayout.setWidthFull();
        tournamentDetailsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center horizontally

        // Two-column player selection layout
        Grid<Player> availablePlayersGrid = new Grid<>(Player.class, false);
//        availablePlayersGrid.setItems(playerService.getAllPlayers());
        availablePlayersGrid.addColumn(Player::getPlayerName).setHeader("Name").setSortable(true);
        availablePlayersGrid.addColumn(Player::getEmail).setHeader("Email").setSortable(true);
        availablePlayersGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        availablePlayersGrid.setWidth("45%");

        Grid<Player> selectedPlayersGrid = new Grid<>(Player.class, false);
        selectedPlayersGrid.addColumn(Player::getPlayerName).setHeader("Name").setSortable(true);
        selectedPlayersGrid.addColumn(Player::getEmail).setHeader("Email").setSortable(true);
        selectedPlayersGrid.setWidth("45%");

        List<Player> selectedPlayers = new ArrayList<>();
        availablePlayersGrid.addSelectionListener(selection -> selection.getFirstSelectedItem().ifPresent(player -> {
            if (!selectedPlayers.contains(player)) {
                selectedPlayers.add(player);
                selectedPlayersGrid.setItems(selectedPlayers);
            }
        }));

        selectedPlayersGrid.addSelectionListener(selection -> selection.getFirstSelectedItem().ifPresent(player -> {
            selectedPlayers.remove(player);
            selectedPlayersGrid.setItems(selectedPlayers);
        }));

        HorizontalLayout playerSelectionLayout = new HorizontalLayout(availablePlayersGrid, selectedPlayersGrid);
        playerSelectionLayout.setSpacing(true);
        playerSelectionLayout.setWidthFull();

        // Buttons
        Button saveButton = new Button("Save", event -> {
            String name = tournamentNameField.getValue();
            String rules = rulesField.getValue();
            String type = tournamentTypeComboBox.getValue();

            if (name.isEmpty() || type == null || selectedPlayers.isEmpty()) {
                Notification.show("Please fill in all required fields and select players.");
                return;
            }

//            tournamentService.save(new Tournament(name, selectedPlayers.size(), rules, "Pending", type, selectedPlayers));
            Notification.show("Tournament created successfully!");
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", event -> getUI().ifPresent(ui -> ui.navigate(TournamentsView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setSpacing(true);

        // Add components to the layout
        add(tournamentDetailsLayout, playerSelectionLayout, buttonLayout);
    }
}
