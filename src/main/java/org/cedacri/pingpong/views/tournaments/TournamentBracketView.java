package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.tournaments.components.MatchComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route(value = "tournament/matches", layout = MainLayout.class)
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TournamentBracketView.class);

    private final TournamentService tournamentService;
    private final MatchService matchService;

    private Tournament tournament;

    private VerticalLayout matchContainer;


    public TournamentBracketView(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        logger.info("TournamentBracketView initialized.");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer tournamentId) {
        logger.info("Received request to load tournament with ID {}", tournamentId);
        Tournament searchedTournament = tournamentService.find(tournamentId);

        if (searchedTournament != null)
        {
            tournament = searchedTournament;
            logger.info("Tournament found : {}", tournament);
            initView();
        } else
        {
            logger.warn("Tournament with ID {} not found", tournamentId);
            add(new H2("Tournament not found"));
        }
    }

    private void initView()
    {
        logger.info("Initializing view for tournament {}", tournament.getTournamentName());

        /*
        Title view
         */
        add(
                ViewUtils.createHorizontalLayout(
                        JustifyContentMode.BETWEEN,
                        new H1(this.tournament.getTournamentName()
                        )
                )
        );

        /*
        Buttons
         */
        add(createRoundButtonsLayout());

        // Container for matches
        matchContainer = new VerticalLayout();
        matchContainer.setSpacing(true);
        matchContainer.setHorizontalComponentAlignment(Alignment.STRETCH);
        add(matchContainer);

        refreshMatchesInRound(1);
    }

    private HorizontalLayout createRoundButtonsLayout() {
        logger.info("Creating layout for Round buttons.");
        HorizontalLayout roundButtons = new HorizontalLayout();
        roundButtons.setJustifyContentMode(JustifyContentMode.START);
        int roundsCount = TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers());

        List<Button> buttons = new ArrayList<>();
        for (int i = 1; i <= roundsCount; i++) {
            int round = i;
            logger.debug("Adding button for round {}", i);
            Button roundButton = new Button("Stage " + round, event -> {
                refreshMatchesInRound(round);
                ViewUtils.highlightSelectedComponentFromComponentsList(buttons, round - 1, "selected");
            });

            roundButton.addClassName("button");

            buttons.add(roundButton);
            roundButtons.add(roundButton);
        }

        // Initially highlight the first button
        ViewUtils.highlightSelectedComponentFromComponentsList(buttons, 0, "selected");

        return ViewUtils.createHorizontalLayout(
                JustifyContentMode.BETWEEN,
                roundButtons
        );
    }

    private void refreshMatchesInRound(int round) {
        logger.info("Refreshing matches for round {}", round);
        matchContainer.removeAll();

        displayMatches(tournament.getMatches()
                .stream()
                .filter(m -> m.getRound() == round)
                .sorted(Comparator.comparingInt(Match::getPosition))
                .toList());
    }

    private void displayMatches(List<Match> matches)
    {
        logger.info("Displaying {} matches", matches.size() );

//        matches.sort(Comparator.comparingInt(Match::getId));

        for (Match match : matches ) {
            logger.debug("Processed match {}", match);

            MatchComponent matchLayout = new MatchComponent(match, matchService, tournament, () -> refreshMatchesInRound(match.getRound()));

            matchContainer.add(matchLayout);
        } // ends foreach(matches)

    }

}

