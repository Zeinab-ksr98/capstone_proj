package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.enums.*;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.UserComplements.AmbulanceCarService;
import com.dgpad.thyme.service.UserComplements.AmbulanceRequestService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class AmbulanceController {

    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceService ambulanceService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AmbulanceRequestService ambulanceRequestService;
    private final AmbulanceCarService ambulanceCarService;

    @Autowired
    public AmbulanceController(AmbulanceCarService ambulanceCarService) {
        this.ambulanceCarService = ambulanceCarService;
    }
    @GetMapping("/manage-car")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageCar(Model model) {
        model.addAttribute("cars", ambulanceCarService.getAllCars());
        return "ambulanceAgency/manage-car";
    }

    @PostMapping("/add-car")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addCar(@RequestParam("cartype") Ambulancetypes cartype, @RequestParam("num") int num) {
        for (int i = 0; i < num ; i++) {
            AmbulanceCar car = new AmbulanceCar();
            car.setStatus(AmbulanceStatus.Reserved);
            car.setType(cartype);
            ambulanceCarService.save(car);
        }
        return "redirect:/manage-car";
    }
    @GetMapping("/update-car")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateCar(@RequestParam("id") int id, @RequestParam("updatedStatus") AmbulanceStatus status) {
        AmbulanceCar car = ambulanceCarService.getCarById(id);
        car.setStatus(status);
        ambulanceCarService.save(car);
        return "redirect:/manage-car";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String deleteCar(@PathVariable int id) {
        ambulanceCarService.deleteCar(id);
        return "redirect:/manage-car";
    }
    // request from patient dispach
    @GetMapping("/ambulance_request")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String submitRequest( Model model) {
//        un comment these where i develop a way to dispatch under location
//        for (Ambulance A : ambulanceService.getAllAmbulances()) {
            AmbulanceRequest AR =new AmbulanceRequest();
//            AR.setAmbulance(A);
            AR.setService(Ambulanceservice.homeService);
            AR.setStatus(AmbulanceRequestStatus.PENDING);
            AR.setSender(userService.getCurrentUser());
            AR.setCreatedAt(LocalDateTime.now());
            //add the address(from)
            ambulanceRequestService.save(AR);
//        }
        model.addAttribute("alertMessage", "Request sent successfully!");
        return "redirect:/home";
    }
    @GetMapping("/new-requests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String newRequests(Model model) {
        model.addAttribute("requests", ambulanceRequestService.getAllRequests());
        return "ambulanceAgency/home";
    }
    //all new request in home
    @GetMapping("/updatestatus")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String acceptRequest(@RequestParam("id") long id) {
        AmbulanceRequest r =ambulanceRequestService.getRequestById(id);
        r.setStatus(AmbulanceRequestStatus.ACCEPTED);
        r.setAmbulance(ambulanceService.getAmbulanceById(userService.getCurrentUser().getId()));
        ambulanceRequestService.save(r);
        return "redirect:/home";
    }
    @GetMapping("/manage-requests")
    @PreAuthorize("hasAuthority('AMBULANCE')")
    public String manageRequests(Model model) {
        model.addAttribute("newrequest",new AmbulanceRequest());
        model.addAttribute("requests", ambulanceRequestService.getAllRequestsForUser(userService.getCurrentUser().getId()));
        return "ambulanceAgency/Requests";
    }
    @PostMapping("/add-request")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String addRequest(@RequestParam("newrequest") AmbulanceRequest newrequest) {
        newrequest.setAmbulance(ambulanceService.getAmbulanceById(userService.getCurrentUser().getId()));
        newrequest.setStatus(AmbulanceRequestStatus.ACCEPTED);
        newrequest.setCreatedAt(LocalDateTime.now());
        ambulanceRequestService.save(newrequest);
        return "redirect:/manage-requests";
    }
    @GetMapping("/update-requestdetails")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String updateRequest(@RequestParam("id") int id, @RequestParam("updatedRequest") AmbulanceRequest request) {
//        AmbulanceCar car = ambulanceCarService.getCarById(id);
//        car.setStatus(status);
//        ambulanceCarService.save(car);
        return "redirect:/manage-requests";
    }

    @PostMapping(value = "/create-ambulance")
    @PreAuthorize("hasAnyAuthority('AMBULANCE')")
    public String CreateAdmin(@RequestParam("username") String userName,@RequestParam("publicname") String publicName,  @RequestParam("email") String email, @RequestParam("phone") String phone) {
        Ambulance ambulance = new Ambulance(userName, publicName, email, passwordEncoder.encode("123"), phone);
        ambulance.setAgency(ambulanceService.getAmbulanceById(userService.getCurrentUser().getId()).getAgency());
        ambulanceService.save(ambulance);
        return "redirect:/home";
    }
}