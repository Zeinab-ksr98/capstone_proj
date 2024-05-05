package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.enums.*;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.RequestBedCategory;
import com.dgpad.thyme.model.usercomplements.*;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.UserComplements.*;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HospitalController {

    @Autowired
    private HSectionsService hSectionsService;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private UserService userService;
    @Autowired
    private BedsService bedsService;
    @Autowired
    private BedCategoryService bedCategoryService;
    @Autowired
    private BedCategoryRequestService bedCategoryRequestService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private AmbulanceService ambulanceservice;
    @Autowired
    private AmbulanceRequestService ambulanceRequestService;

    @GetMapping(value = "/manage-sections")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String hospitalSectionManagement(Model model) {
        User currentuser=userService.getCurrentUser();
        model.addAttribute("user",currentuser );

        model.addAttribute("hospitalSections",hospitalService.getHospitalById(currentuser.getId()).getHospitalSections() );
        return "hospital/manage-hsection";
    }
    @PostMapping("/add-hSection")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String addSection(@RequestParam("name") String name) {
        HSections section = new HSections() ;
        section.setName(name);
        User currentuser=userService.getCurrentUser();
        section.setHospital(hospitalService.getHospitalById(currentuser.getId()));
        hSectionsService.save(section);
        return "redirect:/manage-sections";
    }

    @PostMapping("/update-hSection")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String updateSection(@RequestParam("id") Long id, @RequestParam("updatedname") String name){
        HSections section = hSectionsService.getHSectionById(id);
        section.setName(name);
        User currentuser=userService.getCurrentUser();
        section.setHospital(hospitalService.getHospitalById(currentuser.getId()));
        hSectionsService.save(section);
        return "redirect:/manage-sections";
    }

    @GetMapping("/delete-section/{id}")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String deleteSection(@PathVariable Long id) {
        hSectionsService.deleteSection(id);
        return "redirect:/manage-sections";
    }

    //manage available beds
    @GetMapping(value = "/manage-beds")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String bedsManagement(Model model) {
        User currentuser=userService.getCurrentUser();
        model.addAttribute("user",currentuser );
        model.addAttribute("hospitalbeds",hospitalService.getHospitalById(currentuser.getId()).getAvailableBeds() );
        model.addAttribute("bedCategories",bedCategoryService.getAllBedCategories() );
        return "hospital/manage-beds";
    }
    @PostMapping("/add-bed")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String addAvailableBeds(@RequestParam("nbbed") int numbed,@RequestParam("bedcat") BedCategory bedcat) {
        User currentuser=userService.getCurrentUser();
        Hospital h= hospitalService.getHospitalById(currentuser.getId());
        Beds bed = new Beds() ;
        bed.setNbBeds(numbed);
        bed.setCategory(bedcat);
        bedsService.save(bed);
        h.getAvailableBeds().add(bed);
        h.setAvailableBeds(h.getAvailableBeds());
        hospitalService.save(h);
        return "redirect:/manage-beds";
    }
    @PostMapping("/update-bed")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String updateVacancyBed(@RequestParam("id") int id,@RequestParam("nbbed") int nbbed){
        Beds bed = bedsService.getBedById(id);
        bed.setCategory(bed.getCategory());
        bed.setNbBeds(nbbed);
        bedsService.save(bed);
        return "redirect:/manage-beds";
    }
    @GetMapping("/delete-bed/{ID}")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String deleteAvailableBed(@PathVariable int ID) {
        User currentuser=userService.getCurrentUser();
        Hospital h= hospitalService.getHospitalById(currentuser.getId());
        h.getAvailableBeds().remove(bedsService.getBedById(ID));
        userService.save(h);
        return "redirect:/manage-beds";
    }
    @PostMapping("/Request-bedCategory")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String requestBedCategory(@RequestParam("bedCategory") String bedCategory) {
        RequestBedCategory requested=new RequestBedCategory();
        requested.setRequestedCategory(bedCategory);
        bedCategoryRequestService.save(requested);
        return "redirect:/manage-beds";
    }
    @PostMapping("/hospital_detailambulancerequest")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String submitDetailedRequest(@RequestParam("toH") boolean toH, @RequestParam("region") Distracts regin,@RequestParam("address") String location, @RequestParam("Ambulancetype") Ambulancetypes Ambulancetype, @RequestParam("name") String name, @RequestParam("phone") String phone, @RequestParam("description") String description,  Model model) throws IOException {
        Hospital cu =hospitalService.getHospitalById(userService.getCurrentUser().id);
        String Region;
        if (toH)
            Region = regin.toString();
        else
            Region=cu.getAddress().getREGION();

        if (!ambulanceservice.getAllCompletedAmbulancesinRegion(Region).isEmpty()){
            Address address =new Address();
            address.setName(location);
            address.setREGION(regin.toString());
            address= addressService.save(address);
    //        dispatch according to availablity (enabled ones) and same region
    //        address can be eather from or to
            description=name +" "+phone+"\n"+description ;
            ambulanceRequestService.hospitalrequestingAmbulance(address,Region,toH,Ambulancetype,description);
            model.addAttribute("alertMessage", "Request sent successfully!");
            return "redirect:/home";

        }
        else{
            model.addAttribute("alertMessage", "ambulances are out of service near this region!");
            return "account/regionerror";
        }

    }
    //it works but no need for it cause the main and the branch are not connected --> no main that monitor
//    @PostMapping(value = "/create-hospital")
//    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
//    public String CreateBranch(@RequestParam("username") String userName, @RequestParam("publicname") String publicName, @RequestParam("email") String email, @RequestParam("phone") String phone) {
//        Hospital hospital = new Hospital(userName, publicName, email, passwordEncoder.encode("123"), phone,false);
//        hospitalService.save(hospital);
//        return "redirect:/home";
//    }
}
