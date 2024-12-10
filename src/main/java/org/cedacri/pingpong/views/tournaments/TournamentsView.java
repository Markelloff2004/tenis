package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("TournamentsView")
@Route(value = "tournaments", layout = MainLayout.class)
//@Menu(order = 1, icon = LineAwesomeIconUrl.PEOPLE_CARRY_SOLID)
@Uses(Icon.class)
public class TournamentsView extends Composite<VerticalLayout> {

    public TournamentsView() {

    }
}
