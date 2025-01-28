package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

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
        Optional<Tournament> searchedTournament = tournamentService.find(tournamentId);

        if (searchedTournament.isPresent())
        {
            tournament = searchedTournament.get();
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
        add(matchContainer);

        refreshMatchesInRound(1);
    }

    private HorizontalLayout createRoundButtonsLayout() {
        logger.info("Creating layout for Round buttons.");
        HorizontalLayout roundButtons = new HorizontalLayout();
        roundButtons.setJustifyContentMode(JustifyContentMode.START);
        Integer roundsCount = (int) Math.sqrt(tournament.getMaxPlayers());

        for (int i = 1; i <= roundsCount; i++) {
            int round = i;
            logger.debug("Adding button for round {}", i);
            roundButtons.add(
                    ViewUtils.createButton("Stage " + round, "colored-button",
                            () -> refreshMatchesInRound(round)
                    )
            );
        }

        Button prevoiusPageButton = ViewUtils.createButton(
                "Previous Page",
                "colored-button",
                () -> getUI().
                        ifPresent(ui ->
                                {
                                    logger.info("Navigating to general details page for Tournament with ID: {}", tournament.getId());
                                    ui.navigate("tournament/general-details/" + tournament.getId());
                                }
                        )
        );

        return ViewUtils.createHorizontalLayout(
                JustifyContentMode.BETWEEN,
                roundButtons,
                prevoiusPageButton
        );
    }


    private void refreshMatchesInRound(int round) {
        logger.info("Refreshing matches for round {}", round);
        matchContainer.removeAll();

        displayMatches(tournament.getMatches()
                .stream()
                .filter(m -> m.getRound() == round)
                .toList());
    }

    private void displayMatches(List<Match> matches)
    {
        logger.info("Displaying {} matches", matches.size() );

        for (Match match : matches) {
            logger.debug("Processed match {}", match);

            // Main match layout
            HorizontalLayout matchLayout = new HorizontalLayout();
            matchLayout.setWidthFull();
            matchLayout.setSpacing(true);
            matchLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center everything vertically
            matchLayout.getStyle().set("background-color", "#f8f8f8");
            matchLayout.setPadding(true);

            // Player Details Layout
            VerticalLayout playerDetails = createPlayersDetails(match);

            HorizontalLayout scoreDetails = createScoreDetails(match);

            HorizontalLayout winnerDetails = ViewUtils.createHorizontalLayout(
                    JustifyContentMode.CENTER,
                    (match.getWinner() != null)
                            ? new Div("#" + match.getWinner().getRating() + " " + match.getWinner().getName() + " " + match.getWinner().getSurname() + match.getPosition())
                            : new Div("Unknown ->" + match.getPosition())
                    );

            matchLayout.add(
                    playerDetails,
                    scoreDetails,
                    winnerDetails
            );
//            matchLayout.add(playerDetails, scoreDetails, winnerLayout);
            matchContainer.add(matchLayout);
        } // ends foreach(matches)

    }

    private VerticalLayout createPlayersDetails(Match match)
    {
        logger.debug("Creating players details for match {}", match);
        VerticalLayout playerDetails = new VerticalLayout();
        playerDetails.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        playerDetails.setAlignItems(FlexComponent.Alignment.START);
        playerDetails.setPadding(false);
        playerDetails.setSpacing(false);

            playerDetails.add(
                    match.getTopPlayer() != null
                            ? createPlayerLayout(match.getTopPlayer().getRating(), match.getTopPlayer().getName() + " " + match.getTopPlayer().getSurname())
                            : createPlayerLayout(0, "Null"),
                    match.getBottomPlayer() != null
                            ? createPlayerLayout(match.getBottomPlayer().getRating(), match.getBottomPlayer().getName() + " "  + match.getBottomPlayer().getSurname())
                            : createPlayerLayout(0, "Null")
            );


        return playerDetails;
    }

    private HorizontalLayout createScoreDetails(Match match)
    {
        logger.debug("Creating score details for match {}", match);
        HorizontalLayout scoreDetails = new HorizontalLayout();
        scoreDetails.setSpacing(false);
        scoreDetails.setAlignItems(FlexComponent.Alignment.CENTER);

        List<Score> defaultScore = match.getScore();

        for (int i = 0; i < 3; i++)
        {
            Score setScore = (i < defaultScore.size())
                    ? defaultScore.get(i)
                    // handle if setScore is empty
//                    : new Score(0, 0);
                    : null;

            scoreDetails.add(createScoreColumn(setScore));
        }

        Button editScoreButton = ViewUtils.createButton("", "colored-button", () -> editMatchScore(match));
        editScoreButton.setIcon(VaadinIcon.PENCIL.create());
        editScoreButton.setMaxWidth("20px");

        scoreDetails.add( editScoreButton );

        return scoreDetails;
    }

    private VerticalLayout createScoreColumn(Score scores) {
        VerticalLayout column = new VerticalLayout();
        column.setSpacing(true);
        column.setAlignItems(FlexComponent.Alignment.CENTER);

        column.add(
                createStyledDiv(
                        (scores != null) ? String.valueOf(scores.getTopPlayerScore()) : "-"
                        ),
                createStyledDiv(
                        (scores != null) ? String.valueOf(scores.getBottomPlayerScore()) : "-"
                        )
        );
        return column;
    }

    private Div createStyledDiv(String content) {
        Div div = new Div(content);
        div.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("background", "#e0e0e0")
                .set("text-align", "center")
                .set("line-height", "40px")
                .set("font-weight", "bold");
        return div;
    }



    private void editMatchScore(Match match) {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        Span player1Label = new Span(match.getBottomPlayer().getName() + " " + match.getBottomPlayer().getSurname());
        Span player2Label = new Span(match.getTopPlayer().getName() + " " + match.getTopPlayer().getSurname());

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSpacing(false);
        hLayout.add(player1Label, new Span(" : "), player2Label);

        layout.add(hLayout);

        TextField[] scoreFields = new TextField[3];
        for (int i = 0; i < TournamentUtils.getSetsCount(tournament.getTournamentType().toString()); i++) {
            scoreFields[i] = new TextField("Set " + (i + 1));
            scoreFields[i].setPlaceholder("11:2 or -:-");
            scoreFields[i].setPattern("^(\\d{1,2}:\\d{1,2}|-:-)$");
            scoreFields[i].setErrorMessage("Invalid score format!");
            scoreFields[i].setWidth("100px");
            layout.add(scoreFields[i]);
        }

        // insert default score
        if (match.getScore() != null && !match.getScore().isEmpty()) {
            List<Score> setScores = match.getScore();
            for (int i = 0; i < setScores.size() && i < scoreFields.length; i++) {
                scoreFields[i].setValue(setScores.get(i).toString());
            }
        }

        Button saveButton = new Button("Save", event -> {
            StringBuilder scoreBuilder = new StringBuilder();

            boolean isValid = true;
            for (TextField scoreField : scoreFields) {
                String value = scoreField.getValue().trim();
                if (!value.matches("^(\\d{1,2}:\\d{1,2}|-:-)$") && !value.isEmpty()) {
                    isValid = false;
                    scoreField.setInvalid(true);
                } else {
                    scoreField.setInvalid(false);
                    if (!scoreBuilder.isEmpty()) {
                        scoreBuilder.append(";");
                    }
                    scoreBuilder.append(value.isEmpty() ? "-:-" : value);
                }
            }

            if (isValid) {
                String finalScore = scoreBuilder.toString();
//                match.setScore(finalScore);
                matchService.saveOrUpdateMatch(match);
                NotificationManager.showInfoNotification("Score saved: " + finalScore);
//                TournamentUtils.determinateWinner(matchService, match, tournament.getMaxPlayers());
            } else {
                NotificationManager.showInfoNotification("Please fix invalid scores!");
            }

            UI.getCurrent().refreshCurrentRoute(true);
        });

        saveButton.getStyle()
                .set("background-color", "#4CAF50")
                .set("color", "white");

        layout.add(saveButton);

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Match Score");
        dialog.add(layout);
        dialog.open();
    }


    private HorizontalLayout createPlayerLayout(int rating, String playerName) {
        HorizontalLayout playerLayout = new HorizontalLayout();
        playerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Div playerRating = new Div("#" + rating);
        playerRating.getStyle()
                .set("width", "100px")
                .set("text-align", "center")
                .set("font-size", "18px")
                .set("font-weight", "bold");

        Div playerNameDiv = new Div(playerName);
        playerNameDiv.getStyle()
                .set("font-size", "18px")
                .set("padding-left", "10px");

        playerLayout.add(playerRating, playerNameDiv);
        return playerLayout;
    }

}

