package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@Slf4j
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "home", layout = MainLayout.class)
@AnonymousAllowed
@PermitAll
public class HomeView extends VerticalLayout
{

    private final TournamentService tournamentService;
    private final VerticalLayout tournamentContainer = new VerticalLayout();

    @Autowired
    public HomeView(TournamentService tournamentService)
    {
        this.tournamentService = tournamentService;
        log.info("HomeView initialized.");

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(ViewUtils.createHorizontalLayout(FlexComponent.JustifyContentMode.BETWEEN, new H1("Home")));
        tournamentContainer.setSpacing(true);
        tournamentContainer.setHorizontalComponentAlignment(Alignment.STRETCH);
        add(tournamentContainer);

        displayTournaments();
    }

    private void displayTournaments()
    {
        List<TournamentOlympic> tournamentOlympics = tournamentService.findAllTournaments()
                .sorted(
                        Comparator.comparing(
                                        TournamentOlympic::getStartedAt,
                                        Comparator.nullsFirst(Comparator.naturalOrder())
                                )
                                .thenComparing(
                                        TournamentOlympic::getCreatedAt,
                                        Comparator.nullsFirst(Comparator.naturalOrder())
                                )
                                .reversed()
                )
                .toList();
        log.info("Displaying {} tournaments", tournamentOlympics.size());

        tournamentContainer.removeAll();

        if (tournamentOlympics.isEmpty())
        {
            showNoTournamentFound();
            return;
        }

        tournamentOlympics.forEach(tournament ->
        {
            log.debug("Processed tournament {}", tournament);
            tournamentContainer.add(new TournamentSummaryComponent(tournament));
        });
    }

    private void showNoTournamentFound()
    {
        H1 noTournamentsMessage = new H1("There are currently no tournaments");
        noTournamentsMessage.getStyle().set("text-align", "center").set("width", "100%").set("color", "gray");
        tournamentContainer.setAlignItems(Alignment.CENTER);
        tournamentContainer.add(noTournamentsMessage);
    }
}



