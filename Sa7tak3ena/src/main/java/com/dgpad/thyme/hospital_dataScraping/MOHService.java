package com.dgpad.thyme.hospital_dataScraping;

import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.UserComplements.AddressService;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Service
public class MOHService {
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  AddressService addressService;
    @Autowired
    private HospitalService hospitalService;


    private static final String BASE_LINK =  "https://www.moph.gov.lb/HealthFacilities/index/3/188/8/page:@@PAGE@@?&facility_type=8&district=&name=";
    private static final String MOH_LINK = "https://www.moph.gov.lb/";
    private static final String ADDRESS = "Address";
    private static final String PHONE = "Phone";
    private static final String FAX = "Fax ";
    private static final String EMAIL = "Email";
    private static final String DIRECTOR = "Director's name";//manager name
    private static final String REGION = "Caza";
    private static final String CONTROLLING_DOCTOR = "Controlling Doctor"; //SupervisingPhysicianName
    private static final String CONTROLLING_DOCTOR_PHONE_NUMBER = "Controlling Doctor Phone Number";
    private static final String CONSTRUCTION_AUTH_NB = "Construction Authorization Nb";
    public  HashMap<String,HospitalRecord> getRecords(){
        int i=1;
        HashMap<String,HospitalRecord> hospitalRecords = new HashMap<>();
        while(true){
            String link = BASE_LINK.replace("@@PAGE@@",Integer.toString(i++));
            try{
                Connection connection = Jsoup.connect(link);
                Document doc = connection.get();
                String html = doc.html();
                Document document = Jsoup.parse(html);
                Elements rows = document.select("table.table tbody tr");

                for (Element row : rows) {
                    Element nameCell = row.select("td:eq(0)").first();
                    String name = nameCell.text();
                    String href = MOH_LINK+nameCell.select("a").attr("href").replace("/ar/", "/en/");;
                    hospitalRecords.put(name, new HospitalRecord(name, href));
                    System.out.println("Name: " + name);
                    System.out.println("Href: " + href);
                    System.out.println("------------------------------------------------");

                }
            }

            catch (Exception e){
                System.out.println("finished Scraping !!");
                break;
            }
        }
        return hospitalRecords;

    }

    public Hospital getHospitalData(String name,String link) throws IOException {
        Hospital hospital = new Hospital();

        try{
        Connection connection = Jsoup.connect(link);
        Document doc = connection.get();

        Element facilityContent = doc.selectFirst(".facilityContent");
        Elements facilityRowDetails = facilityContent.select(".facilityRowDetails");

        HashMap<String,String> information = new HashMap<>();
        for (Element row : facilityRowDetails) {
            String key = row.select(".infoTitle").text();
            String value = row.select(".infoContent").text();
            information.put(key,value);
        }

        // Extract information from HTML elements and set them in the Hospital object
        hospital.setPhone(information.get(PHONE));
        hospital.setFax(information.getOrDefault(FAX,null));
        hospital.setEmail(information.getOrDefault(EMAIL,"_"));
        hospital.setManagerName(information.getOrDefault(DIRECTOR,null));
        hospital.setSupervisingPhysicianName(information.getOrDefault(CONTROLLING_DOCTOR, null));
        hospital.setSupervisingPhysicianPhone(information.getOrDefault(CONTROLLING_DOCTOR_PHONE_NUMBER,null));
        hospital.setCONSTRUCTION_AUTH_NB(information.getOrDefault(CONSTRUCTION_AUTH_NB,null));
////        processing address
        Address address =new Address();
        address.setName(information.getOrDefault(ADDRESS,null));
        address.setREGION(information.getOrDefault(REGION,null));
        address=addressService.save(address);
        hospital.setAddress(address);
////        process non  scraping data
        hospital.setAdministrator(true);
        hospital.setEnabled(false);
        hospital.setPassword(passwordEncoder.encode("123"));
        hospital.setRole(Role.HOSPITAL);
        hospital.setDeleted(false);

        hospital.setPublicName(name);
        hospital.setUsername(name);
        hospitalService.save(hospital);

        }
        catch (HttpStatusException e) {
            // Handle HTTP errors (e.g., 404 Not Found)
            System.err.println("HTTP error fetching URL: " + e.getUrl());

        }
        return hospital;
    }
}