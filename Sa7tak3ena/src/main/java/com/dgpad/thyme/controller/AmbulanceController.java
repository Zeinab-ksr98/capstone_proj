package com.dgpad.thyme.controller;

import com.dgpad.thyme.Email.EmailService;
import com.dgpad.thyme.Whatsapp.VerificationSender;
import com.dgpad.thyme.model.Image;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AmbulanceController {
    @Autowired
    private AStaffService aStaffService;
    @Autowired
    private EquipmentsService equipmentsService;
    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private VerificationSender verificationSender;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ImageService imageService;

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
    public String staffManagement(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("user",cu);
        model.addAttribute("staff",cu.getParamedicList() );
        return "ambulanceAgency/manage-staff";
    }

    @PostMapping("/add-staff")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addStaff(@RequestParam("name") String name, @RequestParam("image") List<MultipartFile> imageFile, @RequestParam("phone") String phone) throws IOException {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Paramedic staff = new Paramedic() ;
        staff.setName(name);
        staff.setAmbulance(cu);
        staff.setPhone(phone);
        List<Image> images = imageService.saveImages(imageFile);
        staff.setImage(images.get(0));
        aStaffService.save(staff);
        return "redirect:/manage-staff";
    }

    @PostMapping("/update-staff")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateStaff(@RequestParam("id") Long id, @RequestParam("updatedname") String name ,@RequestParam("image") List<MultipartFile> imageFile,@RequestParam("updatedphone") String phone) throws IOException {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Paramedic staff = aStaffService.getStaffById(id);
        staff.setName(name);
        staff.setPhone(phone);
        staff.setAmbulance(cu);
        List<Image> images = imageService.saveImages(imageFile);
        staff.setImage(images.get(0));
        aStaffService.save(staff);
        return "redirect:/manage-staff";
    }

    @GetMapping("/swichactive-staff/{id}")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String deleteStaff(@PathVariable Long id) {
        aStaffService.swichactiveStaff(id);
        return "redirect:/manage-staff";
    }
    @GetMapping(value = "/manage-equipments")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String equipmentsManagement(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("user",cu);
        model.addAttribute("equipments",ambulanceService.getEquipmentsWithinMonthForAmbulance(cu) );
        model.addAttribute("monthlyequipments", ambulanceService.getTotalEquipmentPricesWithinMonthForAmbulance(cu));

        return "ambulanceAgency/manage-equipments";
    }


    @PostMapping("/add-equipments")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addequipments(@RequestParam("name") String name,@RequestParam("price") int price) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Equipments equipment = new Equipments() ;
        equipment.setName(name);
        equipment.setPrice(price);
        equipment.setAmbulance(cu);
        equipment.setCreatedAt(LocalDateTime.now());
        equipmentsService.save(equipment);
        return "redirect:/manage-equipments";
    }

    @PostMapping("/update-equipments")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateEquipments(@RequestParam("id") int id, @RequestParam("updatedname") String name ,@RequestParam("updatedprice") int price){
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        Equipments equipments = equipmentsService.getEquipmentById(id);
        equipments.setName(name);
        equipments.setPrice(price);
        equipments.setAmbulance(cu);
        equipmentsService.save(equipments);
        return "redirect:/manage-staff";
    }
    @GetMapping("/delete-equipments/{id}")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String deleteEquipments(@PathVariable int id) {
        equipmentsService.deleteEquipment(id);
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
        analytics.add(ambulanceRequestService.getAmbulanceRankForCurrentUserInCurrentMonth(cu.id));
        analytics.add(ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available, cu.id).size());
        analytics.add(cu.getParamedicList().size());
        model.addAttribute("user",cu);
        model.addAttribute("requests", ambulanceRequestService.findAllAmbulanceRequestsForUserByStatus(cu.id, AmbulanceRequestStatus.PENDING));
        model.addAttribute("ambulances", ambulanceService.getcompletedAmbulanceByAgency(cu.getAgency()));
        model.addAttribute("analytics",analytics);
        model.addAttribute("admin", cu.isAdministrator());

        model.addAttribute("equipments", ambulanceService.getTotalEquipmentPricesWithinMonthForAmbulance(cu));
        Map<Month,Integer> requestsPerMonth = ambulanceRequestService.getRequestsPerMonth(cu.getId());
        Map<Month,Integer> acceptedrequestsPerMonth = ambulanceRequestService.getAcceptedRequestsPerMonth(cu.getId());
        Map<Month,Integer> patientsPerMonth = ambulanceRequestService.getPatientsPerMonth(cu.getId());
        Map<Month, Paramedic> topParamedicPerMonth = ambulanceRequestService.findTopParamedicWithMostRequestsForEachMonthForAmbulance(cu.getId());
        Month currentMonth = LocalDateTime.now().getMonth();
        Paramedic topParamedicForCurrentMonth = topParamedicPerMonth.get(currentMonth);

        // Add additional data to the model
        model.addAttribute("topParamedicForCurrentMonth", topParamedicForCurrentMonth);
        model.addAttribute("requestsPerMonth", requestsPerMonth);
        model.addAttribute("acceptedrequestsPerMonth", acceptedrequestsPerMonth);

        model.addAttribute("patientsPerMonth", patientsPerMonth);

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
                verificationSender.sendVerificationCode(AR.getSender().getPhone(),
                        "Your ambulance request has been accepted and it is on its way to you. Please stay safe and await its arrival."
                );

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
        model.addAttribute("requests", ambulanceRequestService.findRequestsWithin24Hours(userService.getCurrentUser().getId()));
        List<Hospital> hospitals = hospitalService.findHospitalsWithAvailableBedCategory("طوارئ");
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
        List<Hospital> hospitals = hospitalService.findHospitalsWithAvailableBedCategory("طوارئ");
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("user",cu );

        model.addAttribute("cars", ambulanceCarService.findAllUserCarByStatus(AmbulanceStatus.Available,cu.id));
        return "ambulanceAgency/HistoryRequests";
    }
    @GetMapping("/manage-nonAppedrequests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageNonAppedRequests(Model model) {
        Ambulance cu = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
        model.addAttribute("newrequest",new AmbulanceRequest());
        model.addAttribute("stafflist",cu.getParamedicList() );
        model.addAttribute("requests", ambulanceRequestService.getAllRequestsForUser(userService.getCurrentUser().getId()));
        List<Hospital> hospitals = hospitalService.findHospitalsWithAvailableBedCategory("طوارئ");
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
    ) throws IOException {
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