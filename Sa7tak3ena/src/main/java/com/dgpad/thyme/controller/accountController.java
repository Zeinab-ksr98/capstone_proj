package com.dgpad.thyme.controller;

import com.dgpad.thyme.hospital_dataScraping.HospitalRecord;
import com.dgpad.thyme.hospital_dataScraping.MOHService;
import com.dgpad.thyme.model.Image;
import com.dgpad.thyme.model.enums.*;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.AccountRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.Feedback;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.*;
import com.dgpad.thyme.service.UserComplements.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Controller
public class accountController {
    @Autowired
    private UserService userService;

    @Autowired
    private HSectionsService hSectionsService;

    @Autowired
    private AmbulanceRequestService ambulancerequestService;
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private PatientService patientService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AddressService addressService;

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private AccountRequestService accountRequestService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageService imageService;

    @GetMapping(value = "/Main")
    public String home(Model model) {
        model.addAttribute("account", new AccountRequest());
        return "landingPages/Main";
    }

    @PostMapping("/Request-Account")
    public String requestAccount(@ModelAttribute("account") AccountRequest accountRequest) {
        accountRequestService.save(accountRequest);
        return "redirect:/Main";
    }
    @GetMapping(value = "/feed")
    public String feed(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "account/feedback";
    }
    @PostMapping("/saveFeedback")
    public String saveFeedback(@RequestParam("feedbackRating") feedbackRating rating, @RequestParam("feedbackMessage") String message) {
        // Save the feedback using the feedback service
        User cu =userService.getCurrentUser();
        Feedback feedback = new Feedback();
        feedback.setRate(rating);
        feedback.setImprovement(message);
        feedback.setUser(cu);
        feedbackService.save(feedback);
        cu.setFeedback(feedback);
        userService.save(cu);

        return "redirect:/home";
    }
    @GetMapping(value = "/SignIn")
    public String SignIn(Model model) {
        model.addAttribute("newuser", new Patient());
        return "account/login_signup";
    }

    @GetMapping(value = "/home")
    public String displayHome(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("alertMessage", null);

        if (userService.getCurrentUser().getRole()== Role.ADMIN){

            return "redirect:/Requests";
        }

        else if (userService.getCurrentUser().getRole()== Role.PATIENT){
            List<AmbulanceRequest> requests = ambulancerequestService.findAllAmbulanceRequestsForUserByStatus(userService.getCurrentUser().id, AmbulanceRequestStatus.PENDING);
            model.addAttribute("user", patientService.getPatientById(userService.getCurrentUser().id));
            model.addAttribute("requests_size",requests.size());
            model.addAttribute("DetailsComplete",requestService.AreUserDetailsComplete());
            model.addAttribute("AmbulanceRequests",ambulancerequestService.getAllRequestsForUserWith24hours(userService.getCurrentUser().id) );
            List<Request> requests1= requestService.getAllRequestsForUserWithin24hrs(userService.getCurrentUser().id);
            for (Request r:requests1) {
                for (Image i: r.getDoctorReport()) {
                    i.setImage("");
                }
            }
            model.addAttribute("requests",requests1 );

            return "patient/Home";
        }
        else if (userService.getCurrentUser().getRole()== Role.HOSPITAL) {
            Hospital cu =hospitalService.getHospitalById(userService.getCurrentUser().getId());
            List<Integer> analytics = new ArrayList<>();
            analytics.add(requestService.getAllNewRequestsForUser(cu.id).size());
            analytics.add(cu.getReservations().size());
            analytics.add(cu.getHospitalSections().size());
            analytics.add(hospitalService.getTotalBeds(cu.getAvailableBeds()));
//            model.addAttribute("AmbulanceRequests",hospitalService.getAllForCustomerWithin24hs(cu.id ) );

            model.addAttribute("analytics",analytics);
            model.addAttribute("patientlist", hospitalService.findPatientsByHospital());

            model.addAttribute("genderStat", hospitalService.calculateGenderDistribution());
            model.addAttribute("agestat", hospitalService.calculateAgeDistribution());

            Map<ReservationType, Long> reservationTypeDistribution = reservationService.calculateReservationTypeDistribution();
            model.addAttribute("reservationTypeDistribution", reservationTypeDistribution);

            List<Hospital> hospitals = hospitalService.getAllHospitalsSortedByReservationSize();
            model.addAttribute("hospitals", hospitals);
            return "hospital/Home";
        }
        else if (userService.getCurrentUser().getRole()== Role.AMBULANCE) {
            return "redirect:/new-requests";
        }
        return"landingPages/Main";

    }

//    profile
    @GetMapping("/profile")
    public String userProfile(Model model) {

        // Get the current user
        User user = userService.getCurrentUser();
        switch (user.getRole()) {
            case ADMIN:{
                model.addAttribute("newuser", new User());
                model.addAttribute("user", userService.getUserById(userService.getCurrentUser().id));
                break;
            }
            case PATIENT:{
                model.addAttribute("newuser", new Patient());
                model.addAttribute("user", patientService.getPatientById(user.id));
                break;
            }
            case HOSPITAL:{
                model.addAttribute("newuser", new Hospital());
                model.addAttribute("user", hospitalService.getHospitalById(user.id));
                break;
            }
            default:{
                model.addAttribute("newuser", new Ambulance());
                model.addAttribute("user",ambulanceService.getAmbulanceById(user.id));
                break;
            }
        }

        return "account/Profile";
    }

