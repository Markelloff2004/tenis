package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.ViewUtils;

import java.time.LocalDate;

@Slf4j
public class TournamentSummaryComponent extends HorizontalLayout
{

    public TournamentSummaryComponent(Tournament tournament)
    {
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
                createTournamentDetails(tournament),
                createDateCreatedDetails(tournament),
                createWinnerDetails(tournament),
                createInfoButton(tournament)
        );
    }

    private VerticalLayout createTournamentDetails(Tournament tournament)
    {
        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Span name = new Span("🏆 " + tournament.getTournamentName());
        name.getStyle().set("font-weight", "bold").set("font-size", "16px");

        details.add(name);
        return details;
    }

    private VerticalLayout createDateCreatedDetails(Tournament tournament)
    {
        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        String startedAtInfo;
        LocalDate startedAt = tournament.getStartedAt();
        if (startedAt == null)
        {
            startedAtInfo = "Pending";
        }
        else
        {
            startedAtInfo = startedAt.toString();
        }

        Span dateCreated = new Span("📅 " + startedAtInfo);
        dateCreated.getStyle().set("font-size", "16px");
        details.add(dateCreated);
        return details;
    }

    private VerticalLayout createWinnerDetails(Tournament tournament)
    {
        VerticalLayout details = new VerticalLayout();
        details.setAlignItems(Alignment.START);
        details.setJustifyContentMode(JustifyContentMode.CENTER);

        Player winner = tournament.getWinner();
        Span winnerLabel = (winner != null)
                ? ViewUtils.createPlayerLabel("🥇 " + winner.getName() + " " + winner.getSurname())
                : ViewUtils.createPlayerLabel("No winner yet");

        winnerLabel.getStyle().set("font-size", "16px");
        details.add(winnerLabel);
        return details;
    }

    private Button createInfoButton(Tournament tournament)
    {
        Button infoButton = new Button("Show Details", event ->
        {
            getUI().ifPresent(ui -> ui.navigate("tournament/matches/" + tournament.getId()));
            log.info("Navigating to tournament details page, id: {}", tournament.getId());
        });
        infoButton.addClassName("compact-button");
        infoButton.getStyle().set("font-size", "16px");

        return infoButton;
    }
}
