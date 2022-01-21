package com.ktech.calendar.views;

import com.ktech.starter.dao.DaoAccelerator;
import com.vaadin.addon.leaflet4vaadin.LeafletMap;
import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Expert Events")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ListView extends Div implements AfterNavigationObserver {

    private Grid<String> grid = new Grid<>();
    private TextField title = new TextField("Title");
    private TextField location = new TextField("Location");
    private TextField matter = new TextField("Matter");
    private TextArea description = new TextArea ("Description");
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Dialog dialog;
    private DaoAccelerator dao;


    public ListView(@Autowired DaoAccelerator dao) {
        this.dao = dao;
        addClassName("list-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        //grid.addComponentColumn(expert -> createCard(expert));
        add(grid);
    }

    private void openModel(){

        dialog = new Dialog();
        dialog.setModal(true);
        dialog.setOpened(true);
        dialog.setCloseOnEsc(true);
        dialog.add(new H3("Event Details"));
        dialog.add(getForm());
        dialog.add(getButtonLayout());

    }

    private VerticalLayout getForm(){
        VerticalLayout formLayout = new VerticalLayout();
        title.setValue("Deposition");
        location.setValue("JM Office 123 Park Ln Suite 1330");
        matter.setValue("079741-AGEERS");
        description.setValue("This would be the full detailed description of the calendar event.");
        description.setSizeFull();
        formLayout.add(title);
        HorizontalLayout middle = new HorizontalLayout();
        middle.add(location, matter);
        formLayout.add(middle);
        formLayout.add(description);

        return formLayout;


    }

    private void close(){
        dialog.close();

    }

    private HorizontalLayout getButtonLayout(){
        HorizontalLayout buttons = new HorizontalLayout();
        save = new Button("Save");
        cancel = new Button("Cancel");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(c->save());
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(c->close());
        buttons.add(save, cancel);
        buttons.setAlignItems(FlexComponent.Alignment.CENTER);
        return buttons;
    }

    private void save(){
        Notification notification = Notification.show("Event Saved");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_END);
        close();

    }

    /*
    private HorizontalLayout createCard(Expert expert) {

        Faker faker = new Faker();

        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");


        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");

        String fromDate = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy  'at' hh:mm a 'until '").format(LocalDateTime.now());
        String toDate =  DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.now().plusHours(4));

        H3 eventDate  = new H3(fromDate + toDate);
        eventDate.addClassName("name");

        header.add(eventDate );

        HorizontalLayout titleLayout = new HorizontalLayout();
        Span title = new Span(("Deposition"));
        titleLayout.add(title);
        title.addClassName("date");

        HorizontalLayout locationLayout = new HorizontalLayout();
        locationLayout.addClassName("date");
        //locationLayout.setSpacing(false);
       // locationLayout.getThemeList().add("spacing-s");

        Span location = new Span(faker.address().fullAddress());
        location.addClassName("date");
        locationLayout.add(location);

        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.addClassName("date");

        Span description = new Span(faker.lorem().paragraph());
        description.addClassName("date");
        descriptionLayout.add(description);

        HorizontalLayout actions = new HorizontalLayout();
        Span email = new Span("Email Event to Expert");
        email.addClickListener(e->email());
        email.addClassName("post");

        Span details = new Span("View Details");
        details.addClassName("post");
        details.addClickListener(d -> openModel());

        Span map = new Span("View Map");
        map.addClassName("post");
        map.addClickListener(d -> openMap());

        Span sms = new Span("Text Event To Expert");
        sms.addClassName("post");
        sms.addClickListener(d -> sms());

        //actions.add(details, email, map);
        actions.add(details, email, sms);

        layout.add(header, titleLayout, descriptionLayout, locationLayout, actions);//, post, actions);
        card.add(layout);
        return card;
    }
*/
    private void sms(){

        Notification notification = Notification.show("Text Message Sent");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_END);
    }

    private void openMap(){

        Dialog map = new Dialog();
        map.setModal(true);
        map.setOpened(true);
        map.setCloseOnEsc(true);
        map.add(new H3("Event Map"));
        map.add(getMap());


    }

    private LeafletMap getMap(){

        LeafletMap map = new LeafletMap();
        map.setHeight("200");
        map.setWidth("200");
        map.setSizeFull();
        map.setVisible(true);
        LatLng latlong = LatLng.latlng(32.912740, -97.305980);
        map.setView(latlong, 0);

        return map;
    }

    private void email(){
        Notification notification = Notification.show("Email Sent");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_END);

    }



    @Override
    public void afterNavigation(AfterNavigationEvent event) {


       // grid.setItems(experts);
    }



}
