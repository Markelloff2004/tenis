package org.cedacri.pingpong.views.tournaments.v2;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.model.enums.TournamentStatusEnum;
import org.cedacri.pingpong.model.enums.TournamentTypeEnum;
import org.cedacri.pingpong.model.match.Match;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.service.matches.MatchService;
import org.cedacri.pingpong.service.players.PlayerService;
import org.cedacri.pingpong.service.tournaments.UnifiedTournamentService;
import org.cedacri.pingpong.utils.Constants;
import org.cedacri.pingpong.utils.NotificationManager;
import org.cedacri.pingpong.utils.ViewUtils;
import org.cedacri.pingpong.views.MainLayout;
import org.cedacri.pingpong.views.tournaments.v2.components.matches.MatchComponent;
import org.cedacri.pingpong.views.tournaments.v2.components.navigation.OlympicNavigationPanel;
import org.cedacri.pingpong.views.tournaments.v2.components.navigation.RoundRobinNavigationPanel;
import org.cedacri.pingpong.views.tournaments.v2.components.navigation.TournamentNavigation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Route(value = "tournament/matches", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class TournamentBracketView extends VerticalLayout implements HasUrlParameter<Long> {

    private final UnifiedTournamentService tournamentService;
    private final MatchService matchService;
    private final PlayerService playerService;

    private BaseTournament baseTournament;
    private TournamentNavigation navigationComponent;
    private VerticalLayout matchContainer;

    public TournamentBracketView(UnifiedTournamentService tournamentService,
                                 MatchService matchService,
                                 PlayerService playerService) {

        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.playerService = playerService;
        log.info("TournamentBracketView initialized.");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long tournamentId) {
        log.info("Received request to load tournament with ID {}", tournamentId);
        baseTournament = tournamentService.getDefaultCrudService().findTournamentById(tournamentId);

        if (baseTournament == null) {
            log.warn("Tournament with ID {} not found", tournamentId);
            add(new Span("Tournament not found"));
            return;
        }

        log.info("Tournament found: {}", baseTournament);
        initView();
    }
    private void initView() {
        log.info("Initializing view for tournament {}", baseTournament.getTournamentName());
        removeAll();

        // Header
        add(createHeader());

        // Navigation component
        add(createNavigationComponent());

        // Match container
        matchContainer = new VerticalLayout();
        matchContainer.setWidthFull();
        matchContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        add(matchContainer);

        // End tournament button if needed
        addEndTournamentButtonIfNeeded();

        // Initial load of matches
        refreshAfterMatchUpdate();
    }

    private Component createHeader() {
        return ViewUtils.createHorizontalLayout(
                JustifyContentMode.BETWEEN,
                new H1(baseTournament.getTournamentName())
        );
    }

    private Component createNavigationComponent() {
        TournamentTypeEnum type = baseTournament.getTournamentType();

        if (type == TournamentTypeEnum.OLYMPIC) {
            navigationComponent = new OlympicNavigationPanel(baseTournament);
        } else if (type == TournamentTypeEnum.ROUND_ROBIN) {
            navigationComponent = new RoundRobinNavigationPanel(baseTournament, tournamentService);
        } else {
            return new Span("Tournament type not supported");
        }

        navigationComponent.setSelectionListener(this::refreshMatches);
        return navigationComponent.getComponent();
    }

    private void addEndTournamentButtonIfNeeded() {
        if (baseTournament.getTournamentStatus() == TournamentStatusEnum.ONGOING) {
            Button endButton = ViewUtils.createButton("End Tournament", ViewUtils.BUTTON, () -> {
                refreshTournament();

                try {
                    if (tournamentService.isReadyToEndTournament(baseTournament)) {
                        showEndTournamentDialog();
                    } else {
                        NotificationManager.showErrorNotification(
                                Constants.TOURNAMENT_WINNER_CANT_BE_DETERMINATE
                        );
                    }
                } catch (IllegalStateException e) {
                    NotificationManager.showErrorNotification(e.getMessage());
                }
            });

            add(new HorizontalLayout(endButton));
        }
    }

    private void showEndTournamentDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm end tournament: " + baseTournament.getTournamentName());
        dialog.add("Are you sure you want to end the tournament?");

        Button confirmButton = ViewUtils.createButton("Confirm", ViewUtils.COLORED_BUTTON, () -> {
            try {
                tournamentService.endTournament(baseTournament);
                dialog.close();
                refreshTournament();
            } catch (IllegalStateException e) {
                NotificationManager.showErrorNotification(e.getMessage());
                dialog.close();
            }
        });

        Button cancelButton = ViewUtils.createButton("Cancel", ViewUtils.BUTTON, dialog::close);
        dialog.add(new HorizontalLayout(confirmButton, cancelButton));
        dialog.open();
    }

    private void refreshMatches(Object selection) {
        List<Match> matches = getMatchesForSelection(selection);
        displayMatches(matches);
    }

    private List<Match> getMatchesForSelection(Object selection) {
        if (selection instanceof Integer round) {
            return baseTournament.getMatches().stream()
                    .filter(m -> m.getRound() == round)
                    .toList();
        } else if (selection instanceof String playerFilter) {
            if ("All".equals(playerFilter)) {
                return baseTournament.getMatches();
            } else {
                String[] names = playerFilter.split(" ");
                if (names.length >= 2) {
                    return matchService.getMatchesByPlayerNameSurname(
                            baseTournament.getMatches(),
                            names[0],
                            names[1]
                    );
                }
            }
        }
        return Collections.emptyList();
    }

    private void displayMatches(List<Match> matches) {
        matchContainer.removeAll();

        matches.stream()
                .sorted(Comparator.comparing(Match::getPosition).reversed())
                .forEach(match -> {
                    MatchComponent matchComponent = new MatchComponent(
                            match,
                            matchService,
                            baseTournament,
                            this::refreshAfterMatchUpdate
                    );
                    matchContainer.add(matchComponent);
                });
    }

    private void refreshAfterMatchUpdate() {
        refreshTournament();
        refreshMatches(navigationComponent.getCurrentSelection());
    }

    private void refreshTournament() {
        this.baseTournament = tournamentService.getDefaultCrudService().findTournamentById(baseTournament.getId());
        if (navigationComponent != null) {
            navigationComponent.refresh();
        }
    }
}