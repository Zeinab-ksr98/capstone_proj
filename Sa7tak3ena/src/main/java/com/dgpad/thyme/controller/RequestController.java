package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.*;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.GRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.service.UserComplements.*;
import org.springframework.beans.BeanUtils;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.PatientService;
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
    private AmbulanceRequestService ambulanceRequestService;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AddressService addressService;

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
    public String showRequestForm2(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude, Model model) {
//        order the Enabled hospital list according to distance
        Address address =new Address();
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        addressService.save(address);
        List<Hospital> sortedHospitals = addressService.sortHospitalsByDistance(address,hospitalService.getAllEnabledHospitals());
        model.addAttribute("availablehospitals", sortedHospitals);
        model.addAttribute("fromhospitals", hospitalService.getAllHospitals());
        model.addAttribute("availableagencies", ambulanceAgencyService.getAllAgencies());
        model.addAttribute("grequest", new GRequest());
//        when initially no requests and when there is requests and one of the requests is confirmed
        List<Request> requestsLength = requestService.getAllRequestsForUser(userService.getCurrentUser().id);
        List<Request> requests = requestService.findAllRequestsForUserByStatus(userService.getCurrentUser().id,ReservationStatus.CONFIRMED);
        boolean allow= requestsLength.isEmpty() || (requestsLength.size()>0 && requests.size() ==0);
        model.addAttribute("allow",allow);
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
//            fill the agencies with agency object
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
//    accept request is done by two ways ether only change status (when no need for ambulance)
//    or by using accept-request where status, ambulance car are modified
    @PostMapping("/accept-request")
    @PreAuthorize("hasAnyAuthority('PATIENT','HOSPITAL')")
    public String acceptRequest(@RequestParam Long id, @RequestParam Ambulancetypes type) {
        requestService.acceptRequest(id ,type);
        patientService.acceptRequest(id,requestService.getRequestById(id).getPatient().id,type);
        return "redirect:/get-all-requests";
    }

    @GetMapping("/reserve-request/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String reserveRequest(@PathVariable Long id) {
        Request request=requestService.getRequestById(id);
        requestService.statusRequest(id ,ReservationStatus.CONFIRMED);
        //initializing reservation
        Reservation reservation =new Reservation();

        if (request.isNeedAmbulance()){
    //        processing the prefered ambulance if exist by getting those with prefered agency and filtering them according to cartype, enable,location based (4 based filtration)

            for (AmbulanceAgency agency:request.getPreferedAmbulance()) {
//                filtering by agency, car type needed and enable
                List<Ambulance> filteredAmbulance =ambulanceService.getAmbulanceByAvailableCarTypeAndAgency(agency,request.getCarType());
//                filtering by location
                filteredAmbulance= addressService.sortAmbulancesByDistance(request.getPickupAddress(),filteredAmbulance);
                for (int i = 0; i < 2; i++) {
                    Ambulance ambulance = filteredAmbulance.get(i);
                    AmbulanceRequest ambulanceRequest =new AmbulanceRequest();

                    ambulanceRequest.setTo(request.getHospital().getAddress());
                    ambulanceRequest.setPickupaddress(request.getPickupAddress());

                    ambulanceRequest.setAmbulance(ambulance);
                    ambulanceRequest.setSender(request.getPatient());
                    ambulanceRequest.setHospital(ambulanceRequest.getHospital());

                    ambulanceRequest.setService(Ambulanceservice.transfer);
                    ambulanceRequest.setCar_type(request.getCarType());
                    ambulanceRequest.setDescription(request.getMedicalRecord());

                    ambulanceRequest.setStatus(AmbulanceRequestStatus.PENDING);
                    ambulanceRequest.setCreatedAt(LocalDateTime.now());
                    ambulanceRequest.setFrom_hospital(true);
                    ambulanceRequestService.save(ambulanceRequest);
                    reservation.getAmbulanceRequests().add(ambulanceRequest);
                }
            }
        }
        reservation.setReservationType(request.getReservationType());
        reservation.setStatus(ReservationStatus.RESERVED);

        reservation.setHospital(request.getHospital());
        reservation.setPatient(request.getPatient());
        reservation.setMedicalRecord(request.getMedicalRecord());
        reservation.setCreatedAt(LocalDateTime.now());
        reservationService.save(reservation);
//        cancelling all other hospital not reserved requests to ensure that only one reservation at a time
        for (Request existingRequests : requestService.findAllRequestsForUserByStatus(userService.getCurrentUser().id,ReservationStatus.PENDING)) {
            if (existingRequests !=request) {
                existingRequests.setStatus(ReservationStatus.Deleted);
                requestService.save(existingRequests);
            }
        }
        requestService.statusRequest(id ,ReservationStatus.RESERVED);

        return "redirect:/get-all-requests";
    }


//    private boolean areUserDetailsComplete(User user) {
//        return !user.getAddresses().isEmpty() && user.getPhone() != null && user.getUsername() != null;
//    }
//    @PostMapping("/update-status")
//   ------------ public String updateStatus(@RequestParam("id") Long id, @RequestParam("updatedStatus") OrderStatus status){
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