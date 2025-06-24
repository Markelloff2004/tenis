package org.cedacri.pingpong.views.tournaments.v2.components.navigation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.model.tournament.TournamentRoundRobin;
import org.cedacri.pingpong.service.tournaments.BaseTournamentService;
import org.cedacri.pingpong.service.tournaments.UnifiedTournamentService;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RoundRobinNavigationPanel extends HorizontalLayout implements TournamentNavigation {
    private final BaseTournament tournament;
    private final UnifiedTournamentService tournamentService;
    private ComboBox<String> playerComboBox;
    private String currentSelection = "All";
    private Consumer<Object> selectionListener;

    public RoundRobinNavigationPanel(
            BaseTournament tournament,
            UnifiedTournamentService tournamentService
    ) {
        this.tournament = tournament;
        this.tournamentService = tournamentService;
        initLayout();
    }

    private void initLayout() {
        setAlignItems(Alignment.CENTER);

        playerComboBox = new ComboBox<>("Player Options");
        playerComboBox.setItems(getPlayerOptions());
        playerComboBox.setValue(currentSelection);

        playerComboBox.addValueChangeListener(e -> {
            currentSelection = e.getValue();
            if (selectionListener != null) {
                selectionListener.accept(currentSelection);
            }
        });

        Button ratingButton = ViewUtils.createButton("View Rating", ViewUtils.BUTTON, () -> {
            new TournamentRoundRobinRankingDetails((TournamentRoundRobin) tournament).open();
        });

        add(playerComboBox, ratingButton);
    }

    private List<String> getPlayerOptions() {
        List<String> options = new ArrayList<>(List.of("All"));
        tournament.getPlayers().forEach(p ->
                options.add(p.getName() + " " + p.getSurname())
        );
        return options;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void setSelectionListener(Consumer<Object> listener) {
        this.selectionListener = listener;
    }

    @Override
    public Object getCurrentSelection() {
        return currentSelection;
    }

    @Override
    public void refresh() {
        playerComboBox.setItems(getPlayerOptions());
        playerComboBox.setValue(currentSelection);
    }
}