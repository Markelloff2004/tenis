package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;

public class TournamentSummaryComponent extends HorizontalLayout {

    TournamentService tournamentService;

    public TournamentSummaryComponent(TournamentService tournamentService, Tournament tournament) {
        this.tournamentService = tournamentService;

        setWidthFull();
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        getStyle().set("background-color", "#f0f0f0").set("border-radius", "8px").set("padding", "10px");

        add(
                createTournamentDetails(tournament),
                createDateCreatedDetails(tournament),
                createWinnerDetails(tournament)
        );
    }

    private VerticalLayout createTournamentDetails(Tournament tournament) {
        VerticalLayout details = new VerticalLayout();
        details.setMaxWidth("250px");
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Span name = new Span("🏆 " + tournament.getTournamentName());
        name.getStyle().set("font-weight", "bold");

        details.add(name);
        return details;
    }

    private VerticalLayout createDateCreatedDetails(Tournament tournament) {
        VerticalLayout details = new VerticalLayout();
        details.setMaxWidth("150px");
        details.setAlignItems(Alignment.CENTER);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Span dateCreated = new Span("📅 " + tournament.getCreatedAt().toString());
        details.add(dateCreated);
        return details;
    }

    private VerticalLayout createWinnerDetails(Tournament tournament) {
        VerticalLayout details = new VerticalLayout();
        details.setMaxWidth("250px");
        details.setAlignItems(Alignment.END);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Player winner = tournament.getWinner();
        Span winnerLabel = (winner != null)
                ? ViewUtils.createPlayerLabel("🥇 " + winner.getName() + " " + winner.getSurname())
                : ViewUtils.createPlayerLabel("No winner yet");

        details.add(winnerLabel);
        return details;
    }


}
