package org.cedacri.pingpong.views.tournament;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.HasUrlParameter;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.MatchRepository;
import org.cedacri.pingpong.repository.TournamentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Route("tournaments")
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;

    public TournamentBracketView(TournamentRepository tournamentRepository, MatchRepository matchRepository) {
        this.tournamentRepository = tournamentRepository;
        setSizeFull();
        this.matchRepository = matchRepository;
    }

    private HorizontalLayout createTournamentDetails(Tournament tournament) {
        HorizontalLayout detailsLayout = new HorizontalLayout();
        detailsLayout.setSpacing(true);

        // Tournament Name (Editable)
        TextField tournamentName = new TextField("Tournament Name");
        tournamentName.setValue(tournament.getTournamentName());
        tournamentName.setRequired(true);  // Make it required
        tournamentName.setWidth("300px");  // Set width for consistency

        // Max Players (Editable with ComboBox)
        ComboBox<Integer> maxPlayers = new ComboBox<>("Max Players");
        maxPlayers.setItems(1, 2, 4, 8, 16, 32, 64); // Example options for max players
        maxPlayers.setValue(tournament.getMaxPlayers());
        maxPlayers.setRequired(true);

        // Tournament Status (Editable with ComboBox)
        ComboBox<String> tournamentStatus = new ComboBox<>("Status");
        tournamentStatus.setItems("Scheduled", "Ongoing", "Completed");
        tournamentStatus.setValue(tournament.getTournamentStatus());
        tournamentStatus.setRequired(true);

        // Tournament Type (Editable with ComboBox)
        ComboBox<String> tournamentType = new ComboBox<>("Type");
        tournamentType.setItems("Knockout", "Round Robin", "League");
        tournamentType.setValue(tournament.getTournamentType());
        tournamentType.setRequired(true);

        // Add all fields to the horizontal layout
        detailsLayout.add(tournamentName, maxPlayers, tournamentStatus, tournamentType);

        // Save button to update tournament details
        // Initialize saveButton before using it
        Button saveButton = new Button("Save Changed Details", event -> {
            tournament.setTournamentName(tournamentName.getValue());
            tournament.setMaxPlayers(Integer.valueOf(maxPlayers.getValue()));
            tournament.setTournamentStatus(tournamentStatus.getValue());
            tournament.setTournamentType(tournamentType.getValue());

            tournamentRepository.saveTournament(tournament);

            Notification.show("Tournament saved successfully");
        });

// Add the button to the layout or wherever appropriate
        detailsLayout.add(saveButton);


        // Add the save button to the layout
        detailsLayout.add(saveButton);

        return detailsLayout;
    }

    private VerticalLayout createBracketLayout(Tournament tournament) {
        VerticalLayout bracketLayout = new VerticalLayout();

//         Fetch matches for this tournament
        Set<Match> matches = matchRepository.findAll().stream().filter(match -> match.getTournament().getId().equals(tournament.getId())).collect(Collectors.toSet());

//         Organize matches into rounds (You may need to adjust this logic based on your match data structure)
        addRound(bracketLayout, "First Round", matches.stream().filter(match -> match.getRound().equals(1)).toList());
        addRound(bracketLayout, "Quarter Final", matches.stream().filter(match -> match.getRound().equals(2)).toList());
        addRound(bracketLayout, "Semi Final", matches.stream().filter(match -> match.getRound().equals(3)).toList());
        addRound(bracketLayout, "Final", matches.stream().filter(match -> match.getRound().equals(4)).toList());

        return bracketLayout;
    }

    private void addRound(VerticalLayout bracketLayout, String roundName, List<Match> roundMatches) {
        // Layout pentru întreaga rundă
        VerticalLayout roundLayout = new VerticalLayout();
        roundLayout.setSpacing(true);
        roundLayout.setPadding(true);
        roundLayout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "15px").set("margin", "10px");

        // Adăugare etichetă pentru numele rundei
        Div roundLabel = new Div();
        roundLabel.setText(roundName);
        roundLabel.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "18px")
                .set("margin-bottom", "15px");
        roundLayout.add(roundLabel);

        // Adăugare informații pentru fiecare meci
        for (Match match : roundMatches) {
            // Layout pentru un singur meci
            HorizontalLayout matchLayout = new HorizontalLayout();
            matchLayout.setSpacing(true);
            matchLayout.setPadding(true);
            matchLayout.getStyle()
                    .set("border", "1px solid #ddd")
                    .set("border-radius", "8px")
                    .set("padding", "10px")
                    .set("margin-bottom", "10px");

            // Adăugare detalii despre jucători și scoruri
            Div matchInfo = new Div();
            matchInfo.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "5px");

            // Jucător stânga
            Div leftPlayer = new Div();
            leftPlayer.setText("#" + match.getLeftPlayer().getRating() + " " + match.getLeftPlayer().getPlayerName());
            leftPlayer.getStyle().set("font-size", "14px").set("font-weight", "bold");
            matchInfo.add(leftPlayer);

            // Scor jucător stânga
            Div leftScore = new Div();
            leftScore.setText(match.getScore() != null ? match.getScore().split("-")[0] : "N/A");
            leftScore.getStyle().set("font-size", "12px");
            matchInfo.add(leftScore);

            // Jucător dreapta
            Div rightPlayer = new Div();
            rightPlayer.setText("#" + match.getRightPlayer().getRating() + " " + match.getRightPlayer().getPlayerName());
            rightPlayer.getStyle().set("font-size", "14px").set("font-weight", "bold");
            matchInfo.add(rightPlayer);

            // Scor jucător dreapta
            Div rightScore = new Div();
            rightScore.setText(match.getScore() != null ? match.getScore().split("-")[1] : "N/A");
            rightScore.getStyle().set("font-size", "12px");
            matchInfo.add(rightScore);

            // Adăugare câștigător
            Div winnerInfo = new Div();
            winnerInfo.setText("Winner: #" + match.getWinner().getRating() + " " + match.getWinner().getPlayerName());
            winnerInfo.getStyle().set("font-weight", "bold").set("color", "#4CAF50");
            matchInfo.add(winnerInfo);

            matchLayout.add(matchInfo);
            roundLayout.add(matchLayout);
        }

        bracketLayout.add(roundLayout);
    }

    // Metodă auxiliară pentru a obține numele jucătorului după ID
    private String getPlayerNameById(Integer playerId) {
        // Această metodă trebuie să fie implementată pentru a returna numele jucătorului în funcție de ID
        // Exemplu: return databaseService.getPlayerName(playerId);
        return "Player " + playerId; // Placeholder
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer tournamentId) {

        Optional<Tournament> tournament = tournamentRepository.getById(tournamentId);

        if (!tournament.isPresent()) {
            throw new IllegalArgumentException("Tournament not found with id " + tournamentId);
        }

        // Add tournament details and bracket layout
        add(createTournamentDetails(tournament.orElse(null)));
        add(createBracketLayout(tournament.orElse(null)));
    }
}
