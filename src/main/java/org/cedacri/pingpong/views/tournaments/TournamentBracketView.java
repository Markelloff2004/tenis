package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.service.TournamentService;
import org.cedacri.pingpong.views.MainLayout;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "tournaments", layout = MainLayout.class)
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Integer> {

    private Tournament tournament;

    private Set<Match> matches;

    private VerticalLayout matchContainer;
    private final TournamentService tournamentService;
    private final MatchService matchService;


    public TournamentBracketView(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;

       matchContainer = new VerticalLayout();
       matchContainer.setSizeFull();
       matchContainer.setSpacing(true);
       matchContainer.setPadding(false);
       matchContainer.setAlignItems(Alignment.CENTER);
       matchContainer.setJustifyContentMode(JustifyContentMode.CENTER);
       add(matchContainer);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer tournamentId) {
        Optional<Tournament> searchedTournament = tournamentService.find(tournamentId);

        if (searchedTournament.isPresent())
        {
            tournament = searchedTournament.get();
            matches = matchService.findAll().stream().filter(match -> match.getTournament().getId().equals(tournament.getId())).collect(Collectors.toSet());
            initView();
        } else
        {
            add(new H2("Tournament not found"));
        }
    }

    private void initView()
    {
        add(createTournamentInfoLayout(this.tournament.getTournamentName(), this.tournament.getTournamentType(), this.tournament.getTournamentStatus()));

        Set<Integer> rounds = matches.stream().map(Match::getRound).collect(Collectors.toSet());

        add(createRoundButtonsLayout(rounds));

        // Container pentru meciuri
        matchContainer = new VerticalLayout();
        matchContainer.setSpacing(true);
        add(matchContainer);

        Set<Match> firstRoundMatches = this.matches.stream().filter(r -> r.equals(1)).collect(Collectors.toSet());
        displayMatches(firstRoundMatches);
    }

    private HorizontalLayout createTournamentInfoLayout(String name, String type, String status) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER);

        Div nameDiv = new Div();
        nameDiv.setText(name);
        nameDiv.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold");

//        Div typeDiv = new Div();
//        typeDiv.setText("Type: " + type);
//        typeDiv.getStyle().set("font-size", "18px");
//
//        Div ruleDiv = new Div();
//        ruleDiv.setText("Status: " + status);
//        ruleDiv.getStyle().set("font-size", "18px");

//        layout.add(nameDiv, typeDiv, ruleDiv);
        layout.add(nameDiv);

        return layout;
    }


    private HorizontalLayout createRoundButtonsLayout(Set<Integer> rounds) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout roundButtons = new HorizontalLayout();
        for (Integer round : rounds) {
            Button button = new Button("Round: " + round.toString());
            button.addClassName("colored-button");
            button.addClickListener(e -> onRoundButtonClick(round));
            roundButtons.add(button);
        }

        Button prevoiusPageButton = new Button("Previous Page");
        prevoiusPageButton.addClassName("colored-button");
        prevoiusPageButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("tournament/general-details")));

        buttonLayout.add(roundButtons, prevoiusPageButton);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        return buttonLayout;
    }


    private void onRoundButtonClick(Integer round) {
        matchContainer.removeAll();

        Set<Match> matchesInRound = this.matches.stream()
                .filter(match -> match.getRound().equals(round))
                .collect(Collectors.toSet());


        displayMatches(matchesInRound);
    }

    private void displayMatches(Set<Match> matches) {
        for (Match match : matches) {
            // Main match layout
            HorizontalLayout matchLayout = new HorizontalLayout();
            matchLayout.setWidthFull();
            matchLayout.setSpacing(true);
            matchLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center everything vertically
            matchLayout.getStyle().set("background-color", "#f8f8f8");
            matchLayout.setPadding(true);

            // Player Details Layout
            VerticalLayout playerDetails = new VerticalLayout();
            playerDetails.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            playerDetails.setAlignItems(FlexComponent.Alignment.START); // Align player data to start
            playerDetails.setPadding(false);
            playerDetails.setSpacing(false);

            // Player 1 Layout
            HorizontalLayout player1Layout = createPlayerLayout(
                    match.getRightPlayer().getRating(),
                    match.getRightPlayer().getPlayerName()
            );

            // Player 2 Layout
            HorizontalLayout player2Layout = createPlayerLayout(
                    match.getLeftPlayer().getRating(),
                    match.getLeftPlayer().getPlayerName()
            );

            playerDetails.add(player1Layout, player2Layout);

            // Score Details
            HorizontalLayout scoreDetails = new HorizontalLayout();
            scoreDetails.setSpacing(true);
            scoreDetails.setAlignItems(FlexComponent.Alignment.CENTER); // Center scores vertically

            String scoreString = match.getScore();
            if (scoreString != null && !scoreString.isEmpty()) {
                String[] setScores = scoreString.split(";"); // Example: "11:6;10:12;11:9"

                for (String set : setScores) {
                    String[] scores = set.split(":");

                    if (scores.length == 2) {
                        VerticalLayout column = new VerticalLayout();
                        column.setSpacing(true);
                        column.setAlignItems(FlexComponent.Alignment.CENTER); // Center score boxes

                        Div textField1 = new Div(scores[0]);
                        textField1.getStyle()
                                .set("width", "40px")
                                .set("height", "40px")
                                .set("background", "#e0e0e0")
                                .set("text-align", "center")
                                .set("line-height", "40px")
                                .set("font-weight", "bold");

                        Div textField2 = new Div(scores[1]);
                        textField2.getStyle()
                                .set("width", "40px")
                                .set("height", "40px")
                                .set("background", "#e0e0e0")
                                .set("text-align", "center")
                                .set("line-height", "40px");

                        column.add(textField1, textField2);
                        scoreDetails.add(column);
                    }
                }
            }

            HorizontalLayout winnerLayout = new HorizontalLayout();
            winnerLayout.setSpacing(false);
            winnerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            winnerLayout.getStyle().set("margin-left", "30px");

            Div winnerText = new Div("Winner: ");
            winnerText.getStyle()
                    .set("font-size", "18px")
                    .set("margin-right", "10px");
//                    .set("color", "#888");

            Div winnerDetails = new Div("#" + match.getWinner().getRating()
                    + " " + match.getWinner().getPlayerName());
            winnerDetails.getStyle()
                    .set("font-size", "18px")
                    .set("font-weight", "bold")
                    .set("width", "250px");;

            winnerLayout.add(winnerText, winnerDetails);

            matchLayout.add(playerDetails, scoreDetails, winnerLayout);
            matchContainer.add(matchLayout);
        }
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

