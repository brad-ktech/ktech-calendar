package com.ktech.calendar.views;


import com.ktech.calendar.entities.MedicalExpert;
import com.ktech.calendar.services.BillingCodeService;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Medical Expert WHO Codes")
@Route(value = "whocodes", layout = MainLayout.class)
public class BillingCodeView extends Div implements AfterNavigationObserver {

    private Grid<MedicalExpert> grid = new Grid<>();
    private List<MedicalExpert> mez = new ArrayList<>();
    private BillingCodeService bcs;
    private TextField filter = new TextField();
    private TextField newBillingCode = new TextField();


    public BillingCodeView(@Autowired BillingCodeService bcs) {
        addClassName("card-list-view");
        setSizeFull();
        this.bcs = bcs;
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(me -> createCard(me));
        filter.setPlaceholder("Filter by WHO code...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> updateList(e));
        add(filter, grid);
    }




    private HorizontalLayout createCard(MedicalExpert me) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        H4 who = new H4(me.getBillingCode().toUpperCase());

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(me.getFirstName() + " " + me.getLastName());
        name.addClassName("name");
        header.add(name);

        Span specialty = new Span(me.getSpecialty());
        specialty.addClassName("date");
        Span location = new Span(me.getLocation());
        HorizontalLayout details = new HorizontalLayout();
        details.add(specialty, location);

        Icon emailIcon = VaadinIcon.ENVELOPE.create();
        emailIcon.addClassName("icon");
        Span email = new Span(me.getEmail());
        email.addClassName("likes");
        Icon phoneIcon = VaadinIcon.PHONE.create();
        phoneIcon.addClassName("icon");
        Span phone = new Span(me.getPhone());
        phone.addClassName("comments");
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(emailIcon, email, phoneIcon, phone);

        VerticalLayout v = new VerticalLayout();
        v.add(header, details, actions);

        card.add(who, v);


        return card;
    }



    public void updateList(HasValue.ValueChangeEvent event){

        String value = (String) event.getValue();
        List<MedicalExpert> filtered = mez.stream()
                                          .filter(me -> me.getBillingCode().contains(value.toUpperCase()))
                                          .collect(Collectors.toList());
        filtered.sort(Comparator.comparing(MedicalExpert::getBillingCode));
        grid.setItems(filtered);

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        mez = bcs.getAllMedicalExperts();

        grid.setItems(mez);
    }

}