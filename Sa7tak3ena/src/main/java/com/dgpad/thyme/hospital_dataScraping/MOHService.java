package com.dgpad.thyme.hospital_dataScraping;

import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.UserComplements.AddressService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MOHService {
    private static PasswordEncoder passwordEncoder;
    private static AddressService addressService;
    private static HospitalService hospitalService;


    private static final String BASE_LINK =  "https://www.moph.gov.lb/HealthFacilities/index/3/188/8/page:@@PAGE@@?&facility_type=8&district=&name=";
    private static final String MOH_LINK = "https://www.moph.gov.lb/";
    private static final String ADDRESS = "Address";
    private static final String PHONE = "Phone";
    private static final String FAX = "Fax ";
    private static final String EMAIL = "Email";
    private static final String DIRECTOR = "Director's name";//manager name
    private static final String REGION = "Caza";
    private static final String OWNERSHIP = "Ownership";
    private static final String CONTROLLING_DOCTOR = "Controlling Doctor"; //SupervisingPhysicianName
    private static final String CONTROLLING_DOCTOR_PHONE_NUMBER = "Controlling Doctor Phone Number";
    private static final String MOSOPH = "Member of syndicate of private hospitals";
    private static final String CONSTRUCTION_AUTH_NB = "Construction Authorization Nb";
    public static List<HospitalRecord> getRecords(){
        int i=1;
        List<HospitalRecord> hospitalRecords = new ArrayList<>();
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
                    hospitalRecords.add(new HospitalRecord(name,href));
                    System.out.println("Name: " + name);
                    System.out.println("Href: " + href);
                    System.out.println("------------------------------------------------");

                }
            }catch (Exception e){
                System.out.println("finished Scraping !!");
                break;
            }
        }
        return hospitalRecords;

    }

    public static Hospital getHospitalData(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document doc = connection.get();
        Hospital hospital = new Hospital();

        Element facilityContent = doc.selectFirst(".facilityContent");
        Elements facilityRowDetails = facilityContent.select(".facilityRowDetails");

        HashMap<String,String> information = new HashMap<>();
        for (Element row : facilityRowDetails) {
            String key = row.select(".infoTitle").text();
            String value = row.select(".infoContent").text();
            information.put(key,value);
        }

        // Extract information from HTML elements and set them in the Hospital object
        hospital.setPhone(information.getOrDefault(PHONE, null));
        hospital.setFax(information.getOrDefault(FAX, null));
        hospital.setEmail(information.getOrDefault(EMAIL, null));
        hospital.setManagerName(information.getOrDefault(DIRECTOR, null));
        hospital.setOWNERSHIP(information.getOrDefault(OWNERSHIP, null));
        hospital.setSupervisingPhysicianName(information.getOrDefault(CONTROLLING_DOCTOR, null));
        hospital.setSupervisingPhysicianPhone(information.getOrDefault(CONTROLLING_DOCTOR_PHONE_NUMBER, null));
        hospital.setMOSOPH(information.getOrDefault(MOSOPH, null));
        hospital.setCONSTRUCTION_AUTH_NB(information.getOrDefault(CONSTRUCTION_AUTH_NB, null));
//        processing address
        Address address =new Address();
        address.setName(information.getOrDefault(ADDRESS, null));
        address.setREGION(information.getOrDefault(REGION, null));
        hospital.setAddress(addressService.save(address));

//        process non  scraping data
        hospital.setAdministrator(true);
        hospital.setEnabled(false);
        hospital.setPassword(passwordEncoder.encode("123"));
        hospital.setRole(Role.HOSPITAL);
        hospital.setDeleted(false);

//        hospital.setPublicName();

        hospitalService.save(hospital);
        return hospital;
    }



    public static void main(String[] args) throws IOException {
        List<HospitalRecord> hospitalRecords = getRecords();
        for(HospitalRecord hospitalRecord : hospitalRecords){
            try{
                System.out.println(getHospitalData(hospitalRecord.detailsLink()));
            }catch (Exception e){
                System.out.println("\nError Fetching link : "+hospitalRecord.detailsLink()+" for hospital "+hospitalRecord.name()+"\n");
            }
        }
    }

}