package com.ktech.calendar.entities;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.*;

@Entity
@Table(name="STATUS_CALENDAR")
@Getter
@Setter
public class CalendarType {

    @Id
    @Column(name="ID")
    private Long id;

    @Column(name="NAME")
    private String name;

    @Column(name="HEX_COLOR")
    private String hex;

    @Column(name="ACTIVELY_REVIEWED")
    private Integer isReviewed;

    @Column(name="SHARED_WITH_EXPERT")
    private Integer isShared;

    @Column(name="CLIO_ID")
    private Long clioId;

    public String getPrettyName(){
        return getName().substring(2);
    }

    public boolean isReviewed(){

        return BooleanUtils.toBoolean(isReviewed);

    }

    public boolean isShared(){

        return BooleanUtils.toBoolean(isShared);
    }


}
