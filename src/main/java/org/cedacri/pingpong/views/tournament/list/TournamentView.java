package org.cedacri.pingpong.views.tournament.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.TournamentRepository;

import java.util.stream.Collectors;

@Route("tournaments")
@PageTitle("Tournament List")
public class TournamentView extends VerticalLayout {

    private final TournamentRepository tournamentRepository;
    private final Grid<Tournament> grid = new Grid<>(Tournament.class);
    private final TextField nameField = new TextField("Name");
    private final TextField maxPlayersField = new TextField("Max Players");
    private final TextField rulesField = new TextField("Rules");
    private final TextField statusField = new TextField("Status");
    private final TextField typeField = new TextField("Type");
    private Tournament selectedTournament;

    public TournamentView(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
        setSizeFull();
        configureGrid();
        add(createFormLayout(), grid);
        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("tournamentName", "maxPlayers", "tournamentStatus", "tournamentType");

        // Adăugăm butonul de detalii pentru fiecare rând
        grid.addComponentColumn(tournament -> {
            Button detailsButton = new Button("Detalii", event -> navigateToBracketView(tournament));
            Button editButton = new Button("Edit", event -> editTournament(tournament));
            Button deleteButton = new Button("Delete", event -> deleteTournament(tournament));
            return new HorizontalLayout(detailsButton, editButton, deleteButton);
        }).setHeader("Actions");

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        // Eveniment pentru selectarea unui turneu
        grid.addSelectionListener(event -> {
            selectedTournament = event.getFirstSelectedItem().orElse(null);
            if (selectedTournament != null) {
                populateForm(selectedTournament);
            }
        });
    }

    private HorizontalLayout createFormLayout() {
        Button addButton = new Button("Add", event -> saveTournament());
        Button updateButton = new Button("Update", event -> updateTournament());
        Button deleteButton = new Button("Delete", event -> deleteTournament(selectedTournament));

        return new HorizontalLayout(nameField, maxPlayersField, rulesField, statusField, typeField, addButton, updateButton, deleteButton);
    }

    private void updateGrid() {
        grid.setItems(tournamentRepository.getAll().collect(Collectors.toList()));
    }

    private void populateForm(Tournament tournament) {
        nameField.setValue(tournament.getTournamentName());
        maxPlayersField.setValue(String.valueOf(tournament.getMaxPlayers()));
        statusField.setValue(tournament.getTournamentStatus());
        typeField.setValue(tournament.getTournamentType());
    }

    private void clearForm() {
        nameField.clear();
        maxPlayersField.clear();
        rulesField.clear();
        statusField.clear();
        typeField.clear();
        selectedTournament = null;
    }

    private void saveTournament() {
        try {
            Tournament tournament = new Tournament();
            tournament.setTournamentName(nameField.getValue());
            tournament.setMaxPlayers(Integer.parseInt(maxPlayersField.getValue()));
            tournament.setTournamentStatus(statusField.getValue());
            tournament.setTournamentType(typeField.getValue());
            tournament.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()).toInstant());

            tournamentRepository.getAll()
                    .filter(existing -> existing.getTournamentName().equalsIgnoreCase(tournament.getTournamentName()))
                    .findAny()
                    .ifPresentOrElse(existing -> Notification.show("Tournament already exists!"),
                            () -> {
                                tournamentRepository.getAll();
                            });

            Notification.show("Tournament added successfully!");
            updateGrid();
            clearForm();
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }

    private void editTournament(Tournament tournament) {
        // Populăm formularul cu detaliile turneului selectat
        populateForm(tournament);
        selectedTournament = tournament;
    }

    private void updateTournament() {
        if (selectedTournament == null) {
            Notification.show("Select a tournament to update.");
            return;
        }

        try {
            selectedTournament.setTournamentName(nameField.getValue());
            selectedTournament.setMaxPlayers(Integer.parseInt(maxPlayersField.getValue()));
            selectedTournament.setTournamentStatus(statusField.getValue());
            selectedTournament.setTournamentType((typeField.getValue()));

            Notification.show("Tournament updated successfully!");
            updateGrid();
            clearForm();
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }

    private void deleteTournament(Tournament tournament) {
        if (tournament == null) {
            Notification.show("Select a tournament to delete.");
            return;
        }

        try {
            tournamentRepository.getAll()
                    .filter(t -> !t.getId().equals(tournament.getId()))
                    .collect(Collectors.toList());

            Notification.show("Tournament deleted successfully!");
            updateGrid();
            clearForm();
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }

    // Metoda pentru navigare către pagina TournamentBracketView
    private void navigateToBracketView(Tournament tournament) {
        // Redirecționăm utilizatorul către TournamentBracketView cu ID-ul turneului
        getUI().ifPresent(ui -> ui.navigate("tournaments/" + tournament.getId()));
    }
}
