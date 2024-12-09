package org.cedacri.pingpong.views.tournament;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Turneu")
@Route(value = "turneu", layout = MainLayout.class)
public class TournamentView extends VerticalLayout {

    @Autowired
    private TournamentService tournamentService;
    private Grid<Tournament> grid = new Grid<>(Tournament.class, false);

    private Button startTournamentButton = new Button("STARTEAZA TURNEU");

    public TournamentView() {
        setSizeFull();
        H3 title = new H3("Lista de Turnee");
        title.getStyle().set("margin-top", "0");

        configureGrid();
        updateGrid();

        HorizontalLayout headerLayout = new HorizontalLayout(title, startTournamentButton);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.expand(title);
        headerLayout.setPadding(true);

        add(headerLayout, grid);
        setFlexGrow(1, grid);
    }

    private void configureGrid() {
        // Trophy image column
        grid.addComponentColumn(t -> {
            Image trophy = new Image("images/trophy.png", "Trophy");
            trophy.setWidth("40px");
            return trophy;
        }).setAutoWidth(true).setFlexGrow(0);

        grid.addColumn(Tournament::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Tournament::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Tournament::getPlayers).setHeader("Players").setAutoWidth(true);
        grid.addColumn(Tournament::getRules).setHeader("Rules").setAutoWidth(true);
        grid.addColumn(Tournament::getWinner).setHeader("Winner").setAutoWidth(true);

        grid.addComponentColumn(t -> {
            Button details = new Button("Detalii");
            details.addClickListener(e -> {
                // Navigate to the details page with the tournament ID
                UI.getCurrent().navigate("tournament-details/" + t.getId());
            });

            Button delete = new Button(new Icon(VaadinIcon.TRASH));
            delete.addClickListener(e -> {
                tournamentService.deleteTournament(t.getId());
                updateGrid();
            });

            HorizontalLayout actions = new HorizontalLayout(details, delete);
            return actions;
        }).setHeader("").setAutoWidth(true);
    }

    private void updateGrid() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        grid.setItems(tournaments);
    }
}