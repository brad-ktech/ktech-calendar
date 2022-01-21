package com.ktech.calendar.views;

import com.ktech.calendar.entities.CalendarEntry;
import com.ktech.calendar.entities.CalendarType;
import com.ktech.calendar.entities.MedicalExpert;
import com.ktech.calendar.services.CalendarService;
import com.ktech.starter.entities.Matter;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.FileDownloadWrapper;
import org.vaadin.stefan.fullcalendar.*;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Calendar")
@Route(value = "calendar/", layout = MainLayout.class)
public class CalendarView extends VerticalLayout implements BeforeEnterObserver{


    private CalendarService service;
    private List<CalendarEntry> entries = new ArrayList<>();
    private FullCalendar calendar = null;
    private LocalDateTime currentMonth = LocalDateTime.now();
    private VerticalLayout calendarLayout = new VerticalLayout();
    private HorizontalLayout title = null;
    private H2 name = null;
    private MedicalExpert expert;
    private long offset = 0;


    //new entry dialog fields
    private TextField location = new TextField();
    private DateTimePicker startPicker = new DateTimePicker();
    private DateTimePicker endPicker = new DateTimePicker();
    private TextArea newEntryDescription = new TextArea();
    private Checkbox allDay = new Checkbox("All Day Event");
    private Select<CalendarType> select = new Select<>();


    public CalendarView(@Autowired CalendarService service) {
        setSpacing(false);
        addClassName("list-view");
        this.service = service;
        calendar = FullCalendarBuilder.create().build();
        calendar.setVisible(true);
        calendar.setHeightAuto();

        calendar.addDayNumberClickedListener(e -> addEntry(e));
        calendar.addEntryClickedListener(e -> clicked(e));

        calendarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        calendarLayout.setSizeFull();
        calendarLayout.setWidthFull();
        calendarLayout.setHeightFull();
        title = getMonthTitle(0);
        calendarLayout.add(title, calendar, getButtonLayout());
        add(calendarLayout);

    }


    private HorizontalLayout getMonthTitle(long offset){
        HorizontalLayout title = new HorizontalLayout();
        currentMonth = currentMonth.plusMonths(offset);
        H2 month = new H2(currentMonth.getMonth().getDisplayName( TextStyle.FULL , Locale.getDefault()) + " " +currentMonth.getYear());
        title.setJustifyContentMode(JustifyContentMode.CENTER);
        title.add(month);
        title.setWidthFull();
        return title;
    }

