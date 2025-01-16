package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.views.MainLayout;

@PageTitle("MainView")
@Route(value = "home", layout = MainLayout.class)
@Uses(Icon.class)
public class HomeView extends Composite<VerticalLayout> {
    public HomeView() {
    }
}
