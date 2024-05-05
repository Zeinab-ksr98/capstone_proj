package com.dgpad.thyme.controller;

import com.dgpad.thyme.Email.EmailService;
import com.dgpad.thyme.model.Image;
import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.*;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.GRequest;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.usercomplements.Paramedic;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private EmailService emailService;

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
    private ImageService imageService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AddressService addressService;

    //Reservation:
    @GetMapping(value = "/get-all-reservations")
    @PreAuthorize("hasAnyAuthority('HOSPITAL','PATIENT')")
    public String displayReservations(Model model) {
        User cu = userService.getCurrentUser();
        model.addAttribute("user", cu);
        model.addAttribute("reservations", reservationService.getAllReservationForCustomerWithin24hs(cu.id));

        return "account/Reservation";
    }

    @GetMapping(value = "/get-history-reservations")
    @PreAuthorize("hasAnyAuthority('HOSPITAL','PATIENT')")
    public String displayHistoryReservations(Model model) {
        User cu = userService.getCurrentUser();
        model.addAttribute("user", cu);
        model.addAttribute("reservations", reservationService.getAllReservationForCustomerOutside24hs(cu.id));

        return "account/HistoryReservation";
    }

    //requests
    @GetMapping(value = "/get-all-requests")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String displayRequests(Model model) {
        User cu = userService.getCurrentUser();
        model.addAttribute("user", cu);
        model.addAttribute("requests", requestService.getAllRequestsForUser(cu.id));
        return "account/Requests";
    }

    //sending (for user)
    @GetMapping("/request-form2")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String showRequestForm2(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude, Model model) {
//        order the Enabled hospital list according to distance
        Address address = new Address();
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        addressService.save(address);
        List<Hospital> sortedHospitals = addressService.sortHospitalsByDistance(address, hospitalService.getAllEnabledHospitals());
        model.addAttribute("availablehospitals", sortedHospitals);
        model.addAttribute("fromhospitals", hospitalService.getAllHospitals());
        model.addAttribute("availableagencies", ambulanceAgencyService.getAllAgencies());
        model.addAttribute("grequest", new GRequest());
        model.addAttribute("user", userService.getCurrentUser());

//        when initially no requests and when there is requests and one of the requests is confirmed
        List<Request> requestsLength = requestService.getAllRequestsForUser(userService.getCurrentUser().id);
        List<Request> requests = requestService.findAllRequestsForUserByStatus(userService.getCurrentUser().id, ReservationStatus.CONFIRMED);
        boolean allow = requestsLength.isEmpty() || (requestsLength.size() > 0 && requests.size() == 0);
        if (allow && requestService.AreUserDetailsComplete())
            return "patient/create request2";
        else if (!requestService.AreUserDetailsComplete())
            return "redirect:/error_page";
        else
            return "account/error";
    }

    @PostMapping("/submit2")
    public String submitRequest2(@RequestParam("location") String location, @RequestParam("lat") double latitude, @RequestParam("lon") double longitude, @ModelAttribute GRequest grequest, @RequestParam("images") List<MultipartFile> imageFile, Model model) throws IOException {
        Address address = new Address();
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        address.setName(location);
        address = addressService.save(address);
        System.out.println(grequest.getPreferedhospitals());
        String[] hospitalIDsArray = grequest.getPreferedhospitals().split(",");
        // Creating an ArrayList to store UUIDs
        ArrayList<UUID> hospitalsIDs = new ArrayList<>();
        // Converting each string representation of UUID to UUID and adding to the ArrayList
        for (String hospitalID : hospitalIDsArray) {
            hospitalsIDs.add(UUID.fromString(hospitalID.trim()));
        }
        ArrayList<AmbulanceAgency> ambulanceAgencies = null;

        if (grequest.getPreferedagencies() != null) {
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
        if (grequest.getRequest().getReservationType() != ReservationType.EMERGENCY) {
            List<Image> images = imageService.saveImages(imageFile);
            grequest.getRequest().setDoctorReport(images);
        }
        Patient patient = patientService.getPatientById(userService.getCurrentUser().getId());
        for (UUID hospitalId : hospitalsIDs) {
            Request r = new Request();
            BeanUtils.copyProperties(grequest.getRequest(), r, "id");
            r.setCreatedAt(LocalDateTime.now());
            r.setHospital(hospitalService.getHospitalById(hospitalId));

            r.setPickupAddress(address);
            r.setPatient(patient);
            r.setPreferedAmbulance(ambulanceAgencies);
            r.setStatus(ReservationStatus.PENDING);
            Request request = requestService.save(r);
            patient.requests.add(request);
            hospitalService.getHospitalById(hospitalId).requests.add(request);
            patientService.save(patient);
            hospitalService.save(hospitalService.getHospitalById(hospitalId));
            emailService.senddetailsEmail(hospitalService.getHospitalById(hospitalId).email, 3);

        }
        patientService.save(patient);
        model.addAttribute("alertMessage", "Request sent successfully!");
        return "redirect:/home";
    }

    @GetMapping("/change-requestStatus/{status}/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT','HOSPITAL')")
    public String changeRequestStatus(@PathVariable ReservationStatus status, @PathVariable Long id) {
        requestService.statusRequest(id, status);
        patientService.statusRequest(id, requestService.getRequestById(id).getPatient().id, status);
        if (userService.getCurrentUser().getRole() == Role.PATIENT)
            return "redirect:/home";
        return "redirect:/get-all-requests";
    }

    //    accept request is done by two ways ether only change status (when no need for ambulance)
//    or by using accept-request where status, ambulance car are modified
    @PostMapping("/accept-request")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String acceptRequest(@RequestParam Long id, @RequestParam Ambulancetypes type) {
        requestService.acceptRequest(id, type);
        patientService.acceptRequest(id, requestService.getRequestById(id).getPatient().id, type);
        return "redirect:/get-all-requests";
    }

    @GetMapping("/reserve-request/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public String reserveRequest(@PathVariable Long id) throws IOException {
        Request request = requestService.getRequestById(id);
        requestService.statusRequest(id, ReservationStatus.RESERVED);

        // Initialize reservation
        Reservation reservation = new Reservation();
// request ambulances if needed
        if (request.isNeedAmbulance()) {
            for (AmbulanceAgency agency : request.getPreferedAmbulance()) {
                // Filtering ambulances
                List<Ambulance> filteredAmbulance = new ArrayList<>();
                if (request.getCarType() != Ambulancetypes.any)
                    filteredAmbulance = ambulanceService.getAmbulanceByAvailableCarTypeAndAgency(agency, request.getCarType());
                else
                    filteredAmbulance = ambulanceService.getcompletedAmbulanceByAgency(agency);

                filteredAmbulance = addressService.sortAndFilterAmbulancesByDistance(request.getPickupAddress(), filteredAmbulance, 80); // Filter by 80 km
//                filteredAmbulance = addressService.sortAmbulancesByDistance(request.getPickupAddress(), filteredAmbulance);

                for (int i = 0; i < filteredAmbulance.size(); i++) {
                    Ambulance ambulance = filteredAmbulance.get(i);
                    AmbulanceRequest ambulanceRequest = new AmbulanceRequest();

                    ambulanceRequest.setTo(request.getHospital().getAddress());
                    ambulanceRequest.setPickupaddress(request.getPickupAddress());
                    ambulanceRequest.setAmbulance(ambulance);
                    ambulanceRequest.setSender(request.getPatient());
                    ambulanceRequest.setHospital(request.getHospital()); // <-- Ensure hospital is set here
                    ambulanceRequest.setService(Ambulanceservice.transfer);
                    ambulanceRequest.setCar_type(request.getCarType());
                    ambulanceRequest.setDescription(request.getMedicalRecord());
                    ambulanceRequest.setStatus(AmbulanceRequestStatus.PENDING);
                    ambulanceRequest.setCreatedAt(LocalDateTime.now());
                    ambulanceRequest.setFrom_hospital(true);

                    ambulanceRequest = ambulanceRequestService.save(ambulanceRequest);
                    reservation.getAmbulanceRequests().add(ambulanceRequest);
                    emailService.senddetailsEmail(ambulance.email, 3);

                }

            }
        }

//        modify rest details
        reservation.setReservationType(request.getReservationType());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setHospital(request.getHospital()); // Ensure hospital is set here as well
        Patient patient = patientService.getPatientById(request.getPatient().id);
        reservation.setPatient(patient);
        reservation.setMedicalRecord(request.getMedicalRecord());
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation savedReservation = reservationService.save(reservation);
        patient.reservations.add(savedReservation); // Update patient's reservations
        patientService.save(patient);

        // Cancel all other hospital non-reserved requests
        for (Request existingRequest : requestService.findAllnonResurvedRequestsForUser(userService.getCurrentUser().id)) {
            if (!existingRequest.equals(request)) { // Fix the condition for comparison
                existingRequest.setStatus(ReservationStatus.Deleted);
                requestService.save(existingRequest);
            }
        }

        Hospital hospital = savedReservation.getHospital();
        hospital.reservations.add(savedReservation); // Update hospital's reservations
        hospitalService.save(hospital);

        // Update request status
//        requestService.statusRequest(id, ReservationStatus.RESERVED);

        return "redirect:/home";
    }

    @PostMapping("/update-reservation")
    @PreAuthorize("hasAnyAuthority('HOSPITAL')")
    public String updateReservation(@RequestParam("id") Long id, @RequestParam("bed") String bed, @RequestParam("floor") int floor) {
        Reservation r = reservationService.getReservationById(id);
        r.setBednum(bed);
        r.setFloornum(floor);
        reservationService.save(r);
        return "redirect:/get-all-reservations";
    }
}