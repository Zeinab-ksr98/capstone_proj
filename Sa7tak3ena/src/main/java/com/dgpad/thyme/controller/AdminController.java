package com.dgpad.thyme.controller;

import com.dgpad.thyme.Email.EmailService;
import com.dgpad.thyme.hospital_dataScraping.HospitalRecord;
import com.dgpad.thyme.hospital_dataScraping.MOHService;
import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Distracts;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.usercomplements.AccountRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.BedCategory;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.AccountRequestRepository;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.PatientService;
import com.dgpad.thyme.service.UserComplements.*;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;


@Controller
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceRequestService ambulanceRequestService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private MOHService mohService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AmbulanceAgencyService ambulanceAgencyService;
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private BedCategoryService bedCategoryService;
    @Autowired
    private BedCategoryRequestService bedCategoryRequestService;
    @Autowired
    private AccountRequestService accountRequestService;
    @Autowired
    private EmailService emailService;
    @GetMapping("/manage-users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageusers(Model model) {
        model.addAttribute("userrole", userService.getCurrentUser().getRole().toString());
        model.addAttribute("users", userService.getAllUsersNotBlocked());
        model.addAttribute("agencies", ambulanceAgencyService.getAllAgencies());
        return "Admin/manage-users";
    }
    @PostMapping("/edit-account")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String editUserProfile(@RequestParam("id") UUID id, @RequestParam("updatedName") String name, @RequestParam("updatedEmail") String email, @RequestParam("updatedPhone") String phone) {
        User user= userService.getUserById(id);
        user.setUsername(name);
        user.setEmail(email);
        user.setPhone(phone);
        userService.save(user);
        return "redirect:/manage-users";
    }
    @PostMapping("/send-mail")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String sendMail(@RequestParam("to") String to, @RequestParam("subject") String subject  ,@RequestParam("massage") String massage) throws IOException {
        emailService.sendEmail(to, subject,massage);
        return "redirect:/manage-users";
    }


    @PostMapping(value = "/admin-create-withRole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String createAdmin(@RequestParam("username") String userName,
                              @RequestParam("location") String location,
                              @RequestParam("region") Distracts region,
                              @RequestParam(value = "publicname", required = false) String publicName,
                              @RequestParam("email") String email,
                              @RequestParam("phone") String phone,
                              @RequestParam("role") Role role,
                              @RequestParam(value = "agency", required = false) AmbulanceAgency agency ,Model model) throws IOException {
        if (role == Role.AMBULANCE && (publicName == null || agency == null)) {
            model.addAttribute("error", "Public Name and Agency are required for Ambulance role.");
            return "error-page";
        }
        Role selectedRole = Role.valueOf(role.name());
        Address address =new Address();
        address.setREGION(region.toString());
        address.setName(location);
        address=addressService.save(address);
    if (selectedRole == Role.HOSPITAL) {
        Hospital hospital = new Hospital(userName, publicName, email, passwordEncoder.encode("123"), phone,true);
        hospital.setAddress(address);
        hospitalService.save(hospital);
        emailService.senddetailsEmail(userService.getUserById(hospital.id).email,1);

    }
    else if (selectedRole == Role.ADMIN) {
        User admin = new User(userName, email, passwordEncoder.encode("123"), phone,Role.ADMIN,false);
        userService.save(admin);
        emailService.senddetailsEmail(admin.email,1);
    }
    else if (selectedRole == Role.AMBULANCE) {
        Ambulance ambulance = new Ambulance(userName, publicName, email, passwordEncoder.encode("123"), phone,true);
        ambulance.setAgency(agency);
        ambulance.setAddress(address);
        ambulanceService.save(ambulance);
        emailService.senddetailsEmail(userService.getUserById(ambulance.getId()).email,1);
    }
    return "redirect:/manage-users";

}
    @PostMapping("/request-account")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String requesttoaccount(@RequestParam("id") int id,@RequestParam("agency") AmbulanceAgency agency ){
        try {
            AccountRequest accountRequest=accountRequestService.getRequestById(id);
            // Call the createAdmin method
            String result = createAdmin(accountRequest.getCompanyName(), accountRequest.getLocation(), accountRequest.getRegion(), accountRequest.getPublicName(), accountRequest.getEmail(), accountRequest.getPhone(), accountRequest.getType(), agency, null);
            // Handle the result if needed
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }
        return "redirect:/home";
    }

