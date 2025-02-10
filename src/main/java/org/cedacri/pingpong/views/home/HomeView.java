package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;

import java.util.Comparator;
import java.util.List;

@Slf4j
@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@CssImport("./themes/ping-pong-tournament/main-layout.css")
@Uses(Icon.class)
public class HomeView extends VerticalLayout {

    private final TournamentService tournamentService;

    private VerticalLayout tournamentContainer;

    public HomeView(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
        log.info("HomeView initialized.");

        initView();
    }

    private void initView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(
                ViewUtils.createHorizontalLayout(
                        JustifyContentMode.BETWEEN,
                        new H1("Home")
                )
        );

        tournamentContainer = new VerticalLayout();
        tournamentContainer.setSpacing(true);
        tournamentContainer.setHorizontalComponentAlignment(Alignment.STRETCH);
        add(tournamentContainer);

        displayTournaments(tournamentService.findAll().toList());
    }

    private void displayTournaments(List<Tournament> tournaments) {
        log.info("Displaying {} tournaments", tournaments.size());

        if (tournaments.isEmpty()) {
            showNoTournamentFound();
            return;
        }

        for (Tournament tournament : tournaments) {
            log.debug("Processed tournament {}", tournament);

            TournamentSummaryComponent tournamentLayout = new TournamentSummaryComponent(tournamentService, tournament);
            tournamentContainer.add(tournamentLayout);
        }

    }

    private void showNoTournamentFound() {
        tournamentContainer.removeAll(); // Clear any existing content

        H1 noTournamentsMessage = new H1("There are currently no tournaments");
        noTournamentsMessage.getStyle()
                .set("text-align", "center")
                .set("width", "100%")
                .set("color", "gray");

        tournamentContainer.setAlignItems(Alignment.CENTER);
        tournamentContainer.add(noTournamentsMessage);
    }

    private List<Tournament> getTournamentsList() {
        return tournamentService.findAll().sorted(Comparator.comparing(Tournament::getCreatedAt).reversed()).toList();
    }

}