    @GetMapping("/error_page")
    public String error(Model model) {

        User user = userService.getCurrentUser();
        if (user.getRole() == Role.PATIENT){
                return "account/error-profilenotcompleted_patient";
        }
        else {
            return "account/error-profilenotcompleted";

        }

    }

    @PostMapping("/profile-edit-ambulance")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String editUserProfile(@ModelAttribute("newuser") Ambulance user) {
        ambulanceService.update(ambulanceService.getAmbulanceById(userService.getCurrentUser().getId()),user);
        return "redirect:/profile";
    }
    @PostMapping("/profile-edit-hospital")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String editUserProfile(@ModelAttribute("newuser") Hospital user) {
        hospitalService.update(hospitalService.getHospitalById(userService.getCurrentUser().getId()),user);
        return "redirect:/profile";
    }
    @PostMapping("/profile-edit-patient")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String editUserProfile(@ModelAttribute("newuser") Patient user, @RequestParam("image") List<MultipartFile> imageFile) throws IOException {

        patientService.update(patientService.getPatientById(userService.getCurrentUser().getId()),user,imageFile);
        return "redirect:/profile";
    }
//    edit admin details
    @PostMapping("/profile-edit-admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String editUserProfile(@ModelAttribute("newuser") User user) {
        userService.update(userService.getCurrentUser(),user);
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        // Get the currently logged-in user
        User user = userService.getCurrentUser();
        // Check if the current password matches the one in the database
        if (passwordEncoder.matches(currentPassword, user.getPassword()) && newPassword.equals(confirmPassword)) {
            // Update the password
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);

            // Redirect to the profile page or wherever you want
            return "redirect:/profile";
        } else {
            // Password change failed, return to the change password page with an error message
            model.addAttribute("error", "Invalid current password or new passwords do not match");
            return "redirect:/profile#security";
        }
    }

    @PostMapping("/address-edit")
    public String editUseraddress(@ModelAttribute("address") Address address) {
        User user = userService.getUserById(userService.getCurrentUser().id);
        if(user.getRole()== Role.HOSPITAL){
            Hospital h= hospitalService.getHospitalById(user.getId());
            h.setAddress(address);
            userService.save(h);
        }
        else {
            Ambulance A= ambulanceService.getAmbulanceById(user.getId());
            A.setAddress(address);
            userService.save(A);

        }
        return "redirect:/profile";

}
    @PostMapping("/profile-resetPassword")
    public String resetPassword(@RequestParam String newPassword) {
        User user = userService.getCurrentUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
        return "redirect:/profile";
    }
    @GetMapping("/forgetPage")
    public String forgetPage() {
        return "account/forgetPass";
    }
    @PostMapping("/forget_pass")
    public String forgetPassword(@RequestParam String resetpassword, @RequestParam String reusername, @RequestParam String reemail, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByEmailAndUserName(reusername, reemail).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(resetpassword));
            userService.save(user);
            return "redirect:/SignIn";
        } else {
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/forgetPage";
        }
    }
    @GetMapping(value = "/ambulance-contactInfo")
    public String displayAmbulance(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("ambulances",ambulanceService.getAllAmbulances());
        return "account/Ambulance contact info";
    }
    @GetMapping(value = "/hospitals")
    public String hospitals(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("hospitalList",hospitalService.getAllHospitals() );
        model.addAttribute("hospitalSections",hSectionsService.getAllHSections() );
        return "patient/display hospitals";
    }
    @PostMapping("/gps_location")
    public String receiveLocation(@RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude) {
        if (userService.getCurrentUser().getRole() ==Role.HOSPITAL){
            Hospital h = hospitalService.getHospitalById(userService.getCurrentUser().getId());
            Address address = new Address();
            address.setLongitude(longitude);
            address.setLatitude(latitude);

            if (h.getAddress() != null && h.getAddress().getId() != null) {
                address = addressService.updateAddress(h.getAddress().getId(), address);
            } else {
                address = addressService.save(address);
            }

            h.setAddress(address);
            hospitalService.save(h);
        } else if  (userService.getCurrentUser().getRole() ==Role.AMBULANCE){
            Ambulance ambulance = ambulanceService.getAmbulanceById(userService.getCurrentUser().getId());
            Address address = new Address();
            address.setLongitude(longitude);
            address.setLatitude(latitude);

            if (ambulance.getAddress() != null && ambulance.getAddress().getId() != null) {
                address = addressService.updateAddress(ambulance.getAddress().getId(), address);
            } else {
                address = addressService.save(address);
            }

            ambulance.setAddress(address);
            ambulanceService.save(ambulance);
        }
        return "redirect:/home";

    }





}