//agency
    @GetMapping("/manage-agency")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageAgencies(Model model) {
        model.addAttribute("userrole",userService.getCurrentUser().getRole().toString());
        model.addAttribute("agencies",ambulanceAgencyService.getAllAgencies());
        return "Admin/manage-agencies";
    }
    @PostMapping("/add-agency")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String addAgency(@RequestParam("agencyname") String agencyName) {
        AmbulanceAgency agency = new AmbulanceAgency();
        agency.setName(agencyName);
        ambulanceAgencyService.save(agency);
        return "redirect:/manage-agency";
    }
    @PostMapping("/update-agency")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String updateCategory(@RequestParam("id") int id, @RequestParam("updatedName") String name){

        AmbulanceAgency agency = ambulanceAgencyService.getAmbulanceAgencyId(id);
        agency.setName(name);
        ambulanceAgencyService.save(agency);
        return "redirect:/manage-agency";
    }

    //bed category management (by admin)
    @GetMapping("/manage-bedsCategory")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageCategory(Model model) {
        model.addAttribute("categories",bedCategoryService.getAllBedCategories());
        return "Admin/manage-bedcategory";
    }
    @PostMapping("/add-bedsCategory")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String addbedsCategory(@RequestParam("name") String name) {
        BedCategory bedcat = new BedCategory() ;
        bedcat.setName(name);
        bedCategoryService.save(bedcat);
        return "redirect:/manage-bedsCategory";
    }
    @PostMapping("/update-bedsCategory")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String updatebedsCategory(@RequestParam("id") Long id,@RequestParam("updatename") String name){
        BedCategory bedcat = bedCategoryService.getBedCategoryById(id);
        bedcat.setName(name);
        bedCategoryService.save(bedcat);
        return "redirect:/manage-bedsCategory";
    }
    @GetMapping("/delete-bedsCategory/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deletebedsCategory(@PathVariable Long id) {
        bedCategoryService.deleteBedCategory(id);
        return "redirect:/manage-bedsCategory";
    }
//   view requests
    @GetMapping("/Requests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageRequests(Model model) {
        //role cart distribution
        Role[] allRoles = Role.values();
        Map<Role, Double> rolePercentage = userService.calculateRolePercentage(allRoles);
        model.addAttribute("rolePercentage", rolePercentage);

// Feedback
        Map<String, Integer> feedbackCount = feedbackService.calculateFeedbackCounts();
        model.addAttribute("feedbackCount", feedbackCount);
        List<Integer> analytics = new ArrayList<>();
        analytics.add(reservationService.getAllReservations().size());
        analytics.add(ambulanceRequestService.findAllRequestbystatus(AmbulanceRequestStatus.ACCEPTED).size());
//        analytics.add(cu.getHospitalSections().size());
        model.addAttribute("analytics",analytics);

        model.addAttribute("requestedaccounts",accountRequestService.getAllRequest());
        model.addAttribute("requestedcategories",bedCategoryRequestService.getAllRequestBedCategories());
        return "Admin/requests";
    }
    //delete request of creating new category
    @GetMapping("/delete-CategoryRequest/{ID}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deleteRequestedBedCategory(@PathVariable int ID) {
        bedCategoryRequestService.deleteCat(ID);
        return "redirect:/Requests";
    }

    //    delete account creation request
    @GetMapping("/delete-AccountRequest/{ID}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deleteAccountRequested(@PathVariable int ID) {
        accountRequestService.deleteRequest(ID);
        return "redirect:/Requests";
    }

    @GetMapping("/manage-feeds")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageFeeds(Model model) {
        model.addAttribute("userrole",userService.getCurrentUser().getRole().toString());
        model.addAttribute("feeds",feedbackService.getAllFeeds());
        return "Admin/manage-feeds";
    }
    @GetMapping("/Admin/scrap")
    @PreAuthorize("hasAuthority('ADMIN')")

    public String scrap(Model model) {
        HashMap<String, HospitalRecord> hospitalRecords = mohService.getRecords();
        for (Map.Entry<String, HospitalRecord> entry: hospitalRecords.entrySet()) {
            String key = entry.getKey();
            HospitalRecord value = entry.getValue();

            try{
                System.out.println(mohService.getHospitalData(key,value.detailsLink()));
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\nError Fetching link : "+value.detailsLink()+" for hospital "+value.name()+"\n");
            }
        }
        return "redirect:/home";
    }

}