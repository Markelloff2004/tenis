package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.Constraints;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.interfaces.TournamentManagement;
import org.cedacri.pingpong.views.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@PageTitle("TournamentsView")
@Route(value = "tournaments", layout = MainLayout.class)
@Uses(Icon.class)
public class TournamentsView extends VerticalLayout implements TournamentManagement
{

    private static final Logger logger = LoggerFactory.getLogger(TournamentsView.class);

    private final Grid<Tournament> tournamentsGrid = new Grid<>(Tournament.class, false);
    private final TournamentService tournamentService;
    private final PlayerService playerService;

    public TournamentsView(TournamentService tournamentService, PlayerService playerService) {
        this.tournamentService = tournamentService;

        configureView();
        configureGrid();

        refreshGridData();
        this.playerService = playerService;

        logger.info("TournamentsView initialized");
    }

    private void configureView()
    {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Tournaments list");
        title.addClassName("tournaments-title");

        Button addTournamentButton = ViewUtils.createButton(
                "Add Tournament",
                "colored-button",
                this::showCreateTournament
        );

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, addTournamentButton);

        add(title, buttonLayout, tournamentsGrid);
        logger.debug("TournamentsView configured: title and button added");

    }

    private void configureGrid()
    {
        tournamentsGrid.addClassName("tournaments-grid");
        tournamentsGrid.setSizeFull();

        tournamentsGrid.addColumn(Tournament::getId).setHeader("ID").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentName).setHeader("Name").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getMaxPlayers).setHeader("MaxPlayers").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentType).setHeader("Type").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentStatus).setHeader("Status").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getCreatedAt).setHeader("CreatedAt").setSortable(true);

        tournamentsGrid
                .addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        refreshGridData();

        logger.debug("TournamentsView grid configured with columns and actions");
    }

    private HorizontalLayout createActionButtons(Tournament tournament)
    {
        Button viewButton = ViewUtils.createButton("View", "compact-button", () -> showInfoTournament(tournament));

        Button editButton = ViewUtils.createButton("Edit", "compact-button", () ->
        {
            if(tournament.getTournamentStatus().equals(Constraints.STATUS_PENDING)) {
                showEditTournament(tournament);
            }
            else{
                NotificationManager.showInfoNotification("This tournament isn't in Pending status!");
            }
        });

        Button deleteButton = ViewUtils.createButton("Delete", "compact-button", () -> showDeleteTournament(tournament));

        return ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, viewButton, editButton, deleteButton);
    }

    private void refreshGridData() {
        tournamentsGrid.setItems(tournamentService.findAll().collect(Collectors.toSet()));
        logger.info("Grid data refreshed, tournaments loaded.");
    }

    @Override
    public void showCreateTournament()
    {
        getUI().ifPresent(ui -> {
            logger.info("Navigating to create tournament page");
            ui.navigate("tournaments/add");
        });
    }

    @Override
    public void showInfoTournament(Tournament tournamentDetails) {
        getUI().ifPresent(ui -> ui.navigate("tournament/general-details/" + tournamentDetails.getId()));
        logger.info("Navigating to tournament details page, id: {}", tournamentDetails.getId());
    }

    @Override
    public void showDeleteTournament(Tournament tournamentDelete) {
        Dialog confirmDeleteTournamentDialog = new Dialog("Delete Tournament");
        confirmDeleteTournamentDialog.add("Are you sure you want to delete the tournament \"" + tournamentDelete.getTournamentName() + "\"?");

        Button confirmButton = ViewUtils.createButton(
                "Delete",
                "colored-button",
                () -> {
                    int id = tournamentDelete.getId();
                    try {
                        tournamentService.deleteById(id);
                        refreshGridData();
                        confirmDeleteTournamentDialog.close();
                        NotificationManager.showInfoNotification("Tournament deleted successfully! id: " + id);
                        logger.info("Tournament deleted successfully! id: {}", id);
                    } catch (Exception e) {
                        NotificationManager.showInfoNotification("Error deleting tournament: " + e.getMessage());
                        logger.error("Error deleting tournament: {}, {}", id, e.getMessage());
                    }
                }
        );

        Button cancelButton = ViewUtils.createButton("Cancel", "button", confirmDeleteTournamentDialog::close);

        HorizontalLayout dialogButtons = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, confirmButton, cancelButton);
        confirmDeleteTournamentDialog.add(dialogButtons);

        confirmDeleteTournamentDialog.open();
    }

    @Override
    public void showEditTournament(Tournament tournament){
            Dialog editDialog = new Dialog();
            editDialog.setHeaderTitle("Edit Tournament");

            TextField nameField = new TextField("Name");
            nameField.setValue(tournament.getTournamentName());

            ComboBox<String> typeComboBox = new ComboBox<>("Type");
            typeComboBox.setItems(Arrays.asList(TournamentTypeEnum.values())
                    .stream()
                    .map(v -> v.toString())
                    .collect(Collectors.toSet()));
            typeComboBox.setValue(tournament.getTournamentType().toString());
            typeComboBox.setWidthFull();

            ComboBox<String> setsCountComboBox = new ComboBox<>("Sets Count");
            setsCountComboBox.setItems(Arrays.asList(SetTypesEnum.values())
                    .stream()
                    .map(st -> st.toString())
                    .collect(Collectors.toSet()));
            setsCountComboBox.setValue(tournament.getSetsToWin().toString());
            setsCountComboBox.setWidthFull();

        ComboBox<String> semifinalsSetsCountComboBox= new ComboBox<>("Semifinals Sets Count");
        semifinalsSetsCountComboBox.setItems(Arrays.asList(SetTypesEnum.values())
                .stream()
                .map(st -> st.toString())
                .collect(Collectors.toSet()));
        semifinalsSetsCountComboBox.setValue(tournament.getSemifinalsSetsToWin().toString());
        semifinalsSetsCountComboBox.setWidthFull();

        ComboBox<String> finalsSetsCountComboBox = new ComboBox<>("Finals Sets Count");
        finalsSetsCountComboBox.setItems(Arrays.asList(SetTypesEnum.values())
                .stream()
                .map(st -> st.toString())
                .collect(Collectors.toSet()));
        finalsSetsCountComboBox.setValue(tournament.getFinalsSetsToWin().toString());
        finalsSetsCountComboBox.setWidthFull();

            ComboBox<String> statusComboBox = new ComboBox<>("Status");
            statusComboBox.setItems(Constraints.TOURNAMENT_STATUSES);
            statusComboBox.setValue(tournament.getTournamentStatus().toString());

            Set<Player> selectedPlayersSet = new HashSet<>(tournament.getPlayers());

            Set<Player> availablePlayersSet = playerService.getAll()
                    .filter(p -> !selectedPlayersSet.contains(p))
                    .collect(Collectors.toSet());

            logger.debug("Edit tournament dialog initialized for tournament: {}", tournament.getId());

            Grid<Player> selectedPlayersGrid = new Grid<>(Player.class, false);
            Grid<Player> availablePlayersGrid = new Grid<>(Player.class, false);

            selectedPlayersGrid.setItems(selectedPlayersSet);
            availablePlayersGrid.setItems(availablePlayersSet);

            Runnable refreshGrids = () -> {
                selectedPlayersGrid.setItems(selectedPlayersSet);
                availablePlayersGrid.setItems(availablePlayersSet);
            };

            GridUtils.configurePlayerGrid(selectedPlayersGrid, selectedPlayersSet, availablePlayersSet, "Remove", refreshGrids);
            GridUtils.configurePlayerGrid(availablePlayersGrid, availablePlayersSet, selectedPlayersSet, "Add", refreshGrids);

            HorizontalLayout playersLayout = ViewUtils.
                    createHorizontalLayout(JustifyContentMode.BETWEEN, selectedPlayersGrid, availablePlayersGrid);

            VerticalLayout dialogLayout = new VerticalLayout(nameField, typeComboBox, statusComboBox, setsCountComboBox,
                    semifinalsSetsCountComboBox, finalsSetsCountComboBox, playersLayout);
            dialogLayout.setSpacing(true);

            if(tournament.getTournamentStatus().equals(Constraints.STATUS_PENDING)){
                Grid<String> playersGrid = new Grid<>(String.class, false);
                playersGrid.addColumn(player -> player).setHeader("Player name");

                Dialog addPlayerDialog = new Dialog();
                addPlayerDialog.setHeaderTitle("Add Player");
            }


            Button saveButton = new Button("Save", event -> {
                try{
                    tournament.setTournamentName(nameField.getValue());
                    tournament.setTournamentType(TournamentTypeEnum.valueOf(typeComboBox.getValue().toUpperCase()));
                    tournament.setTournamentStatus(TournamentStatusEnum.valueOf(statusComboBox.getValue().toUpperCase()));
                    tournament.setPlayers(selectedPlayersSet);
                    tournament.setSetsToWin(SetTypesEnum.valueOf(setsCountComboBox.getValue().toUpperCase()));
                    tournament.setSemifinalsSetsToWin(SetTypesEnum.valueOf(semifinalsSetsCountComboBox.getValue().toUpperCase()));
                    tournament.setFinalsSetsToWin(SetTypesEnum.valueOf(finalsSetsCountComboBox.getValue().toUpperCase()));

                    tournamentService.saveTournament(tournament);
                    editDialog.close();
                    Notification.show(Constraints.TOURNAMENT_UPDATE_SUCCESS,
                            5000, Notification.Position.BOTTOM_CENTER);
                    refreshGridData();

                    logger.info("Tournament updated successfully: {}", tournament.getId());
                }
                catch (Exception e){
                    Notification.show(Constraints.TOURNAMENT_UPDATE_ERROR
                            + "\n" + e.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
                    logger.error("Error updating tournament: {}", tournament.getId());
                }
            });
            saveButton.addClassName("button");

            Button cancelButton = new Button("Cancel", event -> editDialog.close());
            cancelButton.addClassName("button");

            HorizontalLayout dialogButtons = new HorizontalLayout(saveButton, cancelButton);
            dialogButtons.setSpacing(true);
            dialogButtons.setJustifyContentMode(JustifyContentMode.END);

            editDialog.add(dialogLayout, playersLayout, dialogButtons);
            editDialog.setWidth("50%");
            editDialog.open();

    }

}
