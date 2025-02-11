package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.tournaments.components.MatchComponent;
import org.cedacri.pingpong.views.tournaments.components.RobinRoundDetailsDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Route(value = "tournament/matches", layout = MainLayout.class)
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final TournamentService tournamentService;
    private final MatchService matchService;

    private Tournament tournament;

    private VerticalLayout matchContainer;
    private ComboBox<String> playerOptionsComboBox;


    public TournamentBracketView(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        log.info("TournamentBracketView initialized.");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer tournamentId) {
        log.info("Received request to load tournament with ID {}", tournamentId);
        tournament = tournamentService.find(tournamentId);

        if (tournament == null) {
            log.warn("Tournament with ID {} not found", tournamentId);
            add(new H2("Tournament not found"));
            return;
        }

        log.info("Tournament found : {}", tournament);
        initView();

    }

    private void initView() {
        log.info("Initializing view for tournament {}", tournament.getTournamentName());

        add(
                ViewUtils.createHorizontalLayout(
                        JustifyContentMode.BETWEEN,
                        new H1(this.tournament.getTournamentName()
                        )
                )
        );

        add(createRoundButtonsLayout());

        matchContainer = ViewUtils.createVerticalLayout(JustifyContentMode.CENTER);

        add(matchContainer);

        initialLoadMatchesBasedOnTournamentType();
    }

    private void initialLoadMatchesBasedOnTournamentType() {
        if (tournament.getTournamentType().equals(TournamentTypeEnum.OLYMPIC)) {
            refreshMatchesInRound(1);
        } else if (tournament.getTournamentType().equals(TournamentTypeEnum.ROBIN_ROUND))
        {
            refreshMatchesInRound("All");
        }
    }

    private HorizontalLayout createRoundButtonsLayout() {

        log.info("Creating layout for Round buttons.");

        return tournament.getTournamentType() == TournamentTypeEnum.OLYMPIC
                ? createOlympicRoundButtons()
                : tournament.getTournamentType() == TournamentTypeEnum.ROBIN_ROUND
                    ? createRobinRoundPlayerSelection()
                    : createUnknownTournamentType();
    }

    private HorizontalLayout createUnknownTournamentType() {
        return ViewUtils.createHorizontalLayout(JustifyContentMode.START, new Span("Tournament type not supported"));
    }

    private HorizontalLayout createRobinRoundPlayerSelection() {
        Set<Player> playerList = tournament.getPlayers();
        List<String> playerOptions = new ArrayList<>(List.of("All"));
        playerList.forEach(p -> playerOptions.add(p.getName() + " " + p.getSurname()));

        playerOptionsComboBox = ViewUtils.createComboBox("Player Options", playerOptions);
        playerOptionsComboBox.setValue(playerOptions.get(0));
        playerOptionsComboBox.addValueChangeListener(event -> refreshMatchesInRound(event.getValue()));

        Button openRating = ViewUtils.createButton("View Rating","button", () -> {
            RobinRoundDetailsDialog robinRoundDetailsDialog = new RobinRoundDetailsDialog(tournamentService.find(tournament.getId()));
            robinRoundDetailsDialog.open();
        });

        return ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN, playerOptionsComboBox, openRating);
    }

    private HorizontalLayout createOlympicRoundButtons() {
        HorizontalLayout roundButtons = new HorizontalLayout();
        roundButtons.setJustifyContentMode(JustifyContentMode.START);
        List<Button> buttons = new ArrayList<>();

        int roundsCount = TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers());

        for (int i = 1; i <= roundsCount; i++) {
            int round = i;
            Button roundButton = new Button("Stage " + round, event -> {
                refreshMatchesInRound(round);
                ViewUtils.highlightSelectedComponentFromComponentsList(buttons, round - 1, "selected");
            });

            roundButton.addClassName("button");
            buttons.add(roundButton);
            roundButtons.add(roundButton);
        }

        ViewUtils.highlightSelectedComponentFromComponentsList(buttons, 0, "selected");

        return ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN, roundButtons);
    }

    private void refreshMatchesInRound(Object round) {

        log.info("Refreshing matches for round {}", round);

        if(round instanceof Integer)
        {
            displayMatches(matchService.getMatchesByTournamentAndRound(tournament, (Integer) round));
        } else if( round instanceof String) {
            if("All".equals(round) || ((String) round).isEmpty() || ((String) round).isBlank()) {
//                displayMatches(tournament.getMatches().stream().toList());
                displayMatches(matchService.getMatchesByTournament(tournament));
            }
            else {
                String[] playerNameSurname = ((String) round).split(" ");

                displayMatches(matchService.getMatchesByPlayerNameSurname(tournament, playerNameSurname[0], playerNameSurname[1]));
            }
        }

    }

    private void displayMatches(List<Match> matches) {

        log.info("Displaying {} matches", matches.size());
        matchContainer.removeAll();

        for (Match match : matches) {
            log.debug("Processed match {}", match);

            MatchComponent matchLayout = new MatchComponent(match, matchService, tournament,
                    () -> {
                        if (tournament.getTournamentType() == TournamentTypeEnum.OLYMPIC)
                            refreshMatchesInRound( match.getRound());

                        if (tournament.getTournamentType() == TournamentTypeEnum.ROBIN_ROUND)
                            refreshMatchesInRound(playerOptionsComboBox.getValue());
                    });

            matchContainer.add(matchLayout);
        }

    }

}

