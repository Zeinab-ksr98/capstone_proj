package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.RequestBedCategory;
import com.dgpad.thyme.model.usercomplements.AccountRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.*;
import com.dgpad.thyme.service.UserComplements.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

//the following controller group out the common functions that are accessed by all the users
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
    private AddressService addressService;
    @Autowired
    private AccountRequestService accountRequestService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AmbulanceAgencyService ambulanceAgencyService;
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
        return "account/feedback";
    }

    @GetMapping(value = "/SignIn")
    public String SignIn(Model model) {
        model.addAttribute("newuser", new Patient());
        return "account/login_signup";
    }


    @GetMapping(value = "/home")
    public String displayHome(Model model) {
        model.addAttribute("userrole", userService.getCurrentUser().getRole().toString());
        model.addAttribute("alertMessage", null);

        if (userService.getCurrentUser().getRole()== Role.ADMIN)
            return "redirect:/Requests";
        else if (userService.getCurrentUser().getRole()== Role.PATIENT){
            List<AmbulanceRequest> requests = ambulancerequestService.findAllAmbulanceRequestsForUserByStatus(userService.getCurrentUser().id, AmbulanceRequestStatus.PENDING);
            model.addAttribute("requests_size",requests.size());

            return "patient/Home";
        }
        else if (userService.getCurrentUser().getRole()== Role.HOSPITAL) {
            Hospital cu =hospitalService.getHospitalById(userService.getCurrentUser().getId());
            List<Integer> analytics = new ArrayList<>();
            analytics.add(cu.getReservations().size());
            analytics.add(cu.getRequests().size());
            analytics.add(0);
            model.addAttribute("analytics",analytics);
            model.addAttribute("hospitals", hospitalService.getAllHospitals());

            model.addAttribute("admin", userService.getCurrentUser().isAdministrator());
            return "hospital/Home";
        }
        else if (userService.getCurrentUser().getRole()== Role.AMBULANCE) {
            return "redirect:/new-requests";
        }
        return"landingPages/Main";
//        return "redirect:/Main";

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
        // Add the "role" and "user" attributes to the model
        model.addAttribute("role", user.getRole().toString());

        return "account/Profile";
    }
    @PostMapping("/profile-edit-ambulance")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String editUserProfile(@ModelAttribute("newuser") Ambulance user) {
        ambulanceService.save(user);
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
    public String editUserProfile(@ModelAttribute("newuser") Patient user) {

        patientService.update(patientService.getPatientById(userService.getCurrentUser().getId()),user);
        return "redirect:/profile";
    }
//    edit admin details
    @PostMapping("/profile-edit-admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String editUserProfile(@ModelAttribute("newuser") User user) {
        userService.save(user);
        return "redirect:/profile";
    }

//    @PostMapping("/profile-edit")
//    public String editUserProfile(@ModelAttribute("user") User user) {
//        userService.update(user);
////        switch (user.getRole()) {
////            case ADMIN:
////                userService.save(user);
////                break;
////            case PATIENT:
////                u.update((Patient) user);
////                break;
////            case HOSPITAL:
////                hospitalService.save((Hospital) user);
////                break;
////            case AMBULANCE:
////                ambulanceService.save((Ambulance) user);
////                break;
////        }
//        return "redirect:/profile";
//    }
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

    @GetMapping("/delete-address/{addressID}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public String deleteAddress(@PathVariable Long addressID) {
        Address address = addressService.getAddressById(addressID);
        User user = userService.getUserById(userService.getCurrentUser().id);

        if (address != null) {
            if(user.getRole()== Role.HOSPITAL){
                Hospital h= hospitalService.getHospitalById(user.getId());
                userService.save(user);
                addressService.deleteAddress(address);
            }
            else {
                Ambulance A= ambulanceService.getAmbulanceById(user.getId());

            }
        }

        return "redirect:/profile#";
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
        model.addAttribute("role", user.getRole().toString());
        model.addAttribute("ambulances",ambulanceService.getAllAmbulances());
        return "account/Ambulance contact info";
    }
    @GetMapping(value = "/hospitals")
    public String hospitals(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("role", user.getRole().toString());
        model.addAttribute("hospitalList",hospitalService.getAllHospitals() );
        model.addAttribute("hospitalSections",hSectionsService.getAllHSections() );
        return "patient/display hospitals";
    }

}