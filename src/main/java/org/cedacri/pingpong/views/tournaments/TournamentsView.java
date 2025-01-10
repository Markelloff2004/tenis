package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.interfaces.TournamentManagement;

@PageTitle("TournamentsView")
@Route(value = "tournaments", layout = MainLayout.class)
//@Menu(order = 1, icon = LineAwesomeIconUrl.PEOPLE_CARRY_SOLID)
@Uses(Icon.class)
public class TournamentsView extends VerticalLayout implements TournamentManagement
{

    private final Grid<Tournament> tournamentsGrid = new Grid<>(Tournament.class, false);;
    private final TournamentService tournamentService;

    public TournamentsView(TournamentService tournamentService) {
        this.tournamentService = tournamentService;

        configureView();
        configureGrid();

        refreshGridData();
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
                () -> showCreateTournament()
        );

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.END, addTournamentButton);

        add(title, buttonLayout, tournamentsGrid);

    }

    private void configureGrid()
    {
        tournamentsGrid.addClassName("tournaments-grid");
        tournamentsGrid.setSizeFull();
        
        tournamentsGrid.addColumn(Tournament::getTournamentName).setHeader("Name").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getMaxPlayers).setHeader("MaxPlayers").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentType).setHeader("Type").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentStatus).setHeader("Status").setSortable(true);
//        tournamentsGrid.addColumn(Tournament::getCreatedAt).setHeader("CreatedAt").setSortable(true);

        tournamentsGrid
                .addColumn(new ComponentRenderer<>(tournament -> createActionButtons(tournament)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        refreshGridData();
    }

    private HorizontalLayout createActionButtons(Tournament tournament)
    {
        Button viewButton = ViewUtils.createButton("View", "compact-button", () -> showInfoTournament(tournament));

        Button editButton = ViewUtils.createButton("Edit", "compact-button", () -> showEditTournament(tournament));

        Button deleteButton = ViewUtils.createButton("Delete", "compact-button", () -> showDeleteTournament(tournament));

        return ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, viewButton, editButton, deleteButton);
    }

    private void refreshGridData() {
        tournamentsGrid.setItems(tournamentService.findAll().toList());
    }

    @Override
    public void showCreateTournament()
    {
        getUI().ifPresent(ui -> ui.navigate("tournaments/add"));
    }

    @Override
    public void showInfoTournament(Tournament tournamentDetails) {
        getUI().ifPresent(ui -> ui.navigate("tournament/general-details/" + tournamentDetails.getId()));
    }

    @Override
    public void showEditTournament(Tournament tournamentEdit) {
        Notification.show("Edit functional hasn't created yet.", 5000, Notification.Position.MIDDLE );
    }

    @Override
    public void showDeleteTournament(Tournament tournamentDelete) {
        Dialog confirmDeleteTournamentDialog = new Dialog("Delete Tournament");
        confirmDeleteTournamentDialog.add("Are you sure you want to delete the tournament \"" + tournamentDelete.getTournamentName() + "\"?");

        Button confirmButton = ViewUtils.createButton(
                "Delete",
                "colored-button",
                () -> {
                    try {
                        tournamentService.delete(tournamentDelete.getId());
                        refreshGridData();
                        confirmDeleteTournamentDialog.close();
                        Notification.show("Tournament deleted successfully!");
                    } catch (Exception e) {
                        Notification.show("Error deleting tournament: " + e.getMessage());
                    }
                }
        );

        Button cancelButton = ViewUtils.createButton("Cancel", "button", () -> confirmDeleteTournamentDialog.close());

        HorizontalLayout dialogButtons = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, confirmButton, cancelButton);
        confirmDeleteTournamentDialog.add(dialogButtons);

        confirmDeleteTournamentDialog.open();
    }
}
