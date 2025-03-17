package org.cedacri.pingpong.views.tournaments.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Match;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Score;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.enums.RoleEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.service.MatchService;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.SecurityUtils;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class MatchComponent extends HorizontalLayout {

    private final MatchService matchService;
    private final Tournament tournament;
    private final List<List<TextField>> scoreFields = new ArrayList<>();

    public MatchComponent(Match match, MatchService matchService, Tournament tournament, Runnable refreshMatches) {
        this.matchService = matchService;
        this.tournament = tournament;
        log.info("Initializing MatchComponent for match with id {}", match.getId());

        configureLayout();
        add(
                createMatchOrderDetails(match),
                createPlayersDetails(match),
                createScoreDetails(match, refreshMatches),
                createWinnerDetails(match)
        );
    }

    private void configureLayout() {
        setWidthFull();
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        getStyle().set("background-color", "#f8f8f8");
    }

    private VerticalLayout createMatchOrderDetails(Match match) {
        VerticalLayout matchIdDetails = new VerticalLayout();
        matchIdDetails.setMaxWidth("120px");
        matchIdDetails.setAlignItems(Alignment.START);
        matchIdDetails.setJustifyContentMode(JustifyContentMode.CENTER);

        Span spanDetails = new Span("Match: " + match.getPosition());
        matchIdDetails.add(spanDetails);
        return matchIdDetails;
    }

    private VerticalLayout createPlayersDetails(Match match) {
        log.debug("Initializing PlayersDetails for match with id {}", match.getId());

        VerticalLayout playerDetails = new VerticalLayout();
        playerDetails.setMaxWidth("300px");
        playerDetails.setAlignItems(Alignment.START);
        playerDetails.setJustifyContentMode(JustifyContentMode.CENTER);

        playerDetails.add(getPlayerLabel(match.getTopPlayer(), match, true));
        playerDetails.add(getPlayerLabel(match.getBottomPlayer(), match, false));

        return playerDetails;
    }

    private HorizontalLayout createScoreDetails(Match match, Runnable onEditScoreCallback) {
        log.debug("Creating score details for match '{}'", match.getId());

        HorizontalLayout scoreDetails = new HorizontalLayout();
        scoreDetails.setSpacing(false);
        scoreDetails.setPadding(false);
        scoreDetails.setAlignItems(Alignment.CENTER);
        scoreDetails.setJustifyContentMode(JustifyContentMode.BETWEEN);

        List<Score> matchScore = match.getScore();

        int setsCount = TournamentUtils.getNumsOfSetsPerMatch(match);

        for (int i = 0; i < setsCount; i++) {
            TextField topScoreField = createScoreField(matchScore, i, true);
            TextField bottomScoreField = createScoreField(matchScore, i, false);

            if (isReadOnly(match)) {
                topScoreField.setReadOnly(true);
                bottomScoreField.setReadOnly(true);
            }

            scoreFields.add(List.of(topScoreField, bottomScoreField));

            scoreDetails.add(
                    ViewUtils.createVerticalLayout(
                            JustifyContentMode.CENTER,
                            topScoreField,
                            bottomScoreField
                    )
            );
        }

        Button editScoreButton = createEditScoreButton(match, onEditScoreCallback);
        scoreDetails.add(editScoreButton);

        return scoreDetails;
    }

    private TextField createScoreField(List<Score> matchScore, int index, boolean isTop) {
        if (matchScore.size() > index) {
            return isTop
                    ? ViewUtils.createScoreField(matchScore.get(index).getTopPlayerScore())
                    : ViewUtils.createScoreField(matchScore.get(index).getBottomPlayerScore());
        }
        return ViewUtils.createScoreField(null);
    }

    private boolean isReadOnly(Match match) {
        return match.getTopPlayer() == null ||
                match.getBottomPlayer() == null ||
                match.getTournament().getTournamentStatus() == TournamentStatusEnum.FINISHED;
    }

    private Button createEditScoreButton(Match match, Runnable onEditScoreCallback) {
        Button editScoreButton = ViewUtils.createSecuredButton(
                "",
                ViewUtils.COLORED_BUTTON,
                () -> handleEditScore(match, onEditScoreCallback),
                RoleEnum.MANAGER, RoleEnum.ADMIN
        );

        editScoreButton.setIcon(VaadinIcon.PENCIL.create());
        editScoreButton.setMaxWidth("20px");

        if (match.getTournament().getTournamentStatus() == TournamentStatusEnum.FINISHED) {
             editScoreButton.setVisible(false);
        }

        return editScoreButton;
    }

    private void handleEditScore(Match match, Runnable onEditScoreCallback) {
        if (match.getTournament().getTournamentStatus().equals(TournamentStatusEnum.ONGOING)) {
            if (match.getWinner() != null) {
                showConfirmDialog(match, onEditScoreCallback);
            } else {
                updateMatchScore(match);
                onEditScoreCallback.run();
            }
        } else {
            NotificationManager.showInfoNotification("Unable to edit the score for a match in a finished tournament");
            onEditScoreCallback.run();
        }
    }

    private void showConfirmDialog(Match match, Runnable onEditScoreCallback) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.add(new Text("Are you sure about changing the score? This will change the current winner: " + match.getWinner().getName() + "?"));

        Button confirmButton = ViewUtils.createButton("Confirm", ViewUtils.COLORED_BUTTON, () -> {
            matchService.cleanAllFutureMatches(match);
            updateMatchScore(match);
            onEditScoreCallback.run();
            confirmDialog.close();
        });


        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, confirmDialog::close);

        HorizontalLayout buttonLayout = ViewUtils.createHorizontalLayout(JustifyContentMode.CENTER, confirmButton, cancelButton);
        confirmDialog.add(buttonLayout);

        confirmDialog.open();
    }


    private VerticalLayout createWinnerDetails(Match winner) {
        VerticalLayout winnerDetails = new VerticalLayout();
        winnerDetails.setMaxWidth("300px");
        winnerDetails.setAlignItems(Alignment.START);
        winnerDetails.setJustifyContentMode(JustifyContentMode.CENTER);

        if (winner.getWinner() != null)
            winnerDetails.add(ViewUtils.createPlayerLabel(winner.getWinner().getName() + " " + winner.getWinner().getSurname()));
        else
            winnerDetails.add(ViewUtils.createPlayerLabel("Match " + winner.getPosition() + " winner"));

        return winnerDetails;
    }

    public void updateMatchScore(Match match) {
        List<Score> newMatchScores = new ArrayList<>();

        for (List<TextField> scoreRow : scoreFields) {
            try {
                int topScore = Integer.parseInt(scoreRow.get(0).getValue().trim());
                int bottomScore = Integer.parseInt(scoreRow.get(1).getValue().trim());
                newMatchScores.add(new Score(topScore, bottomScore));

            } catch (NumberFormatException ignored) {
                //Ignore, because tak nado.
            }
        }

        if (!newMatchScores.isEmpty()) {
            String validationMessages = "";

            try {
                validationMessages = matchService.validateAndUpdateScores(match, newMatchScores);

                if (!validationMessages.isEmpty()) {
                    NotificationManager.showErrorNotification(validationMessages);
                }

                if (!newMatchScores.isEmpty()) {
                    NotificationManager.showInfoNotification("The Score for this match has been updated.");
                    log.info("Updated scores for match {}: {}", match.getId(), newMatchScores);
                }
            } catch (IllegalArgumentException illegalArgumentException) {
                NotificationManager.showErrorNotification(illegalArgumentException.getMessage() + validationMessages);
            }
        }

    }

    private Span getPlayerLabel(Player player, Match match, boolean isTop) {
        return player != null
                ? ViewUtils.createPlayerLabel(player.getName() + " " + player.getSurname())
                : getPreviousMatchWinner(match, isTop);
    }

    private Span getPreviousMatchWinner(Match match, boolean isTop) {
        List<Match> previousMatches = tournament.getMatches().stream()
                .filter(m -> Objects.nonNull(m.getNextMatch()) && Objects.equals(m.getNextMatch().getId(), match.getId()))
                .toList();

        if (previousMatches.isEmpty()) {
            return ViewUtils.createPlayerLabel("BYE");
        }

        Optional<Match> selectedMatch = previousMatches.stream()
                .filter(m -> (isTop && m.getPosition() % 2 == 1) || (!isTop && m.getPosition() % 2 == 0))
                .findFirst();

        return selectedMatch.map(matchObj -> ViewUtils.createPlayerLabel("Match " + matchObj.getPosition() + " winner"))
                .orElse(ViewUtils.createPlayerLabel("No previous match winner"));
    }

}
