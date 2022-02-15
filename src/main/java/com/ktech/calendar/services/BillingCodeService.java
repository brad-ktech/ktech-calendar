package com.ktech.calendar.services;

import com.ktech.calendar.entities.MedicalExpert;
import com.ktech.starter.clio.apis.ClioApi;
import com.ktech.starter.dao.AutoDaoService;
import com.ktech.starter.entities.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


        Optional<List<MedicalExpert>> opt = dao.findAll(MedicalExpert.class);
        if(opt.isPresent()){
            mez = opt.get();
            mez.sort((MedicalExpert me1, MedicalExpert me2)->me1.getBillingCode().compareTo(me2.getBillingCode()));
        }
        return mez;

    }




}
