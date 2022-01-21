package com.ktech.calendar.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="STATUS_CALENDAR_ENTRY_ATTENDEE")
@Getter
@Setter
public class CalendarEntryAttendee {


    @Id
    @Column(name="STATUS_CALENDAR_ENTRY_ID")
    private Long entryId;


    @Column(name="ATTENDEE_CLIO_ID")
    private Long calendarId;
/*
    @Column(name="ATTENDEE_TYPE")
    private String type;
*/


}
