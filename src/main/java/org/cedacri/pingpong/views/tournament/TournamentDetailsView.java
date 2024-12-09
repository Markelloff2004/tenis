package org.cedacri.pingpong.views.tournament;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PageTitle("Detalii Turneu")
@Route(value = "tournament-details/:id", layout = MainLayout.class)
public class TournamentDetailsView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private TournamentService tournamentService; // Sau injectare dacă folosești CDI/Spring
    private Tournament tournament;
    private Long tournamentId;

    // Hardcoded matches pentru demonstrație
    private List<Match> matches = Arrays.asList(
            new Match("#2 Starsii Marcu", Arrays.asList(11, 5, 14), "#1 Slonovschi Victor", Arrays.asList(6, 11, 16), "Slonovschi Victor", "#1"),
            new Match("#2 Starsii Marcu", Arrays.asList(11, 5, 14), "#3 Doroganici Andrei", Arrays.asList(6, 11, 16), "Doroganici Andrei", "#3")
    );

    public TournamentDetailsView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tournamentId = event.getRouteParameters().getLong("id").orElse(null);
        if (tournamentId == null) {
            Notification.show("ID-ul turneului este invalid!", 3000, Notification.Position.MIDDLE);
            event.forwardTo(TournamentView.class);
            return;
        }

        if (tournamentService.getTournamentById(tournamentId).isEmpty()) {
            Notification.show("Turneul nu a fost găsit!", 3000, Notification.Position.MIDDLE);
            event.forwardTo(TournamentView.class);
            return;
        }

        tournament = tournamentService.getTournamentById(tournamentId).get();

        buildView();
    }

    private void buildView() {
        // Titlu: Numele turneului și regulile
        H2 tournamentName = new H2(tournament.getName());
        H4 rules = new H4(tournament.getRules());
        HorizontalLayout titleBar = new HorizontalLayout(tournamentName, rules);
        titleBar.setWidthFull();
        titleBar.setAlignItems(Alignment.CENTER);
        titleBar.expand(tournamentName);

        // Butoanele pentru runde
        HorizontalLayout roundButtons = new HorizontalLayout();
        roundButtons.setSpacing(true);
        List<String> rounds = Arrays.asList("First Round", "Quarter Final", "SemiFinal", "Final");
        rounds.forEach(round -> roundButtons.add(createRoundButton(round)));

        add(titleBar, roundButtons);

        // Afișarea meciurilor
        matches.forEach(this::addMatchLayout);
    }

    private Button createRoundButton(String label) {
        Button btn = new Button(label);
        btn.getStyle().set("background-color", "#FFC107");
        btn.getStyle().set("color", "black");
        btn.getStyle().set("font-weight", "bold");
        return btn;
    }

    private void addMatchLayout(Match match) {
        VerticalLayout matchLayout = new VerticalLayout();
        matchLayout.setPadding(true);
        matchLayout.setSpacing(true);
        matchLayout.getStyle().set("border", "1px solid #e0e0e0");
        matchLayout.getStyle().set("border-radius", "8px");
        matchLayout.getStyle().set("background-color", "white");

        HorizontalLayout player1Line = createPlayerScoreLine(match.getPlayer1Name(), match.getPlayer1Scores());
        HorizontalLayout player2Line = createPlayerScoreLine(match.getPlayer2Name(), match.getPlayer2Scores());

        HorizontalLayout winnerLine = new HorizontalLayout();
        Span winnerLabel = new Span("Winner : ");
        Span winner = new Span(match.getWinnerRank() + " " + match.getWinnerName());
        winner.getStyle().set("font-weight", "bold");
        winnerLine.add(winnerLabel, winner);

        matchLayout.add(player1Line, player2Line, winnerLine);
        add(matchLayout);
    }

    private HorizontalLayout createPlayerScoreLine(String playerName, List<Integer> scores) {
        HorizontalLayout line = new HorizontalLayout();
        line.setAlignItems(Alignment.CENTER);
        Span playerLabel = new Span(playerName);
        line.add(playerLabel);

        for (Integer s : scores) {
            Div scoreBox = new Div();
            scoreBox.setText(String.valueOf(s));
            scoreBox.getStyle().set("background-color", "#e0e0e0");
            scoreBox.getStyle().set("padding", "5px 10px");
            scoreBox.getStyle().set("margin-left", "5px");
            scoreBox.getStyle().set("border-radius", "4px");
            line.add(scoreBox);
        }

        return line;
    }
}
