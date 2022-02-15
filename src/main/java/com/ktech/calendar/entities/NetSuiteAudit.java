package com.ktech.calendar.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="NETSUITE_AUDIT")
@Getter
@Setter
public class NetSuiteAudit {


    @Id
    @Column(name="ID")
    private Long id;

    @Column(name="CLIO_ID")
    private Long clioId;

    @Column(name="NETSUITE_ID")
    private Long netsuiteId;

    @Column(name="ENTITY_TYPE")
    private String type;

    @Column(name="STATUS")
    private String status;

    @Column(name="SOURCE")
    private String source;

    @Column(name="LOGGED_AT")
    private LocalDateTime logTime;

    public String getPrettyTime(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY HH:mm a");
        return formatter.format(getLogTime());
    }


}
