package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.enums.RoleEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.interfaces.TournamentManagement;
import org.cedacri.pingpong.views.tournaments.components.TournamentAddDialog;
import org.cedacri.pingpong.views.tournaments.components.TournamentDeleteDialog;
import org.cedacri.pingpong.views.tournaments.components.TournamentEditDialog;
import org.cedacri.pingpong.views.tournaments.components.TournamentInfoDialog;

import java.util.Comparator;


@Slf4j
@PageTitle("TournamentsView")
@Route(value = "tournaments", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
public class TournamentsView extends VerticalLayout implements TournamentManagement
{

    private final Grid<BaseTournament> tournamentsGrid = new Grid<>(BaseTournament.class, false);
    private final TournamentService tournamentService;
    private final PlayerService playerService;

    public TournamentsView(TournamentService tournamentService, PlayerService playerService)
    {
        this.tournamentService = tournamentService;
        this.playerService = playerService;

        configureView();
        configureGrid();

        refreshGridData();

        log.info("TournamentsView initialized");
    }

    private void configureView()
    {
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

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, addTournamentButton);

        add(title, buttonLayout, tournamentsGrid);
        log.debug("TournamentsView configured: title and button added");

    }

    private void configureGrid()
    {
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

    private HorizontalLayout createActionButtons(BaseTournament baseTournament)
    {
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
                && baseTournament.getTournamentStatus().equals(TournamentStatusEnum.PENDING))
        {

            Button editButton = ViewUtils.createSecuredButton(
                    "Edit",
                    ViewUtils.COMPACT_BUTTON,
                    () -> showEditTournament(baseTournament),
                    RoleEnum.ADMIN, RoleEnum.MANAGER
            );

            layout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, editButton, viewButton, deleteButton);

        }
        else
        {
            layout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, viewButton, deleteButton);
        }

        return layout;
    }

    private void refreshGridData()
    {
        tournamentsGrid.setItems(
                tournamentService.findAllTournaments()
                        .stream()
                        .sorted(Comparator.comparingLong(BaseTournament::getId).reversed())
                        .toList());
        log.info("Grid data refreshed, tournaments loaded.");
    }

    @Override
    public void showCreateTournament()
    {
        log.info("Saving a new tournament:");

        TournamentAddDialog tournamentAddDialog = new TournamentAddDialog(tournamentService, playerService, this::refreshGridData);
        tournamentAddDialog.open();
    }

    @Override
    public void showInfoTournament(BaseTournament baseTournamentDetails)
    {
        log.info("Showing tournament details for tournament: {} with Id: {}", baseTournamentDetails.getTournamentName(), baseTournamentDetails.getId());

        TournamentInfoDialog tournamentEditDialog = new TournamentInfoDialog(playerService, baseTournamentDetails);
        tournamentEditDialog.open();

    }

    @Override
    public void showDeleteTournament(BaseTournament tournamentOlympicDelete)
    {
        log.info("Deleting tournament: {} with Id: {}", tournamentOlympicDelete.getTournamentName(), tournamentOlympicDelete.getId());

        TournamentDeleteDialog confirmDeleteTournamentDialog = new TournamentDeleteDialog(tournamentService, tournamentOlympicDelete, this::refreshGridData);
        confirmDeleteTournamentDialog.open();
    }

    @Override
    public void showEditTournament(BaseTournament baseTournament)
    {

        log.info("Editing tournament: {} with Id: {}", baseTournament.getTournamentName(), baseTournament.getId());

        TournamentEditDialog tournamentEditDialog = new TournamentEditDialog(tournamentService, playerService, baseTournament, this::refreshGridData);
        tournamentEditDialog.open();
    }

}
