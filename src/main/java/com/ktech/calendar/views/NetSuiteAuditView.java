package com.ktech.calendar.views;


import com.ktech.calendar.entities.NetSuiteAudit;
import com.ktech.calendar.services.NetSuiteAuditLogService;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@PageTitle("NetSuite Audit Log")
@Route(value = "netsuiteauditlog", layout = MainLayout.class)
public class NetSuiteAuditView extends Div implements AfterNavigationObserver {


    private NetSuiteAuditLogService service;
    private Grid<NetSuiteAudit> grid = new Grid<>();
    private List<NetSuiteAudit> logs = new ArrayList<>();


    public  NetSuiteAuditView(@Autowired NetSuiteAuditLogService service){


        setSizeFull();
        this.service = service;
        grid.setHeight("100%");


        grid.addComponentColumn(nsa -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.add(getIcon(nsa.getStatus()), new Span(nsa.getStatus()));
            return layout;
        }).setHeader(getStatusFilter());
        grid.addColumn(NetSuiteAudit::getClioId).setHeader(getClioIdFilter());
        grid.addColumn(NetSuiteAudit::getNetsuiteId).setHeader(getNetSuiteIDFilter());
        grid.addColumn(NetSuiteAudit::getType).setHeader(getDataTypeFilter());
        grid.addColumn(NetSuiteAudit::getPrettyTime).setHeader(getDateRangePicker());
        grid.addComponentColumn(nsa -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button retry = new Button(new Icon(VaadinIcon.RECYCLE));
            retry.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            retry.setVisible("INVOICE".equalsIgnoreCase(nsa.getType()) && "FAILED".equalsIgnoreCase(nsa.getStatus()));
            retry.setDisableOnClick(true);
            retry.addClickListener(clicked -> {
                Notification retrying = Notification.show("Retrying...", 7000, Notification.Position.BOTTOM_END);
                retrying.addThemeVariants(NotificationVariant.LUMO_ERROR);
                this.service.retry(nsa);
            });
            layout.add(retry);
            return layout;
        }).setHeader("Retry");

        add(grid);
    }

    private Component getDateRangePicker(){

        DatePicker datePicker = new DatePicker("Start date");
        datePicker.setLabel("Select Date");
        datePicker.setPlaceholder("DD/MM/YYYY");
        datePicker.setHelperText("The date sent to NetSuite");
        datePicker.setInitialPosition(LocalDate.now());
        datePicker.setRequiredIndicatorVisible(true);
        datePicker.addValueChangeListener(e -> getData(e));

        return datePicker;
    }

    private void getData(AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate> datePicker){

       LocalDate date = datePicker.getValue();
       logs =  service.getLogs(date);

       grid.setItems(logs);

    }

    private Component getDataTypeFilter(){

        Select<String> filter = new Select<>();
        filter.setLabel("Filter By Data Type");
        filter.setItems("ALL", "CUSTOMER", "INVOICE", "EMPLOYEE", "JOB", "VENDOR", "CUSTOMER_PAYMENT", "CREDIT_MEMO");
        filter.setPlaceholder("Select type");
        filter.addValueChangeListener(e -> filterByDataType(e));
        return filter;

    }

    private void filterByDataType(HasValue.ValueChangeEvent event){

        String value = (String) event.getValue();
        if("ALL".equalsIgnoreCase(value)){
            grid.setItems(logs);
        }else{
            List<NetSuiteAudit> filtered = logs.stream()
                    .filter(log -> log.getType().equalsIgnoreCase(value))
                    .collect(Collectors.toList());
            grid.setItems(filtered);
        }

    }

    private Component getNetSuiteIDFilter(){

        TextField filter = new TextField();
        filter.setLabel("NetSuite ID");
        filter.setPlaceholder("Filter by NetSuite ID...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> filterNetSuiteId(e));
        return filter;

    }

    private void filterNetSuiteId(HasValue.ValueChangeEvent event){

        String value = (String) event.getValue();
        List<NetSuiteAudit> filtered = logs.stream()
                .filter(log -> log.getNetsuiteId().toString().contains(value.toUpperCase()))
                .collect(Collectors.toList());
        grid.setItems(filtered);

    }

    private Component getClioIdFilter(){

        TextField filter = new TextField();
        filter.setLabel("Clio ID");
        filter.setPlaceholder("Filter by Clio ID...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> filterClioId(e));
        return filter;
    }

    private void filterClioId(HasValue.ValueChangeEvent event){

        String value = (String) event.getValue();
        List<NetSuiteAudit> filtered = logs.stream()
                    .filter(log -> log.getClioId().toString().contains(value.toUpperCase()))
                    .collect(Collectors.toList());
        grid.setItems(filtered);


    }


    private Component getStatusFilter(){

        Select<String> filter = new Select<>();
        filter.setLabel("Filter By Status");
        filter.setItems("ALL", "SUCCESS", "FAILED", "SKIPPED", "FORCED_SUCCESS");
        filter.setPlaceholder("Select status");
        filter.addValueChangeListener(e -> filterStatus(e));
        return filter;
    }

    private void filterStatus(HasValue.ValueChangeEvent event) {

        String value = (String) event.getValue();
        if("ALL".equalsIgnoreCase(value)){
            grid.setItems(logs);
        }else{
            List<NetSuiteAudit> filtered = logs.stream()
                    .filter(log -> log.getStatus().equalsIgnoreCase(value))
                    .collect(Collectors.toList());
            grid.setItems(filtered);
        }


    }

    private Icon getIcon(String status){

        Icon icon = null;
        switch(status.toUpperCase().trim()){
            case "SUCCESS" :{
                icon = VaadinIcon.SMILEY_O.create();
                icon.setColor("green");
                break;
            }
            case "FAILED"  :{
                icon = VaadinIcon.FROWN_O.create();
                icon.setColor("red");
                break;
            }
            case "SKIPPED" :{
                icon = VaadinIcon.MEH_O.create();
                icon.setColor("yellow");
                break;
            }
            case "FORCED_SUCCESS" :{
                icon = VaadinIcon.QUESTION_CIRCLE_O.create();
                icon.setColor("orange");
                break;
            }
            default: {
                icon = VaadinIcon.AMBULANCE.create();
                icon.setColor("white");
            }
        }
        return icon;

    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

        logs.addAll(service.getLogs(LocalDate.now()));
        grid.setItems(logs);
    }
}
