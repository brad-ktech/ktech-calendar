package com.ktech.calendar.services;


import com.ktech.calendar.entities.NetSuiteAudit;
import com.ktech.starter.dao.AutoDaoService;
import com.ktech.starter.dao.QueryParameters;
import com.ktech.starter.enums.QueryComparatorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NetSuiteAuditLogService {


    private AutoDaoService dao;

    public NetSuiteAuditLogService(@Autowired AutoDaoService dao){

        this.dao = dao;
    }

    public void retry(NetSuiteAudit nsa){




    }


    public List<NetSuiteAudit> getLogs(LocalDate date){



        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(11,59, 59, 59);

        QueryParameters qp = new QueryParameters();
        qp.put("logTime", startTime, QueryComparatorEnum.GTE);
        qp.put("logTime",  endTime, QueryComparatorEnum.LTE);
        qp.setOrderByColumns("logTime");
        Optional<List<NetSuiteAudit>> opt = dao.findByParameters(NetSuiteAudit.class, qp);
        List<NetSuiteAudit> results = new ArrayList<>();
        if(opt.isPresent()){
            results.addAll(opt.get());
        }
        Collections.reverse(results);
        return results;

    }
}
