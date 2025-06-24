package org.cedacri.pingpong.views.tournaments.v2;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.config.security.model.enums.RoleEnum;
import org.cedacri.pingpong.model.enums.TournamentStatusEnum;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.service.tournaments.UnifiedTournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.tournaments.v2.components.dialogs.TournamentAddDialog;
import org.cedacri.pingpong.views.tournaments.v2.components.dialogs.TournamentDeleteDialog;
import org.cedacri.pingpong.views.tournaments.v2.components.dialogs.TournamentEditDialog;
import org.cedacri.pingpong.views.tournaments.v2.components.dialogs.TournamentInfoDialog;

@Slf4j
@PageTitle("TournamentsView")
@Route(value = "tournaments", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
public class AllTournamentsView extends VerticalLayout {

    // This class is currently empty, but it can be used to view all tournaments into a Grid or Table.
    // And with action buttons to add, edit, delete tournaments.

    private final Grid<BaseTournament> tournamentsGrid = new Grid<>(BaseTournament.class, false);
    private final transient PlayerService playerService;
    private final transient UnifiedTournamentService tournamentService;

    public AllTournamentsView(UnifiedTournamentService tournamentService, PlayerService playerService) {
        this.tournamentService = tournamentService;
        this.playerService = playerService;

        configureView();
        configureGrid();

        refreshGridData();

        log.info("TournamentsView initialized");
    }

    private void configureView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Tournaments list");
        title.addClassName("tournaments-title");

        Button addTournamentButton = ViewUtils.createSecuredButton(
                "Add Tournament",
                ViewUtils.COLORED_BUTTON,
                this::showCreateTournament,
                RoleEnum.ADMIN, RoleEnum.MANAGER
        );

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.END, addTournamentButton);

        add(title, buttonLayout, tournamentsGrid);
        log.debug("TournamentsView configured: title and button added");

    }

    private void configureGrid() {
        tournamentsGrid.addClassName("tournaments-grid");
        tournamentsGrid.setSizeFull();

        tournamentsGrid.addColumn(BaseTournament::getId).setHeader("ID").setSortable(true);
        tournamentsGrid.addColumn(BaseTournament::getTournamentName).setHeader("Name").setSortable(true);
        tournamentsGrid.addColumn(BaseTournament::getMaxPlayers).setHeader("MaxPlayers").setSortable(true);
        tournamentsGrid.addColumn(BaseTournament::getTournamentType).setHeader("Type").setSortable(true);
        tournamentsGrid.addColumn(BaseTournament::getTournamentStatus).setHeader("Status").setSortable(true);
        tournamentsGrid.addColumn(BaseTournament::getCreatedAt).setHeader("CreatedAt").setSortable(true);

        tournamentsGrid
                .addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        refreshGridData();

        log.debug("TournamentsView grid configured with columns and actions");
    }

    private HorizontalLayout createActionButtons(BaseTournament baseTournament) {
        Button viewButton = ViewUtils.createButton(
                "View",
                ViewUtils.COMPACT_BUTTON,
                () -> showInfoTournament(baseTournament)
        );

        Button deleteButton = ViewUtils.createSecuredButton(
                "Delete",
                ViewUtils.COMPACT_BUTTON,
                () -> showDeleteTournament(baseTournament),
                RoleEnum.ADMIN
        );

        HorizontalLayout layout;

        if (baseTournament.getTournamentStatus() != null
                && baseTournament.getTournamentStatus().equals(TournamentStatusEnum.PENDING)) {

            Button editButton = ViewUtils.createSecuredButton(
                    "Edit",
                    ViewUtils.COMPACT_BUTTON,
                    () -> showEditTournament(baseTournament),
                    RoleEnum.ADMIN, RoleEnum.MANAGER
            );

            layout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, viewButton, editButton, deleteButton);

        } else {
            layout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, viewButton, deleteButton);
        }

        return layout;
    }

    private void refreshGridData() {
        tournamentsGrid.setItems(tournamentService.getDefaultCrudService().findAllTournaments());

        log.info("Grid data refreshed, tournaments loaded.");
    }


    public void showCreateTournament() {
        log.info("Saving a new tournament:");

        TournamentAddDialog tournamentAddDialog = new TournamentAddDialog(tournamentService, playerService, this::refreshGridData);
        tournamentAddDialog.open();
        log.info("Tournament creation dialog opened.");
    }

    public void showInfoTournament(BaseTournament baseTournamentDetails) {
        log.info("Showing tournament details for tournament: {} with Id: {}", baseTournamentDetails.getTournamentName(), baseTournamentDetails.getId());

        TournamentInfoDialog tournamentInfoDialog = new TournamentInfoDialog(playerService, baseTournamentDetails);
        tournamentInfoDialog.open();

        log.info("Tournament info dialog opened for tournament: {} with Id: {}", baseTournamentDetails.getTournamentName(), baseTournamentDetails.getId());

    }

    public void showDeleteTournament(BaseTournament baseTournamentToDelete) {
        log.info("Deleting tournament: {} with Id: {}", baseTournamentToDelete.getTournamentName(), baseTournamentToDelete.getId());

        TournamentDeleteDialog tournamentDeleteDialog = new TournamentDeleteDialog(tournamentService, baseTournamentToDelete, this::refreshGridData);
        tournamentDeleteDialog.open();

        log.info("Tournament deletion dialog opened.");
    }

    public void showEditTournament(BaseTournament baseTournament) {
        log.info("Editing tournament: {} with Id: {}", baseTournament.getTournamentName(), baseTournament.getId());

        TournamentEditDialog tournamentEditDialog = new TournamentEditDialog(tournamentService, playerService, baseTournament, this::refreshGridData);
        tournamentEditDialog.open();

        log.info("Tournament editing dialog opened.");
    }


}
