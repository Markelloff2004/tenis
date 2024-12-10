package org.cedacri.pingpong.views.tournaments;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import java.util.ArrayList;
import java.util.List;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("AddTournamentView")
@Route(value = "tournaments/add", layout = MainLayout.class)
@Menu(order = 3, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
@Uses(Icon.class)
public class AddTournamentView extends Composite<VerticalLayout> {

    public AddTournamentView() {
        HorizontalLayout layoutRow5 = new HorizontalLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        H1 h1 = new H1();
        Button buttonPrimary = new Button();
        Button buttonPrimary2 = new Button();
        Button buttonPrimary3 = new Button();
        VerticalLayout layoutColumn5 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        VerticalLayout layoutColumn6 = new VerticalLayout();
        TextField textField = new TextField();
        VerticalLayout layoutColumn7 = new VerticalLayout();
        ComboBox comboBox = new ComboBox();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        VerticalLayout layoutColumn8 = new VerticalLayout();
        H1 h12 = new H1();
        VerticalLayout layoutColumn9 = new VerticalLayout();
        H1 h13 = new H1();
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        Grid basicGrid = new Grid(Player.class);
        Grid basicGrid2 = new Grid(Player.class);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow5.addClassName(Gap.MEDIUM);
        layoutRow5.setWidth("100%");
        layoutRow5.setHeight("min-content");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn3.getStyle().set("flex-grow", "1");
        layoutColumn4.getStyle().set("flex-grow", "1");
        h1.setText("Table Tenis");
        layoutColumn4.setAlignSelf(FlexComponent.Alignment.START, h1);
        h1.setWidth("150px");
        buttonPrimary.setText("Home");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("Tournament");
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary3.setText("Players");
        buttonPrimary3.setWidth("min-content");
        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layoutColumn5.setWidth("100%");
        layoutColumn5.getStyle().set("flex-grow", "1");
        layoutColumn5.setJustifyContentMode(JustifyContentMode.START);
        layoutColumn5.setAlignItems(Alignment.START);
        layoutRow2.setWidthFull();
        layoutColumn5.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.XLARGE);
        layoutRow2.addClassName(Padding.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        layoutRow2.setAlignItems(Alignment.START);
        layoutRow2.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutColumn6.setHeightFull();
        layoutRow2.setFlexGrow(1.0, layoutColumn6);
        layoutColumn6.setWidth("100%");
        layoutColumn6.setHeight("min-content");
        textField.setLabel("Max. Players amount");
        textField.setWidth("min-content");
        layoutColumn7.setHeightFull();
        layoutRow2.setFlexGrow(1.0, layoutColumn7);
        layoutColumn7.setWidth("100%");
        layoutColumn7.setHeight("min-content");
        comboBox.setLabel("Tournament Rule");
        comboBox.setWidth("min-content");
        setComboBoxSampleData(comboBox);
        layoutRow3.setWidthFull();
        layoutColumn5.setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.getStyle().set("flex-grow", "1");
        layoutColumn8.setHeightFull();
        layoutRow3.setFlexGrow(1.0, layoutColumn8);
        layoutColumn8.setWidth("100%");
        layoutColumn8.getStyle().set("flex-grow", "1");
        h12.setText("Available players");
        h12.setWidth("max-content");
        layoutColumn9.setHeightFull();
        layoutRow3.setFlexGrow(1.0, layoutColumn9);
        layoutColumn9.setWidth("100%");
        layoutColumn9.getStyle().set("flex-grow", "1");
        h13.setText("Participants");
        h13.setWidth("max-content");
        layoutRow4.setWidthFull();
        layoutColumn5.setFlexGrow(1.0, layoutRow4);
        layoutRow4.addClassName(Gap.MEDIUM);
        layoutRow4.setWidth("100%");
        layoutRow4.getStyle().set("flex-grow", "1");
        basicGrid.setWidth("100%");
        basicGrid.getStyle().set("flex-grow", "0");
        setGridSampleData(basicGrid);
        basicGrid2.setWidth("100%");
        basicGrid2.getStyle().set("flex-grow", "0");
        setGridSampleData(basicGrid2);
        getContent().add(layoutRow5);
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(layoutColumn3);
        layoutColumn3.add(layoutColumn4);
        layoutColumn4.add(h1);
        layoutColumn4.add(buttonPrimary);
        layoutColumn4.add(buttonPrimary2);
        layoutColumn4.add(buttonPrimary3);
        layoutRow.add(layoutColumn5);
        layoutColumn5.add(layoutRow2);
        layoutRow2.add(layoutColumn6);
        layoutColumn6.add(textField);
        layoutRow2.add(layoutColumn7);
        layoutColumn7.add(comboBox);
        layoutColumn5.add(layoutRow3);
        layoutRow3.add(layoutColumn8);
        layoutColumn8.add(h12);
        layoutRow3.add(layoutColumn9);
        layoutColumn9.add(h13);
        layoutColumn5.add(layoutRow4);
        layoutRow4.add(basicGrid);
        layoutRow4.add(basicGrid2);
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

    private void setComboBoxSampleData(ComboBox comboBox) {
        List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("first", "First", null));
        sampleItems.add(new SampleItem("second", "Second", null));
        sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new SampleItem("fourth", "Fourth", null));
        comboBox.setItems(sampleItems);
        comboBox.setItemLabelGenerator(item -> ((SampleItem) item).label());
    }

    private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    @Autowired()
    private PlayerService samplePersonService;
}
