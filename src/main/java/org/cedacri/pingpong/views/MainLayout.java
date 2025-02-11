package org.cedacri.pingpong.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import org.cedacri.pingpong.utils.ViewUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CssImport("./themes/ping-pong-tournament/main-layout.css")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private List<Button> menuButtons;
    private final List<String> routes = Arrays.asList("home", "tournaments", "players");

    public MainLayout() {
        configureView();
    }

    private void configureView() {
        setPrimarySection(Section.DRAWER);

        H4 appName = new H4("TOURNAMENT");
        Image logo = new Image("images/logo.png", "Tournament Logo");
        logo.setWidth("160px");
        logo.setHeight("auto");

        VerticalLayout logoLayout = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START, logo, appName);
        logoLayout.addClassName("logo-section");

        menuButtons = Arrays.asList(
                ViewUtils.createButton("Home", "transparent-button", () -> navigateTo("home")),
                ViewUtils.createButton("Tournament", "transparent-button", () -> navigateTo("tournaments")),
                ViewUtils.createButton("Players", "transparent-button", () -> navigateTo("players"))
        );

        VerticalLayout menuItemsLayout = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START, menuButtons.toArray(new Button[0]));
        menuItemsLayout.addClassName("menu-items");

        VerticalLayout drawerLayout = ViewUtils.createVerticalLayout(FlexComponent.JustifyContentMode.START, logoLayout, menuItemsLayout);
        drawerLayout.addClassName("side-menu");

        addToDrawer(drawerLayout);
    }

    private void navigateTo(String route) {
        UI.getCurrent().navigate(route);
    }

    private void highlightActiveMenuItem(String route) {
        int selectedIndex = routes.indexOf(route);
        ViewUtils.highlightSelectedComponentFromComponentsList(menuButtons, selectedIndex, "selected");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        highlightActiveMenuItem(getCurrentRoute());
    }

    private String getCurrentRoute() {
        return Optional.ofNullable(
                    UI.getCurrent()
                            .getInternals()
                            .getActiveViewLocation()
                            .getPath())
                .orElse("home");
    }
}
