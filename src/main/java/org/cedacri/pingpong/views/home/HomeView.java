package org.cedacri.pingpong.views.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.views.MainLayout;

@PageTitle("MainView")
@Route(value = "home", layout = MainLayout.class)
//@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@Uses(Icon.class)
public class HomeView extends Composite<VerticalLayout> {

//    @Autowired
//    private final PlayerService playerService;

    public HomeView(PlayerService playerService) {
//        HorizontalLayout layoutRow = new HorizontalLayout();
//        VerticalLayout layoutColumn2 = new VerticalLayout();
//        H1 h1 = new H1();
//        Button buttonPrimary = new Button();
//        Button buttonPrimary2 = new Button();
//        Button buttonPrimary3 = new Button();
//        VerticalLayout layoutColumn3 = new VerticalLayout();
//        H1 h12 = new H1();
//        Button buttonPrimary4 = new Button();
//        Grid basicGrid = new Grid(Player.class);
//        getContent().setWidth("100%");
//        getContent().getStyle().set("flex-grow", "1");
//        layoutRow.addClassName(Gap.MEDIUM);
//        layoutRow.setWidth("100%");
//        layoutRow.getStyle().set("flex-grow", "1");
//        layoutColumn2.getStyle().set("flex-grow", "1");
//        h1.setText("Table Tenis");
//        layoutColumn2.setAlignSelf(FlexComponent.Alignment.START, h1);
//        h1.setWidth("150px");
//        buttonPrimary.setText("Home");
//        buttonPrimary.setWidth("min-content");
//        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        buttonPrimary2.setText("Tournament");
//        buttonPrimary2.setWidth("min-content");
//        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        buttonPrimary3.setText("Players");
//        buttonPrimary3.setWidth("min-content");
//        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        layoutColumn3.setWidth("100%");
//        layoutColumn3.getStyle().set("flex-grow", "1");
//        h12.setText("Tournaments List");
//        layoutColumn3.setAlignSelf(FlexComponent.Alignment.START, h12);
//        h12.setWidth("max-content");
//        buttonPrimary4.setText("START TOURNAMENT");
//        buttonPrimary4.setWidth("min-content");
//        buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        basicGrid.setWidth("100%");
//        basicGrid.getStyle().set("flex-grow", "0");
//        setGridSampleData(basicGrid);
//        getContent().add(layoutRow);
//        layoutRow.add(layoutColumn2);
//        layoutColumn2.add(h1);
//        layoutColumn2.add(buttonPrimary);
//        layoutColumn2.add(buttonPrimary2);
//        layoutColumn2.add(buttonPrimary3);
//        layoutRow.add(layoutColumn3);
//        layoutColumn3.add(h12);
//        layoutColumn3.add(buttonPrimary4);
//        layoutColumn3.add(basicGrid);
//        this.playerService = playerService;
//
//        buttonPrimary4.addClickListener(event -> openAddPlayerForm());


    }

//     private void openAddPlayerForm() {
//        Dialog dialog = new Dialog();
//        AddPlayerFormView addPlayerForm = new AddPlayerFormView();
//        dialog.add(addPlayerForm);
//        dialog.setWidth("800px");
//        dialog.setHeight("500px");
//        dialog.open();
//     System.out.println("Hello world");
//    }

//    private void setGridSampleData(Grid grid) {
//        grid.setItems(query -> playerService.list(
//                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
//                .stream());
//    }

}
