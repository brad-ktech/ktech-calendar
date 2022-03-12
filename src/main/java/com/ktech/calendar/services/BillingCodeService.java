package com.ktech.calendar.services;

import com.ktech.calendar.entities.MedicalExpert;
import com.ktech.starter.clio.apis.ClioApi;
import com.ktech.starter.dao.AutoDaoService;
import com.ktech.starter.dao.QueryParameters;
import com.ktech.starter.entities.Contact;
import com.ktech.starter.enums.QueryComparatorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BillingCodeService {

    private AutoDaoService dao;
    private ClioApi api;

    public BillingCodeService(@Autowired AutoDaoService dao, @Autowired ClioApi api){

        this.api = api;
        this.dao = dao;
    }


    public List<MedicalExpert> getAllMedicalExperts(){

        List<MedicalExpert> mez = new ArrayList<>();

        QueryParameters qp = new QueryParameters();
        qp.put("billingCode", null, QueryComparatorEnum.NOT_NULL);
        qp.setOrderByColumns("billingCode");



        Optional<List<MedicalExpert>> opt = dao.findByParameters(MedicalExpert.class, qp);
        if(opt.isPresent()){
            mez = opt.get();


        }
        return mez;

    }




}
