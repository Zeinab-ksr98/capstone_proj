package com.dgpad.thyme.controller;

import com.dgpad.thyme.Email.EmailService;
import com.dgpad.thyme.model.enums.*;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.*;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.UserComplements.*;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AmbulanceController {
    @Autowired
    private AStaffService aStaffService;
    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceService ambulanceService;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private AddressService addressService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AmbulanceRequestService ambulanceRequestService;
    private final AmbulanceCarService ambulanceCarService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    public AmbulanceController(AmbulanceCarService ambulanceCarService) {
        this.ambulanceCarService = ambulanceCarService;
    }
    @GetMapping(value = "/manage-staff")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String hospitalSectionManagement(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("user",cu);
        model.addAttribute("staff",cu.getParamedicList() );
        return "ambulanceAgency/manage-staff";
    }

    @PostMapping("/add-staff")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addStaff(@RequestParam("name") String name,@RequestParam("image") String image) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Paramedic staff = new Paramedic() ;
        staff.setName(name);
        staff.setAmbulance(cu);
        staff.setImage(image);
        aStaffService.save(staff);
        return "redirect:/manage-staff";
    }

    @PostMapping("/update-staff")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateStaff(@RequestParam("id") Long id, @RequestParam("updatedname") String name){
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Paramedic staff = aStaffService.getStaffById(id);
        staff.setName(name);
        staff.setAmbulance(cu);
        aStaffService.save(staff);
        return "redirect:/manage-staff";
    }

    @GetMapping("/delete-staff/{id}")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String deleteStaff(@PathVariable Long id) {
        aStaffService.deleteStaff(id);
        return "redirect:/manage-staff";
    }
    @GetMapping("/manage-car")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageCar(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("user",cu );

        model.addAttribute("cars", cu.getAmbulanceCars());
        return "ambulanceAgency/manage-car";
    }

    @PostMapping("/add-car")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addCar(@RequestParam("cartype") Ambulancetypes cartype, @RequestParam("num") int num) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        for (int i = 0; i < num ; i++) {
            AmbulanceCar car = new AmbulanceCar();
            car.setStatus(AmbulanceStatus.Available);
            car.setType(cartype);
            car.setAmbulance(cu);
            ambulanceCarService.save(car);
        }
        return "redirect:/manage-car";
    }
    @GetMapping("/update-car")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateCar(@RequestParam("id") int id, @RequestParam("updatedStatus") AmbulanceStatus status) {
        ambulanceCarService.updateStatus(id,status);
        return "redirect:/manage-car";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String deleteCar(@PathVariable int id) {
        ambulanceCarService.deleteCar(id);
        return "redirect:/manage-car";
    }
    @GetMapping("/new-requests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String request(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        List<Integer> analytics = new ArrayList<>();
        analytics.add(cu.getAmbulanceRequests().size());
        analytics.add(ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available, cu.id).size());
        analytics.add(cu.getParamedicList().size());
        model.addAttribute("user",cu);
        model.addAttribute("requests", ambulanceRequestService.getAllRequestsForUser(cu.id));
        model.addAttribute("ambulances", ambulanceService.getcompletedAmbulanceByAgency(cu.getAgency()));
        model.addAttribute("analytics",analytics);
        model.addAttribute("admin", cu.isAdministrator());

        return "ambulanceAgency/home";
    }

    //all new request in home
    @GetMapping("/acceptRequest")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String acceptRequest(@RequestParam("id") long id) {
        AmbulanceRequest r =ambulanceRequestService.getRequestById(id);
        r.setStatus(AmbulanceRequestStatus.ACCEPTED);
        ambulanceRequestService.save(r);

        for (AmbulanceRequest AR : ambulanceRequestService.findAllAmbulanceRequestsForUserByStatus(r.getSender().id, AmbulanceRequestStatus.PENDING)) {
            if (AR !=r) {
                AR.setStatus(AmbulanceRequestStatus.Deleted);
                ambulanceRequestService.save(AR);
            }
        }
        return "redirect:/home";
    }
    @GetMapping("/rejectRequest")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String rejectRequest(@RequestParam("id") long id) {
        AmbulanceRequest r =ambulanceRequestService.getRequestById(id);
        r.setStatus(AmbulanceRequestStatus.REJECTED);
        ambulanceRequestService.save(r);
        return "redirect:/home";
    }
    @GetMapping("/manage-requests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageRequests(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("newrequest",new AmbulanceRequest());
        model.addAttribute("stafflist",cu.getParamedicList() );
        model.addAttribute("requests", ambulanceRequestService.getAllAmbulanceRequestsForUserWithin24hs(userService.getCurrentUser().getId()));
        List<Hospital> hospitals = hospitalService.findHospitalsWithAvailableEmergencyBeds("طوارئ");
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("user",cu );

        model.addAttribute("cars", ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available,cu.id));
        return "ambulanceAgency/Requests";
    }
    @GetMapping("/manage-historyRequests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageHistoryRequests(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("newrequest",new AmbulanceRequest());
        model.addAttribute("stafflist",cu.getParamedicList() );
        model.addAttribute("requests", ambulanceRequestService.getAllAmbulanceRequestsForUserOutside24hs(userService.getCurrentUser().getId()));
        List<Hospital> hospitals = hospitalService.findHospitalsWithAvailableEmergencyBeds("طوارئ");
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("user",cu );

        model.addAttribute("cars", ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available,cu.id));
        return "ambulanceAgency/Requests";
    }
    @GetMapping("/manage-nonAppedrequests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageNonAppedRequests(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("newrequest",new AmbulanceRequest());
        model.addAttribute("stafflist",cu.getParamedicList() );
        model.addAttribute("requests", ambulanceRequestService.getAllRequestsForUser(userService.getCurrentUser().getId()));
        List<Hospital> hospitals = hospitalService.findHospitalsWithAvailableEmergencyBeds("طوارئ");
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("user",cu );

        model.addAttribute("cars", ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available,cu.id));
        return "ambulanceAgency/Non Apped Requests";
    }
    @PostMapping("/add-request")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addRequest( @ModelAttribute("newrequest") AmbulanceRequest newrequest) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        newrequest.setAmbulance(cu);
        newrequest.setSender(cu);
        newrequest.setStatus(AmbulanceRequestStatus.ACCEPTED);
        newrequest.setCreatedAt(LocalDateTime.now());
        if (newrequest.getService().equals(Ambulanceservice.inDoorService)) {
            // Set the to and from fields to null
            newrequest.setTo(null);
            newrequest.setPickupaddress(null);
        }
        else {
            if (newrequest.to != null && !newrequest.to.getName().isEmpty())
                newrequest.to = addressService.save(newrequest.to);
            else
                newrequest.setTo(null);
            if (newrequest.pickupaddress != null && !newrequest.pickupaddress.getName().isEmpty())
                newrequest.pickupaddress = addressService.save(newrequest.pickupaddress);
            else
                newrequest.setPickupaddress(null);
        }
        ambulanceRequestService.save(newrequest);
        return "redirect:/manage-nonAppedrequests";
    }
    @GetMapping("/update-requestdetails")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateRequestDetails(
            @RequestParam("id") long id,
            @RequestParam("stype") Ambulanceservice survice,
            @RequestParam("car") AmbulanceCar car,
            @RequestParam("nstaff") Paramedic paramedic,
            @RequestParam("newdescription") String description,
            @RequestParam("equipment") String equipment) {
        ambulanceRequestService.update(id, survice, car.getType(), paramedic, description, equipment);
        ambulanceCarService.updateStatus(car.getId(), AmbulanceStatus.Reserved);

        return "redirect:/manage-nonAppedrequests";
    }
    @GetMapping("/update-nonappedrequestdetails")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateRequestDetails(
            @RequestParam("id") long id,
            @RequestParam("newdescription") String description,
            @RequestParam("equipment") String equipment) {
        ambulanceRequestService.updateNonapped(id,description, equipment);
        return "redirect:/manage-requests";
    }
    @GetMapping("/update-hrequestdetails")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateHRequestDetails(
            @RequestParam("id") long id,
            @RequestParam("car") AmbulanceCar car,
            @RequestParam("paramedic") Paramedic paramedic,
            @RequestParam("equipment") String equipment) {
        ambulanceRequestService.update(id, paramedic, equipment);
        ambulanceCarService.updateStatus(car.getId(), AmbulanceStatus.Reserved);

        return "redirect:/manage-requests";
    }
    @GetMapping("/manage-branch")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageBranch(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        List<Integer> analytics = new ArrayList<>();
        analytics.add(cu.getAmbulanceRequests().size());
        analytics.add(ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available, cu.id).size());
        analytics.add(cu.getParamedicList().size());
        model.addAttribute("user", cu);

        model.addAttribute("requests", ambulanceRequestService.getAllRequestsForUser(cu.id));
        model.addAttribute("ambulances", ambulanceService.getAmbulanceByAgency(cu.getAgency()));
        model.addAttribute("analytics",analytics);

        return "ambulanceAgency/manageBranch";
    }
    @PostMapping(value = "/create-ambulance")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String CreateSubAmbulance(@RequestParam("username") String userName,@RequestParam("publicname") String publicName,  @RequestParam("email") String email, @RequestParam("phone") String phone) throws IOException {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Ambulance ambulance = new Ambulance(userName, publicName, email, passwordEncoder.encode("123"), phone,false);
        ambulance.setAgency(cu.getAgency());
        ambulanceService.save(ambulance);
        emailService.senddetailsEmail(email,1);

        return "redirect:/manage-branch";
    }
    @PostMapping("/emergency-reservation")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String reserveByAmbulance(
            @RequestParam("rid") long id,
            @RequestParam("hospital") Hospital hospital,
            @RequestParam("car") AmbulanceCar car,
            @RequestParam("emergencystaff") Paramedic paramedic,
            @RequestParam("emergencydescription") String description,
            @RequestParam("emergencyequipment") String equipment
    ) {
        //description is only changed by update details
        AmbulanceRequest r= ambulanceRequestService.update(id,Ambulanceservice.transfer,car.getType(),paramedic,ambulanceRequestService.getRequestById(id).getDescription(),equipment);
        r.setHospital(hospital);
        r.setTo(hospital.getAddress());
        ambulanceRequestService.save(r);
        reservationService.reserveEmergency(hospital,car,description);
        ambulanceCarService.updateStatus(car.getId(), AmbulanceStatus.Reserved);
        return "redirect:/manage-requests";
    }


}