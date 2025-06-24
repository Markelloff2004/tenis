package org.cedacri.pingpong.views.tournaments.v2.components.navigation;

import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public interface TournamentNavigation {
    Component getComponent();
    void setSelectionListener(Consumer<Object> listener);
    Object getCurrentSelection();
    void refresh();
}
