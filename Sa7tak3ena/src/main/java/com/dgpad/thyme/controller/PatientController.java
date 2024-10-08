package com.dgpad.thyme.controller;

import com.dgpad.thyme.Stripe.StripeRequest;
import com.dgpad.thyme.Whatsapp.VerificationSender;
import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Ambulanceservice;
import com.dgpad.thyme.model.enums.Distracts;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.UserComplements.AddressService;
import com.dgpad.thyme.service.UserComplements.AmbulanceRequestService;
import com.dgpad.thyme.service.UserComplements.RequestService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.dgpad.thyme.model.enums.Role.HOSPITAL;
import static com.dgpad.thyme.model.enums.Role.PATIENT;

@Controller

public class PatientController {
    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceRequestService ambulanceRequestService;
    @Autowired
    private RequestService requestService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private VerificationSender verificationSender;

    // request from patient dispach
    @GetMapping("/ambulance_request")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String submitRequest(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude, Model model) throws IOException {
        Address address = new Address();
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        address = addressService.save(address);
        ambulanceRequestService.dispatchToAvailableNearest(address, null);
        verificationSender.sendVerificationCode(userService.getCurrentUser().getPhone(),
                "Your ambulance request has been successfully submitted and is now being processed. We will notify you with updates shortly. Wishing you a swift and smooth recovery."
        );
        model.addAttribute("alertMessage", "Request sent successfully!");
        return "redirect:/home";
    }

    @GetMapping("/donateAmbulance")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String donateAmbulance(Model model) {
        model.addAttribute("user", userService.getCurrentUser());

        return "patient/donate";
    }



    @Value("${stripe.api.publicKey}")
    private String publicKey;

    @GetMapping("/donate")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String showCard(Model model) {
        StripeRequest request = new StripeRequest(5, userService.getCurrentUser().email);
        model.addAttribute("publicKey", publicKey);
        model.addAttribute("amount", request.getAmount());
        model.addAttribute("email", request.getEmail());
        return "Stripe/checkout";
    }

    @GetMapping("/fullAmbulance_request")
    @PreAuthorize("hasAnyAuthority('PATIENT','HOSPITAL')")
    public String FullAmbulanceRequest(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        List<AmbulanceRequest> requests = ambulanceRequestService.findAllAmbulanceRequestsForUserByStatus(userService.getCurrentUser().id, AmbulanceRequestStatus.PENDING);
        if (user.getRole() == PATIENT && requestService.AreUserDetailsComplete())
            return "patient/fullARequest";
        else if (user.getRole() == HOSPITAL && requestService.AreUserDetailsComplete())
            return "hospital/fullARequest";
        return "redirect:/error_page";

    }


    // detailed request from patient  dispach based on given location
    @PostMapping("/ambulance_detailrequest")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String submitDetailedRequest(@RequestParam("description") String description,@RequestParam("location") String location, @RequestParam("lat") double latitude, @RequestParam("lon") double longitude, Model model) throws IOException {
        Address address =new Address();
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        address.setName(location);
        address= addressService.save(address);
        ambulanceRequestService.dispatchToAvailableNearest(address,description);
        verificationSender.sendVerificationCode(userService.getCurrentUser().getPhone(),
                "Your ambulance request has been successfully submitted and is now being processed. We will notify you with updates shortly. Wishing you a swift and smooth recovery."
        );
        model.addAttribute("alertMessage", "Request sent successfully!");
        return "redirect:/home";
    }
}
