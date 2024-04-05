package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Ambulanceservice;
import com.dgpad.thyme.model.enums.Role;
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
//    @PostMapping("/ambulance_detailrequest")
//    @PreAuthorize("hasAnyAuthority('PATIENT')")
//    public String submitDetailedRequest(@RequestParam("description") String regin,@RequestParam("name") String name,@RequestParam("phone") String phone,@RequestParam("description") String description,@RequestParam("location") String location,  Model model) {
//        User cu =userService.getCurrentUser();
//        Address address =new Address();
//        address.setName(location);
//        address.setREGION(regin);
//        address= addressService.save(address);
////        dispatch according to availablity (enabled ones) and same region
//        List<Ambulance> filteredAmbulances= addressService.sortAmbulancesByDistance(address,ambulanceservice.getAllCompletedAmbulances());
//        for (int i = 0; i < 2; i++) {
//            Ambulance A = filteredAmbulances.get(i);
//            AmbulanceRequest AR =new AmbulanceRequest();
//            AR.setAmbulance(A);
//            if (cu.getRole() == Role.PATIENT){
//                AR.setService(Ambulanceservice.homeService);
//
//            }
//            else {
//                AR.setService(Ambulanceservice.transfer);
//                AR.setTo(hospitalService.getHospitalById(cu.id).getAddress());
//            }
//            AR.setStatus(AmbulanceRequestStatus.PENDING);
//            AR.setPickupaddress(address);
//            AR.setDescription(description);
//            AR.setSender(userService.getCurrentUser());
//            AR.setCreatedAt(LocalDateTime.now());
//            ambulanceRequestService.save(AR);
//        }
//        model.addAttribute("alertMessage", "Request sent successfully!");
//        return "redirect:/home";
//    }
    //it works but no need for it cause the main and the branch are not connected --> no main that monitor
//    @PostMapping(value = "/create-hospital")
//    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
//    public String CreateBranch(@RequestParam("username") String userName, @RequestParam("publicname") String publicName, @RequestParam("email") String email, @RequestParam("phone") String phone) {
//        Hospital hospital = new Hospital(userName, publicName, email, passwordEncoder.encode("123"), phone,false);
//        hospitalService.save(hospital);
//        return "redirect:/home";
//    }
}
