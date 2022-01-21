package com.ktech.calendar.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Entity
@Table(name="MEDICAL_EXPERT")
@Getter
@Setter
public class MedicalExpert {

    @Id
    @Column(name="ID")
    private Long id;

    @Column(name="EMAIL")
    @Email
    private String email;

    @Column(name="FIRST_NAME")
    private String firstName;

    @Column(name="LAST_NAME")
    private String lastName;

    @Column(name="BILLING_CODE")
    private String billingCode;

    @Column(name="LOCATION")
    private String location;

    @Column(name="SPECIALTY")
    private String specialty;

    @Column(name="PHONE")
    private String phone;

    @Column(name="CALENDAR_ID")
    private Long calendarId;

    @Column(name="PSEUDO_CONTACT_ID")
    private Long pseudoContactId;

    @Column(name="CLIO_CONTACT_ID")
    private Long contactId;

}
