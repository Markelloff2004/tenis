package org.cedacri.pingpong.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinSession;
import org.cedacri.pingpong.enums.RoleEnum;
import org.cedacri.pingpong.utils.ViewUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CssImport("./themes/ping-pong-tournament/main-layout.css")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final List<String> routes = Arrays.asList("home", "tournaments", "players");
    private List<Button> menuButtons;

    public MainLayout() {
        configureView();
    }

    private void configureView() {
        setPrimarySection(Section.DRAWER);

        VerticalLayout logoLayout = createLogoSection();

        VerticalLayout menuItemsLayout = createMenuItems();

        HorizontalLayout userSection = createUserSection();

        VerticalLayout drawerLayout = new VerticalLayout(logoLayout, menuItemsLayout);
        drawerLayout.setSizeFull();
        drawerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        drawerLayout.addClassName("side-menu");

        drawerLayout.setFlexGrow(1, menuItemsLayout);

        drawerLayout.add(userSection);
        drawerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        addToDrawer(drawerLayout);
    }

    private VerticalLayout createLogoSection() {
        H4 appName = new H4("TOURNAMENT");
        Image logo = new Image("images/logo.png", "Tournament Logo");
        logo.setWidth("160px");

        VerticalLayout logoLayout = new VerticalLayout(logo, appName);
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.addClassName("logo-section");

        return logoLayout;
    }

    private VerticalLayout createMenuItems() {
        menuButtons = Arrays.asList(
                ViewUtils.createButton("Home", "transparent-button", () -> navigateTo("home")),
                ViewUtils.createSecuredButton(
                        "Tournament",
                        "transparent-button",
                        () -> navigateTo("tournaments"),
                        RoleEnum.ADMIN, RoleEnum.MANAGER

                ),
                ViewUtils.createSecuredButton("Players",
                        "transparent-button",
                        () -> navigateTo("players"),
                        RoleEnum.ADMIN, RoleEnum.MANAGER
                )
        );

        VerticalLayout menuItemsLayout = new VerticalLayout(menuButtons.toArray(new Button[0]));
        menuItemsLayout.setAlignItems(FlexComponent.Alignment.START);
        menuItemsLayout.addClassName("menu-items");

        return menuItemsLayout;
    }

    private HorizontalLayout createUserSection() {
        HorizontalLayout userLayout = new HorizontalLayout();
        userLayout.setWidthFull();
        userLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            MenuBar userMenu = new MenuBar();

            MenuItem userItem = userMenu.addItem("Hi, " + username);

            SubMenu subMenu = userItem.getSubMenu();
            subMenu.addItem("Logout", e -> logout());

            userLayout.add(userMenu);
        } else {
            Button loginButton = new Button("Login", e -> navigateTo("login"));
            userLayout.add(loginButton);
        }

        return userLayout;
    }

    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().setLocation("/login");
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
