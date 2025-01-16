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
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;

import java.util.List;
import java.util.Optional;

@Route(value = "tournament/matches", layout = MainLayout.class)
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final TournamentService tournamentService;
    private final MatchService matchService;

    private Tournament tournament;

    private VerticalLayout matchContainer;


    public TournamentBracketView(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer tournamentId) {
        Optional<Tournament> searchedTournament = tournamentService.find(tournamentId);

        if (searchedTournament.isPresent())
        {
            tournament = searchedTournament.get();
            initView();
        } else
        {
            add(new H2("Tournament not found"));
        }
    }

    private void initView()
    {

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

        refreshMatchesInRound(
                // get FirstRound name
                TournamentUtils.getRoundsCount(tournament.getMaxPlayers()).get(0)
        );
    }

    private HorizontalLayout createRoundButtonsLayout() {
        HorizontalLayout roundButtons = new HorizontalLayout();
        roundButtons.setJustifyContentMode(JustifyContentMode.START);

        for (String round : TournamentUtils.getRoundsCount(tournament.getMaxPlayers())) {
            roundButtons.add(
                    ViewUtils.createButton(round, "colored-button",
                            () -> refreshMatchesInRound(round)
                    )
            );
        }

        Button prevoiusPageButton = ViewUtils.createButton(
                "Previous Page",
                "colored-button",
                () -> getUI().
                        ifPresent(ui ->
                                ui.navigate("tournament/general-details/" + tournament.getId())
                        )
        );

        return ViewUtils.createHorizontalLayout(
                JustifyContentMode.BETWEEN,
                roundButtons,
                prevoiusPageButton
        );
    }


    private void refreshMatchesInRound(String round) {
        matchContainer.removeAll();

        displayMatches(matchService.getMatchesByTournamentAndRound(tournament, round));
    }

    private void displayMatches(List<Match> matches)
    {

        for (Match match : matches) {
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
                            ? new Div("#" + match.getWinner().getRating() + " " + match.getWinner().getPlayerName() + match.getPosition())
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
        VerticalLayout playerDetails = new VerticalLayout();
        playerDetails.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        playerDetails.setAlignItems(FlexComponent.Alignment.START);
        playerDetails.setPadding(false);
        playerDetails.setSpacing(false);

            playerDetails.add(
                    match.getTopPlayer() != null
                            ? createPlayerLayout(match.getTopPlayer().getRating(), match.getTopPlayer().getPlayerName())
                            : createPlayerLayout(0, "Null"),
                    match.getBottomPlayer() != null
                            ? createPlayerLayout(match.getBottomPlayer().getRating(), match.getBottomPlayer().getPlayerName())
                            : createPlayerLayout(0, "Null")
            );


        return playerDetails;
    }

    private HorizontalLayout createScoreDetails(Match match)
    {
        HorizontalLayout scoreDetails = new HorizontalLayout();
        scoreDetails.setSpacing(false);
        scoreDetails.setAlignItems(FlexComponent.Alignment.CENTER);

        String defaultScore = match.getScore();

        String[] setScores = defaultScore != null
                ? defaultScore.split(";")
                : "-:-;-:-;-:-".split(";");

        for (int i = 0; i < TournamentUtils.getSetsCount(tournament.getTournamentType()); i++)
        {
            String[] setScore = (i < setScores.length)
                    ? setScores[i].split(":")
                    // handle if setScore is empty
                    : new String[]{"-", "-"};

            scoreDetails.add(createScoreColumn(setScore));
        }

        Button editScoreButton = ViewUtils.createButton("", "colored-button", () -> editMatchScore(match));
        editScoreButton.setIcon(VaadinIcon.PENCIL.create());
        editScoreButton.setMaxWidth("20px");

        scoreDetails.add( editScoreButton );

        return scoreDetails;
    }

    private VerticalLayout createScoreColumn(String[] scores) {
        VerticalLayout column = new VerticalLayout();
        column.setSpacing(true);
        column.setAlignItems(FlexComponent.Alignment.CENTER);

        column.add(createStyledDiv(scores[0]), createStyledDiv(scores[1]));
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

        Span player1Label = new Span(match.getBottomPlayer().getPlayerName());
        Span player2Label = new Span(match.getTopPlayer().getPlayerName());

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setSpacing(false);
        hLayout.add(player1Label, new Span(" : "), player2Label);

        layout.add(hLayout);

        TextField[] scoreFields = new TextField[3];
        for (int i = 0; i < TournamentUtils.getSetsCount(tournament.getTournamentType()); i++) {
            scoreFields[i] = new TextField("Set " + (i + 1));
            scoreFields[i].setPlaceholder("11:2 or -:-");
            scoreFields[i].setPattern("^(\\d{1,2}:\\d{1,2}|-:-)$");
            scoreFields[i].setErrorMessage("Invalid score format!");
            scoreFields[i].setWidth("100px");
            layout.add(scoreFields[i]);
        }

        // insert default score
        if (match.getScore() != null && !match.getScore().isEmpty()) {
            String[] setScores = match.getScore().split(";");
            for (int i = 0; i < setScores.length && i < scoreFields.length; i++) {
                scoreFields[i].setValue(setScores[i]);
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
                match.setScore(finalScore);
                matchService.saveOrUpdateMatch(match);
                NotificationManager.showInfoNotification("Score saved: " + finalScore);
                TournamentUtils.determinateWinner(matchService, match, tournament.getMaxPlayers());
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

