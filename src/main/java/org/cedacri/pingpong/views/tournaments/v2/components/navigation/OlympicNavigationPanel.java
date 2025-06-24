package org.cedacri.pingpong.views.tournaments.v2.components.navigation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.cedacri.pingpong.model.tournament.BaseTournament;
import org.cedacri.pingpong.utils.TournamentUtils;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OlympicNavigationPanel extends HorizontalLayout implements TournamentNavigation {
    private final BaseTournament tournament;
    private final List<Button> roundButtons = new ArrayList<>();
    private int currentRound = 1;
    private Consumer<Object> selectionListener;

    public OlympicNavigationPanel(BaseTournament tournament) {
        this.tournament = tournament;
        initLayout();
    }

    private void initLayout() {
        setJustifyContentMode(JustifyContentMode.START);

        int roundsCount = TournamentUtils.calculateNumberOfRounds(tournament.getMaxPlayers());
        Map<Integer, String> specialRounds = Map.of(
                roundsCount - 1, "Semifinals",
                roundsCount, "Finals"
        );

        for (int i = 1; i <= roundsCount; i++) {
            int round = i;
            String label = specialRounds.getOrDefault(i, "Stage " + round);

            Button roundButton = new Button(label, e -> {
                currentRound = round;

                highlightSelectedButton();
                addClassName("selected");


                if (selectionListener != null) {
                    selectionListener.accept(round);
                }
            });

            roundButton.addClassName(ViewUtils.BUTTON);
            roundButtons.add(roundButton);
            add(roundButton);
        }

        if (!roundButtons.isEmpty()) {
            highlightSelectedButton();
            roundButtons.get(0).addClassName("selected");
        }
    }

    private void highlightSelectedButton() {
        roundButtons.forEach(b -> b.removeClassName("selected"));
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
        return currentRound;
    }

    @Override
    public void refresh() {
        removeAll();
        roundButtons.clear();
        initLayout();
    }
}