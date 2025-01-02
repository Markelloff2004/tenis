package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;

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
//            openTournamentDialog();
            getUI().ifPresent(ui -> ui.navigate("tournaments/add"));
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


    private void configureGrid()
    {
        tournamentsGrid.addColumn(Tournament::getTournamentName).setHeader("Name").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getMaxPlayers).setHeader("MaxPlayers").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentType).setHeader("Type").setSortable(true);
        tournamentsGrid.addColumn(Tournament::getTournamentStatus).setHeader("Status").setSortable(true);
//        tournamentsGrid.addColumn(Tournament::getCreatedAt).setHeader("CreatedAt").setSortable(true);

        tournamentsGrid.addColumn(new ComponentRenderer<>(tournament -> {
            HorizontalLayout actionsLayout = new HorizontalLayout();

            Button viewButton = new Button("View", click -> {
                getUI().ifPresent(ui -> ui.navigate("tournament/general-details/" + tournament.getId()));
            });
            viewButton.addClassName("compact-button");

            Button editButton = new Button("Edit", click -> {
//                openEditPlayerDialog(tournament);
            });
            editButton.addClassName("compact-button");

            Button deleteButton = new Button("Delete", click -> {
                deleteTournament(tournament);
            });
            deleteButton.addClassName("compact-button");

            actionsLayout.add(viewButton, editButton, deleteButton);
            return actionsLayout;
        })).setHeader("Actions").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
    }

    private void deleteTournament(Tournament tournament) {
        Dialog confirmDialog = new Dialog();

        confirmDialog.setHeaderTitle("Delete Tournament");
        confirmDialog.add("Are you sure you want to delete the tournament \"" + tournament.getTournamentName() + "\"?");

        Button confirmButton = new Button("Delete", event -> {
            try {
                tournamentService.delete(tournament.getId());
                confirmDialog.close();
                Notification.show("Tournament \"" + tournament.getTournamentName() + "\" deleted successfully!");
                refreshGridData();
            } catch (Exception e) {
                Notification.show("Error deleting tournament: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        confirmButton.addClassName("colored-button");

        Button cancelButton = new Button("Cancel", event -> confirmDialog.close());
        cancelButton.addClassName("button");

        HorizontalLayout dialogButtons = new HorizontalLayout(confirmButton, cancelButton);
        dialogButtons.setSpacing(true);
        dialogButtons.getStyle().set("margin-top", "30px");
        confirmDialog.add(dialogButtons);

        confirmDialog.open();
    }

    private void refreshGridData() {
//        tournamentsGrid.getDataProvider().refreshAll();
        // playerService.getAllPlayers()
        tournamentsGrid.setItems(tournamentService.findAll().toList());
    }
}
