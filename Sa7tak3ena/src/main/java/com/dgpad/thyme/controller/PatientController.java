package com.dgpad.thyme.controller;

import com.dgpad.thyme.Stripe.StripeRequest;
import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Ambulanceservice;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.UserComplements.AddressService;
import com.dgpad.thyme.service.UserComplements.AmbulanceRequestService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller

public class PatientController {
    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceRequestService ambulanceRequestService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private AddressService addressService;
    // request from patient dispach
    @GetMapping("/ambulance_request")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String submitRequest(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude, Model model) {
        Address address =new Address();
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        addressService.save(address);
//        dispatch according to availablity (enabled ones) ordered according to distance
        List<Ambulance> filteredAmbulances= addressService.sortAmbulancesByDistance(address,ambulanceService.getAllAmbulances());
        for (int i = 0; i < 2; i++) {
            Ambulance A = filteredAmbulances.get(i);
            AmbulanceRequest AR =new AmbulanceRequest();
            AR.setAmbulance(A);
            AR.setService(Ambulanceservice.homeService);
            AR.setStatus(AmbulanceRequestStatus.PENDING);
            AR.setSender(userService.getCurrentUser());
            AR.setCreatedAt(LocalDateTime.now());
            AR.setPickupaddress(address);
            ambulanceRequestService.save(AR);
        }
        model.addAttribute("alertMessage", "Request sent successfully!");
        return "redirect:/home";
    }

    @Value("${stripe.api.publicKey}")
    private String publicKey;
    @GetMapping("/donate")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String showCard( Model model){
        StripeRequest request =new StripeRequest(5,userService.getCurrentUser().email);
        model.addAttribute("publicKey", publicKey);
        model.addAttribute("amount", request.getAmount());
        model.addAttribute("email", request.getEmail());
        return "Stripe/checkout";
    }



    // detailed request from patient  dispach based on given location
//    @GetMapping("/ambulance_detailrequest")
//    @PreAuthorize("hasAnyAuthority('PATIENT')")
//    public String submitDetailedRequest(  Model model) {
////        un comment these where i develop a way to dispatch under location and availability
////        for (Ambulance A : ambulanceService.getAllAmbulances()) {
//        AmbulanceRequest AR =new AmbulanceRequest();
//
////            AR.setAmbulance(A);
//        AR.setService(Ambulanceservice.homeService);
//        AR.setStatus(AmbulanceRequestStatus.PENDING);
//        AR.setSender(userService.getCurrentUser());
//        AR.setCreatedAt(LocalDateTime.now());
//        //add the address(from)
//        ambulanceRequestService.save(AR);
////        }
//        model.addAttribute("alertMessage", "Request sent successfully!");
//        return "redirect:/home";
//    }
}
