package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.utils.ViewUtils;

import java.time.LocalDate;

@Slf4j
public class TournamentSummaryComponent extends HorizontalLayout {

    public TournamentSummaryComponent(BaseTournament baseTournament) {
        setWidthFull();
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        getStyle()
                .set("background-color", "#f9f9f9")
                .set("border-radius", "12px")
                .set("padding", "15px")
                .set("box-shadow", "0px 4px 6px rgba(0, 0, 0, 0.1)");

        add(
                createTournamentDetails(baseTournament),
                createDateCreatedDetails(baseTournament),
                createWinnerDetails(baseTournament),
                createInfoButton(baseTournament)
        );
    }

    private VerticalLayout createTournamentDetails(BaseTournament baseTournament) {
        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Span name = new Span("🏆 " + baseTournament.getTournamentName());
        name.getStyle().set("font-weight", "bold").set("font-size", "16px");

        details.add(name);
        return details;
    }

    private VerticalLayout createDateCreatedDetails(BaseTournament baseTournament) {
        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        String startedAtInfo;
        LocalDate startedAt = baseTournament.getStartedAt();
        if (startedAt == null) {
            startedAtInfo = "Pending";
        } else {
            startedAtInfo = startedAt.toString();
        }

        Span dateCreated = new Span("📅 " + startedAtInfo);
        dateCreated.getStyle().set("font-size", "16px");
        details.add(dateCreated);
        return details;
    }

    private VerticalLayout createWinnerDetails(BaseTournament baseTournament) {
        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Player winner = baseTournament.getWinner();
        Span winnerLabel = (winner != null)
                ? ViewUtils.createPlayerLabel("🥇 " + winner.getName() + " " + winner.getSurname())
                : ViewUtils.createPlayerLabel("No winner yet");

        winnerLabel.getStyle().set("font-size", "16px");
        details.add(winnerLabel);
        return details;
    }

    private Button createInfoButton(BaseTournament baseTournament) {
        Button infoButton = new Button("Show Details", event ->
        {
            getUI().ifPresent(ui -> ui.navigate("tournament/matches/" + baseTournament.getId()));
            log.info("Navigating to tournament details page, id: {}", baseTournament.getId());
        });
        infoButton.addClassName("compact-button");
        infoButton.getStyle().set("font-size", "16px");

        return infoButton;
    }
}
