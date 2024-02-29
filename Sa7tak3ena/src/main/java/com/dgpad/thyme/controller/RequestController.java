package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.enums.ReservationType;
import com.dgpad.thyme.model.requests.GRequest;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.service.UserComplements.BedsService;
import org.springframework.beans.BeanUtils;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.PatientService;
import com.dgpad.thyme.service.UserComplements.AmbulanceAgencyService;
import com.dgpad.thyme.service.UserComplements.RequestService;
import com.dgpad.thyme.service.UserComplements.ReservationService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
public class RequestController {
    @Autowired
    private UserService userService;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private AmbulanceAgencyService ambulanceAgencyService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private BedsService bedsService;
    @Autowired
    private ReservationService reservationService;

    //Reservation:
    @GetMapping(value = "/get-all-reservations")
    @PreAuthorize("hasAnyAuthority('HOSPITAL','PATIENT')")
    public String displayReservations(Model model) {
        User cu=userService.getCurrentUser();
        model.addAttribute("userrole",cu.getRole().toString());
        model.addAttribute("reservations",reservationService.getAllReservationForCustomer(cu.id) );
        return "account/Reservation";
    }
    //requests
    @GetMapping(value = "/get-all-requests")
    @PreAuthorize("hasAnyAuthority('HOSPITAL','PATIENT')")
    public String displayRequests(Model model) {
        User cu=userService.getCurrentUser();
        model.addAttribute("userrole",cu.getRole().toString());
        model.addAttribute("requests",requestService.getAllRequestsForUser(cu.id) );
        return "account/Requests";
    }
    //sending (for user)
    @GetMapping("/request-form2")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String showRequestForm2(Model model) {
        model.addAttribute("availablehospitals", hospitalService.getAllHospitals());
        model.addAttribute("availableagencies", ambulanceAgencyService.getAllAgencies());
        model.addAttribute("grequest", new GRequest() );
        return "patient/create request2";
    }

    @PostMapping("/submit2")
    public String submitRequest2(@ModelAttribute GRequest grequest, Model model) {
        System.out.println(grequest.getPreferedhospitals());
        String[] hospitalIDsArray = grequest.getPreferedhospitals().split(",");
        // Creating an ArrayList to store UUIDs
        ArrayList<UUID> hospitalsIDs = new ArrayList<>();
        // Converting each string representation of UUID to UUID and adding to the ArrayList
        for (String hospitalID : hospitalIDsArray) {
            hospitalsIDs.add(UUID.fromString(hospitalID.trim()));
        }
        ArrayList<AmbulanceAgency> ambulanceAgencies = null;

        if(grequest.getPreferedagencies() != null) {
            String[] agenciesIDsArray = grequest.getPreferedagencies().split(",");
            // Creating an ArrayList to store UUIDs
            ArrayList<Integer> agenciesIDs = new ArrayList<>();
            // Converting each string representation of UUID to UUID and adding to the ArrayList
            for (String agencyID : agenciesIDsArray) {
                agenciesIDs.add(Integer.parseInt(agencyID.trim()));
            }
            ambulanceAgencies = new ArrayList<>();

            for (int agencyId : agenciesIDs) {
                ambulanceAgencies.add(ambulanceAgencyService.getAmbulanceAgencyId(agencyId));
            }

        }
        Patient patient=patientService.getPatientById(userService.getCurrentUser().getId());
        for (UUID hospitalId : hospitalsIDs) {
            Request r = new Request();
            BeanUtils.copyProperties(grequest.getRequest(), r, "id");
            r.setCreatedAt(LocalDateTime.now());
            r.setHospital(hospitalService.getHospitalById(hospitalId));
            r.setPatient(patient);
            r.setPreferedAmbulance(ambulanceAgencies);
            r.setStatus(ReservationStatus.PENDING);
            patient.requests.add(requestService.save(r));
        }
        patientService.save(patient);
        model.addAttribute("alertMessage", "Request sent successfully!");
        return "redirect:/home";
    }
    @GetMapping("/change-requestStatus/{status}/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT','HOSPITAL')")
    public String changeRequestStatus(@PathVariable ReservationStatus status,@PathVariable Long id) {
        requestService.statusRequest(id ,status);
        patientService.statusRequest(id,requestService.getRequestById(id).getPatient().id, status);
        return "redirect:/get-all-requests";
    }
    @GetMapping("/reserve-request/{id}")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String reserveRequest(@PathVariable Long id) {
        changeRequestStatus(ReservationStatus.RESERVED,id);
//        check the senario and when each is saved
        //hospital , bed number, floor ,car type recomended
//        requesting ambulances if needed (all details )
//        cancel all the rest requests done att that time
        return "redirect:/get-all-requests";
    }

//    @GetMapping("/request-emergency")
//    @PreAuthorize("hasAnyAuthority('Ambulance')")
//    public String reserveByAmbulance(@RequestParam("id") UUID id) {
//        Reservation r=new Reservation();
//        r.setReservationType(ReservationType.EMERGENCY);
//        //give him an id of vacanced bed
//        int id=0;//must be replaced by vacanced
//        Beds b=bedsService.getBedById(id);
//        b.setNbBeds(b.getNbBeds()-1);
//        r.setHospital(hospitalService.getHospitalByBedId(b.getId()));
//        reservationService.save(r);
//        return "redirect:/home";
//    }

//    private boolean areUserDetailsComplete(User user) {
//        return !user.getAddresses().isEmpty() && user.getPhone() != null && user.getUsername() != null;
//    }
//    @PostMapping("/update-status")
//    public String updateStatus(@RequestParam("id") Long id, @RequestParam("updatedStatus") OrderStatus status){
//        System.out.println(id + " "+status);
//        OnlineOrders order = orderService.getOrderById(id);
//        order.setStatus(status);
//        orderService.updateOrder(order);
//        return "redirect:/get-all-orders";
//    }

//    @GetMapping(value="/cartToOrder")
//    public String CartToOrder() {
//        User user=userService.getUserById(userService.getCurrentUser().id);
//
//            Cart c=user.getCart();
//            OnlineOrders order= new OnlineOrders();
//            order.setUser(user);
//            order.setStatus(OrderStatus.IN_PROCESS);
//            order.setTotalPrice(c.getTotalPrice());
//            order.setOrdersList(new ArrayList<CartItem>(c.getCartItemList()));
//            orderService.createOrder(order);
//            c.setTotalPrice(0.0);
//            c.setCartItemList(new ArrayList<>());
//            cartService.save(c);
//            return "redirect:/display-cart";
//
//        }
//
//    }
}