    private HorizontalLayout getButtonLayout(){

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button next = new Button("Next", new Icon(VaadinIcon.ARROW_CIRCLE_RIGHT));
        next.setIconAfterText(true);
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        next.addClickListener(e -> {
            HorizontalLayout newTitle = getMonthTitle(1);
            calendarLayout.replace(title, newTitle);
            title = newTitle;
            calendar.next();
            offset += 1;
            calendar.removeAllEntries();
            getEntries();
        });


        Button previous = new Button("Previous", new Icon(VaadinIcon.ARROW_CIRCLE_LEFT));
        previous.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        previous.addClickListener(e ->{
            HorizontalLayout newTitle = getMonthTitle(-1);
            calendarLayout.replace(title, newTitle);
            title = newTitle;
            calendar.previous();
            offset -= 1;
            calendar.removeAllEntries();
            getEntries();
        });


        Button download = new Button(VaadinIcon.DOWNLOAD.create());
        download.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        download.addClickListener(e -> {
            Notification downloading = Notification.show("Downloading...", 7000, Notification.Position.BOTTOM_END);
            downloading.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper("calendar.csv", () -> getResource());

        buttonWrapper.wrapComponent(download);


        buttonLayout.add(previous, buttonWrapper, next);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();
        return buttonLayout;

    }


    private byte[] getResource() {

        byte[] bytes = null;
        try {
            bytes =  service.download(offset, expert.getContactId());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {

        }
        return bytes;
    }


    private void addEntry(DayNumberClickedEvent event){

        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setOpened(true);
        dialog.setCloseOnEsc(true);
        dialog.add(new H3("Add Calendar Event"));
        dialog.setWidth("600");
        dialog.setHeight("900");

        select.setLabel("Calendar");
        select.setItemLabelGenerator(CalendarType::getPrettyName);
        select.setItems(service.getTypes());
        select.isRequiredIndicatorVisible();
        select.setEmptySelectionAllowed(false);


        location.setLabel("Location");
        location.setPrefixComponent(VaadinIcon.MAP_MARKER.create());

        HorizontalLayout hl1 = new HorizontalLayout();
        hl1.add(select, location);

        LocalDate clickedDate = event.getDate();
        HorizontalLayout startdate = new HorizontalLayout();
        startPicker.setLabel("Meeting Start Date And Time");
        startPicker.setStep(Duration.ofMinutes(15));
        startPicker.setValue(clickedDate.atStartOfDay());
        startdate.add(startPicker);

        HorizontalLayout endDate = new HorizontalLayout();
        endPicker.setLabel("Meeting End Date And Time");
        endPicker.setStep(Duration.ofMinutes(15));
        endPicker.setValue(clickedDate.atTime(16, 0));
        endDate.add(endPicker);






        newEntryDescription.setWidthFull();
        newEntryDescription.setLabel("Event Description");
        newEntryDescription.setPlaceholder("Write event description here");
        newEntryDescription.setSizeFull();

        HorizontalLayout actions = new HorizontalLayout();
        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        save.addClickListener(e -> saveEntry());



        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.CENTER);
        actions.add(save, cancel);

        dialog.add(hl1, startdate, endDate, allDay, newEntryDescription, actions);

        dialog.setCloseOnEsc(false);
        dialog.open();

    }

    private void saveEntry(){

        com.ktech.starter.models.CalendarEntry entry = new com.ktech.starter.models.CalendarEntry();
        entry.setDescription(newEntryDescription.getValue());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        entry.setStartAt(startPicker.getValue().format(formatter));
        entry.setEndAt(endPicker.getValue().format(formatter));
        entry.setLocation(location.getValue());
        entry.setAllDay(allDay.getValue());


        try {
            service.saveCalendarEntry(entry);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void clicked(EntryClickedEvent event){

        //Set up dialog box
        Entry entry = event.getEntry();
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setOpened(true);
        dialog.setCloseOnEsc(true);
        dialog.add(new H3("  Event Details"));
        dialog.add(new H5("  "+entry.getTitle()));
        dialog.setWidth("600px");


        HorizontalLayout dates = createDateLayout(entry);

        //Set up accordion data
        Accordion accordion = createAccordian(entry);

        HorizontalLayout actions = createActionButtons(dialog, entry);

        VerticalLayout layout = new VerticalLayout();

        Integer allDay = (Integer)entry.getCustomProperties().get("allDay");
        if(BooleanUtils.toBoolean(allDay)){

            Button badge = new Button("All Day Event");
            badge.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            layout.add(dates, badge, accordion, actions);
        }else{
            layout.add(dates, accordion, actions);
        }


        dialog.add(layout);

    }

    private HorizontalLayout createDateLayout(Entry entry){
        //Set up horizontal row with date values
        HorizontalLayout dates = new HorizontalLayout();
        Label datesLabel = new Label();
        String fromDate = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy  'at' hh:mm a 'until '").format(entry.getStart());
        String toDate =  DateTimeFormatter.ofPattern("hh:mm a").format(entry.getEnd());
        datesLabel.add(fromDate +  toDate);
        dates.add(datesLabel);

        return dates;
    }

    private HorizontalLayout createActionButtons(Dialog dialog, Entry entry){

        HorizontalLayout actions = new HorizontalLayout();
        Button closer = new Button("Close");
        closer.addClickListener(e->dialog.close());
        closer.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.CENTER);
        actions.add(closer);
        return actions;
    }

    private Accordion createAccordian(Entry entry){

        //set up accordian for event details
        Accordion accordion = new Accordion();
        //set up event description
        VerticalLayout description = new VerticalLayout();
        String[] spans = entry.getDescription().split("--");
        Arrays.stream(spans).forEach(s -> {
            Span text = new Span(s);
            description.add(text);
        });
        accordion.add("  Event Details", description);


        //set up associated matter if it exists
        Long matterId = (Long)entry.getCustomProperties().get("matterId");
        Optional<Matter> opt = service.getMatterForEntry(matterId);
        if(opt.isPresent()){
            VerticalLayout vl = new VerticalLayout();
            Matter matter = opt.get();

            Span title = new Span("Matter - " + matter.getDisplayNumber());
            Span status = new Span("Status - " + matter.getStatus());
            Span desc = new Span(matter.getDescription());
            vl.add(title, status, desc);

            accordion.add("Matter", vl);
        }

        String location = (String)entry.getCustomProperties().get("location");
        accordion.add("Location", new Span(location));


        accordion.close();
        return accordion;
    }



    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
       Location location = beforeEnterEvent.getLocation();
       Map<String, List<String>> map = location.getQueryParameters().getParameters();
        List<String> values = map.get("contact_id") ;

       if(ObjectUtils.isNotEmpty(values)) {

           String contactId = values.stream().findFirst().orElse(null);


           if (ObjectUtils.isNotEmpty(contactId)) {

               Optional<MedicalExpert> meOpt = service.getMedicalExpertFromContactId(contactId);
               if (meOpt.isPresent()) {
                   expert = meOpt.get();
                   entries.addAll(service.getCalendarEntriesForMedicalExpert(expert));
                   getEntries();
               }
           }
       }

    }


    private void getEntries() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a");

        List<CalendarEntry> thisMonth = entries.stream()
                .filter(ca -> ca.getStartAt().getMonth().equals(currentMonth.getMonth()))
                .collect(Collectors.toList());

        thisMonth.stream().forEach(kce -> {
           Entry entry = new Entry();
           entry.setTitle(kce.getTitle());
           entry.setStart(kce.getStartAt());
           entry.setEnd(kce.getEndAt());
           entry.setDescription(kce.getDescription());
           Optional<CalendarType> opt = service.getType(kce.getType());

           if(opt.isPresent()){
               CalendarType type = opt.get();
               entry.setColor(type.getHex());
           }


           entry.setCustomProperty("entryId", kce.getId());
           entry.setCustomProperty("matterId", kce.getMatter());
           entry.setCustomProperty("location", kce.getLocation());
           entry.setCustomProperty("allDay", kce.getAllDay());

           calendar.addEntry(entry);



        });




    }




}
