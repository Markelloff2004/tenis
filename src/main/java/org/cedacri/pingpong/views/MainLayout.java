package org.cedacri.pingpong.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

//@CssImport("./styles/main-layout-styles.css")
@CssImport("./themes/ping-pong-tournament/main-layout.css")
public class MainLayout extends AppLayout {

    private Button homeButton;
    private Button turneuButton;
    private Button jucatoriButton;
    private Button logoutButton;

    public MainLayout() {
        // Top bar
//        logoutButton = new Button("Log out", VaadinIcon.SIGN_OUT.create());
//        logoutButton.addClassName("logout-button");
//        addToNavbar(logoutButton);

        // Drawer content (side-menu)
        setPrimarySection(Section.DRAWER);

        // Logo & Admin Name section
        // This could be a VerticalLayout or Div with assigned classes
        // For simplicity, just adding HTML as brand area:
        H3 appName = new H3("TOURNAMENT");
        Div logoSection = new Div();
        logoSection.setWidthFull();
        logoSection.addClassName("logo-section");
        logoSection.getStyle().set("display", "flex");
        logoSection.getStyle().set("align-items", "center");
        logoSection.getStyle().set("justify-content", "center");
//        Image logo = new Image("src/main/resources/META-INF/resources/images/logo.png", "Logo");
//        logo.addClassName("app-logo");
//        Span adminName = new Span("Ciao" );
//        adminName.addClassName("admin-name");

//        logoSection.add(appName, logo, adminName);
        logoSection.add(appName);

        // Menu items
        homeButton = new Button("Home");
        homeButton.addClassName("transparent-button");
        homeButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("main view")));

        turneuButton = new Button("Tournament");
        turneuButton.addClassName("transparent-button");
        turneuButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("tournaments")));

        jucatoriButton = new Button("Players");
        jucatoriButton.addClassName("transparent-button");
        jucatoriButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("players")));

        // A layout for menu items
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
            case "turneu":
                turneuButton.addClassName("active");
                break;
            case "jucatori":
                jucatoriButton.addClassName("active");
                break;
            default:
                // No action
                break;
        }
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        // The current route can be obtained from the resolved navigation target
        String currentRoute = getCurrentRoute();
        highlightActiveMenuItem(currentRoute);
    }

    private String getCurrentRoute() {
        // One approach is to look at the page title or the URL:
        return getUI()
                .flatMap(ui -> ui.getInternals().getActiveViewLocation() != null
                        ? Optional.of(ui.getInternals().getActiveViewLocation().getPath())
                        : Optional.empty())
                .orElse("");
    }
}
