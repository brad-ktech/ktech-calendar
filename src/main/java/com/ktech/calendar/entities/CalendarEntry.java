package com.ktech.calendar.entities;





import com.ktech.starter.entities.Matter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="STATUS_CALENDAR_ENTRY")
@Getter
@Setter
public class CalendarEntry {

    @Id
    @Column(name="CLIO_ID")
    private Long id;

    @Column(name="SUMMARY")
    private String title;

    @Column(name="LOCATION")
    private String location;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="START_AT")
    private LocalDateTime startAt;

    @Column(name="END_AT")
    private LocalDateTime endAt;

    @Column(name = "matter_id")
    private Long matter;

    @Column(name="CALENDAR_ID")
    private Long type;

    @Column(name="ALL_DAY")
    private Integer allDay;



    public Boolean isAllDay(){
        return BooleanUtils.toBoolean(allDay);
    }
}
