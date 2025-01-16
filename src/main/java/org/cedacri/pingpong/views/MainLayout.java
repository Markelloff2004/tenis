package org.cedacri.pingpong.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@CssImport("./themes/ping-pong-tournament/main-layout.css")
@Route("")
public class MainLayout extends AppLayout {

    private final Button homeButton;
    private final Button turneuButton;
    private final Button jucatoriButton;

    public MainLayout() {
        // Top bar
//        logoutButton = new Button("Log out", VaadinIcon.SIGN_OUT.create());
//        logoutButton.addClassName("logout-button");
//        addToNavbar(logoutButton);

        // Drawer content (side-menu)
        setPrimarySection(Section.DRAWER);

        // Logo & Admin Name section
        H3 appName = new H3("TOURNAMENT");
        Div logoSection = new Div();
        logoSection.setWidthFull();
        logoSection.addClassName("logo-section");
        logoSection.getStyle().set("display", "flex");
        logoSection.getStyle().set("align-items", "center");
        logoSection.getStyle().set("justify-content", "center");
        logoSection.add(appName);

        homeButton = new Button("Home");
        homeButton.addClassName("transparent-button");
        homeButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("home")));

        turneuButton = new Button("Tournament");
        turneuButton.addClassName("transparent-button");
        turneuButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("tournaments")));

        jucatoriButton = new Button("Players");
        jucatoriButton.addClassName("transparent-button");
        jucatoriButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("players")));

        VerticalLayout menuItemsLayout = new VerticalLayout(homeButton, turneuButton, jucatoriButton);
        menuItemsLayout.addClassName("menu-items");
        menuItemsLayout.setPadding(false);
        menuItemsLayout.setSpacing(false);

        VerticalLayout drawerLayout = new VerticalLayout(logoSection, menuItemsLayout);
        drawerLayout.addClassName("side-menu");
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(true);
        addToDrawer(drawerLayout);
    }

    private void highlightActiveMenuItem(String route) {
        homeButton.removeClassName("active");
        turneuButton.removeClassName("active");
        jucatoriButton.removeClassName("active");

        switch (route) {
            case "home":
                homeButton.addClassName("active");
                break;
            case "tournament":
                turneuButton.addClassName("active");
                break;
            case "players":
                jucatoriButton.addClassName("active");
                break;
            default:
                break;
        }
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        String currentRoute = getCurrentRoute();
        highlightActiveMenuItem(currentRoute);
    }

    private String getCurrentRoute() {
        return getUI()
                .flatMap(ui -> ui.getInternals().getActiveViewLocation() != null
                        ? Optional.of(ui.getInternals().getActiveViewLocation().getPath())
                        : Optional.empty())
                .orElse("");
    }
}