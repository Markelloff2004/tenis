package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;
import org.cedacri.pingpong.service.primary.MatchService;
import org.cedacri.pingpong.service.primary.PlayerService;
import org.cedacri.pingpong.service.primary.TournamentService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.tournaments.components.MatchComponent;
import org.cedacri.pingpong.views.tournaments.components.RobinRoundDetailsDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Route(value = "tournament/matches", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Integer>
{

    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final PlayerService playerService;

    private Tournament tournament;

    private VerticalLayout matchContainer;
    private ComboBox<String> playerOptionsComboBox;


    public TournamentBracketView(TournamentService tournamentService, MatchService matchService, PlayerService playerService)
    {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        log.info("TournamentBracketView initialized.");
        this.playerService = playerService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer tournamentId)
    {
        log.info("Received request to load tournament with ID {}", tournamentId);
        tournament = tournamentService.findTournamentById(tournamentId);

        if (tournament == null)
        {
            log.warn("Tournament with ID {} not found", tournamentId);
            add(new H2("Tournament not found"));
            return;
        }

        log.info("Tournament found : {}", tournament);
        initView();

    }

    private void initView()
    {
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

    private void initialLoadMatchesBasedOnTournamentType()
    {
        if (tournament.getTournamentType().equals(TournamentTypeEnum.OLYMPIC))
        {
            refreshMatchesInRound(1);
        }
        else if (tournament.getTournamentType().equals(TournamentTypeEnum.ROBIN_ROUND))
        {
            refreshMatchesInRound("All");
        }
    }

    private HorizontalLayout createRoundButtonsLayout()
    {
        log.info("Creating layout for Round buttons.");

        HorizontalLayout buttonsLayout;

        TournamentTypeEnum type = tournament.getTournamentType();

        if (type == TournamentTypeEnum.OLYMPIC)
        {
            buttonsLayout = createOlympicRoundButtons();
        }
        else if (type == TournamentTypeEnum.ROBIN_ROUND)
        {
            buttonsLayout = createRobinRoundPlayerSelection();
        }
        else
        {
            buttonsLayout = createUnknownTournamentType();
        }

        if (tournament.getTournamentStatus() == TournamentStatusEnum.ONGOING)
        {
            buttonsLayout.add(
                    ViewUtils.createButton("End Tournament", ViewUtils.BUTTON, () ->
                    {
                        refreshTournament();

                        if (TournamentUtils.isTournamentReadyToFinish(tournament))
                        {
                            showEndTournamentDialog();
                        }
                        else
                        {
                            NotificationManager.showErrorNotification(
                                    Constants.TOURNAMENT_WINNER_CANT_BE_DETERMINATED + " Please assure that " +
                                            TournamentUtils.getErrorMessageForTournamentType(tournament)
                            );
                        }
                    })
            );
        }

        return buttonsLayout;
    }

    private void showEndTournamentDialog()
    {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm end tournament: " + tournament.getTournamentName());
        dialog.add(new Text("Are you sure you want to end the tournament?"));


        Button confirmButton = ViewUtils.createButton("Confirm", ViewUtils.COLORED_BUTTON, () ->
        {
            TournamentUtils.checkAndUpdateTournamentWinner(tournament, tournamentService, playerService);
            dialog.close();
        });

        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, dialog::close);

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, confirmButton, cancelButton);
        dialog.add(buttonLayout);

        dialog.open();
    }

    private HorizontalLayout createUnknownTournamentType()
    {
        return ViewUtils.createHorizontalLayout(JustifyContentMode.START, new Span("Tournament type not supported"));
    }

    private HorizontalLayout createRobinRoundPlayerSelection()
    {
        Set<Player> playerList = tournament.getPlayers();
        List<String> playerOptions = new ArrayList<>(List.of("All"));
        playerList.forEach(p -> playerOptions.add(p.getName() + " " + p.getSurname()));

        playerOptionsComboBox = ViewUtils.createComboBox("Player Options", playerOptions);
        playerOptionsComboBox.setValue(playerOptions.get(0));
        playerOptionsComboBox.addValueChangeListener(event -> refreshMatchesInRound(event.getValue()));

        Button openRating = ViewUtils.createButton("View Rating", ViewUtils.BUTTON, () ->
        {
            RobinRoundDetailsDialog robinRoundDetailsDialog = new RobinRoundDetailsDialog(tournament.getId(), tournamentService);
            robinRoundDetailsDialog.open();
        });

        return ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN, playerOptionsComboBox, openRating);
    }

    private HorizontalLayout createOlympicRoundButtons()
    {
        HorizontalLayout roundButtons = new HorizontalLayout();
        roundButtons.setJustifyContentMode(JustifyContentMode.START);
        List<Button> buttons = new ArrayList<>();

        int roundsCount = TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers());
        Map<Integer, String> specialRounds = Map.of(
                roundsCount - 1, "Semifinals",
                roundsCount, "Finals"
        );

        for (int i = 1; i <= roundsCount; i++)
        {
            int round = i;
            String label = specialRounds.getOrDefault(i, "Stage " + round);

            Button roundButton = new Button(label, event ->
            {
                refreshMatchesInRound(round);
                ViewUtils.highlightSelectedComponentFromComponentsList(buttons, round - 1, "selected");
            });

            roundButton.addClassName(ViewUtils.BUTTON);
            buttons.add(roundButton);
            roundButtons.add(roundButton);
        }

        ViewUtils.highlightSelectedComponentFromComponentsList(buttons, 0, "selected");

        return ViewUtils.createHorizontalLayout(JustifyContentMode.BETWEEN, roundButtons);
    }

    private void refreshMatchesInRound(Object round)
    {

        log.info("Refreshing matches for round {}", round);

        if (round instanceof Integer roundInt)
        {
            displayMatches(matchService.findMatchesByTournamentAndRound(tournament, roundInt));
        }
        else if (round instanceof String roundStr)
        {
            if ("All".equals(roundStr) || roundStr.isEmpty() || roundStr.isBlank())
            {
                displayMatches(matchService.findMatchesByTournament(tournament));
            }
            else
            {
                String[] playerNameSurname = roundStr.split(" ");
                if (playerNameSurname.length == 2)
                {
                    displayMatches(matchService.findMatchesByPlayerNameAndSurname(tournament, playerNameSurname[0], playerNameSurname[1]));
                }
                else
                {
                    log.warn("Invalid player name format: {}", roundStr);
                }
            }
        }


    }

    private void displayMatches(List<Match> matches)
    {

        log.info("Displaying {} matches", matches.size());
        matchContainer.removeAll();

        matches = matches.stream().sorted(Comparator.comparing(Match::getPosition).reversed()).toList();

        for (Match match : matches)
        {
            log.debug("Processed match {}", match);

            MatchComponent matchLayout = new MatchComponent(match, matchService, tournament,
                    () ->
                    {
                        if (tournament.getTournamentType() == TournamentTypeEnum.OLYMPIC)
                            refreshMatchesInRound(match.getRound());

                        if (tournament.getTournamentType() == TournamentTypeEnum.ROBIN_ROUND)
                            refreshMatchesInRound(playerOptionsComboBox.getValue());
                    });

            matchContainer.add(matchLayout);
        }
    }

    private void refreshTournament()
    {
        log.info("Refreshing tournament data...");
        this.tournament = tournamentService.findTournamentById(tournament.getId());
    }


}

