package com.ktech.calendar.services;


import com.amazonaws.util.IOUtils;
import com.ktech.calendar.entities.CalendarEntryAttendee;
import com.ktech.calendar.entities.CalendarType;
import com.ktech.calendar.entities.MedicalExpert;
import com.ktech.starter.clio.apis.CalendarAPI;
import com.ktech.starter.clio.models.CalendarEntry;
import com.ktech.starter.dao.AutoDaoService;
import com.ktech.starter.dao.QueryParameters;
import com.ktech.starter.entities.Matter;
import com.ktech.starter.enums.QueryComparatorEnum;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private AutoDaoService dao;
    private RestTemplate rest;
    private CalendarAPI api;

    @Value("${report.url}")
    private String subjectUrl;

    @Value("${report.scheme}")
    private String scheme;

    @Value("${report.host}")
    private String host;


    private static List<CalendarType> types;


    public CalendarService(@Autowired AutoDaoService dao, @Autowired RestTemplate rest, @Autowired CalendarAPI api){

        this.rest = rest;
        this.dao = dao;
        this.api = api;
        Optional<List<CalendarType>> opt = this.dao.findAll(CalendarType.class);
        if(opt.isPresent()){
            types = opt.get();


        }

    }

    public Optional<MedicalExpert> getMedicalExpertFromContactId(String id){



        Optional<MedicalExpert> opt = dao.find(MedicalExpert.class, new QueryParameters("contactId", Long.parseLong(id)));
        if(!opt.isPresent()){

            opt = dao.find(MedicalExpert.class, new QueryParameters("pseudoContactId", Long.parseLong(id)));
        }

        return opt;
    }

    public List<com.ktech.calendar.entities.CalendarEntry> getCalendarEntriesForMedicalExpert(MedicalExpert me){


        List<com.ktech.calendar.entities.CalendarEntry> entries = new ArrayList<>();
        Optional<List<CalendarEntryAttendee>> opt =  dao.findByParameters(CalendarEntryAttendee.class,
                                                                          new QueryParameters("calendarId", me.getCalendarId(), QueryComparatorEnum.EQ));

        if(opt.isPresent()){

            List<Long> idz = opt.get().stream()
                                      .map(CalendarEntryAttendee::getEntryId)
                                      .collect(Collectors.toList());

            QueryParameters qp = new QueryParameters();
            qp.put("id", idz, QueryComparatorEnum.IN);
            Optional<List<com.ktech.calendar.entities.CalendarEntry>> optEntries = dao.findByParameters(com.ktech.calendar.entities.CalendarEntry.class, qp);
            if(optEntries.isPresent()){
               entries.addAll(optEntries.get().stream()
                               .filter(ca -> ca.getStartAt().isAfter(LocalDateTime.now().minusYears(2)))
                               .filter(ca ->{
                                       CalendarType ct = getType(ca.getType()).get();
                                       return (ct.isReviewed() && ct.isShared());
                               }).collect(Collectors.toList()));


            }


        }

            return entries;
    }

    public Optional<CalendarType> getType(Long id){

        return types.stream().filter(t -> id.equals(t.getId())).findFirst();

    }


    public Optional<Matter> getMatterForEntry(Long matterId){

        return dao.find(Matter.class, matterId);


    }

    public byte[] download(long offset, Long contactId) throws URISyntaxException {

        byte[] bytes = null;
        try {
            URIBuilder builder = new URIBuilder();
            URI uri = builder.setScheme(scheme)
                    .setHost(host)
                    .setParameter("type", "EXPERT")
                    .setParameter("month", Long.toString(offset))
                    .setParameter("subject_url", subjectUrl+contactId)
                    .build();
            InputStream is = new URL(uri.toString()).openStream();
            bytes = IOUtils.toByteArray(is);
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;



    }

    public List<CalendarType> getTypes(){

        return types.stream().filter(t -> {
            return (t.isShared() && t.isReviewed());
        }).collect(Collectors.toList());

    }


    public void saveCalendarEntry(CalendarEntry ce) throws URISyntaxException, IOException {


        api.saveCalendarEntry(ce);


    }


}
