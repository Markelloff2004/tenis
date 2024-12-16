package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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

import java.util.ArrayList;
import java.util.List;
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

    private HorizontalLayout createTournamentInfoLayout(String name, String type, String rule) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER); // Centrează elementele vertical

        Div nameDiv = new Div();
        nameDiv.setText("Name: " + name);
        nameDiv.getStyle().set("font-size", "18px");

        Div typeDiv = new Div();
        typeDiv.setText("Type: " + type);
        typeDiv.getStyle().set("font-size", "18px");

        Div ruleDiv = new Div();
        ruleDiv.setText("Rule: " + rule);
        ruleDiv.getStyle().set("font-size", "18px");

        layout.add(nameDiv, typeDiv, ruleDiv);

        return layout;
    }


    private HorizontalLayout createRoundButtonsLayout(Set<Integer> rounds) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(new H3("Rounds: " + rounds.size()));
        for (Integer round : rounds) {
            Button button = new Button("Round:" + round.toString());
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            button.addClickListener(e -> onRoundButtonClick(round));
            buttonLayout.add(button);
        }
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
            HorizontalLayout matchLayout = new HorizontalLayout();
            matchLayout.setSizeFull();
            matchLayout.setSpacing(true);
            matchLayout.getStyle().set("background-color", "#f8f8f8");


            VerticalLayout playerDetails = new VerticalLayout();
            HorizontalLayout player1Layout = new HorizontalLayout();
            player1Layout.add(new Div("#" + match.getRightPlayer().getRating()), new Div(match.getRightPlayer().getPlayerName()),
                    new Div("Score: " + match.getScore()));
            HorizontalLayout player2Layout = new HorizontalLayout();
            player2Layout.add(new Div("#" + match.getLeftPlayer().getRating()), new Div(match.getLeftPlayer().getPlayerName()),
                    new Div("Score: " + match.getScore()));
            playerDetails.add(player1Layout, player2Layout);

            Div winnerDetails = new Div();
            winnerDetails.setText("Winner: #" + match.getWinner().getRating() + " " + match.getWinner().getPlayerName());

            matchLayout.add(playerDetails, winnerDetails);
            matchContainer.add(matchLayout);
        }
    }

}

