package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.RequestRepository;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.PatientService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;


@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private PatientService patientService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private UserService userService;

    public Request save(Request request){
        return requestRepository.save(request);
    }

    public Request getRequestById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }
    public void deleteRequest(long id) {
        requestRepository.delete(getRequestById(id));
    }
    public List<Request> getAllRequestsForUser(UUID userId){
        return requestRepository.findAllRequestsForUser(userId);
    }
    public List<Request> getAllRequestsForUserWithin24hrs(UUID userId){
        return requestRepository.findRequestsByStatusWithin24Hours(userId,LocalDateTime.now().minusHours(24));
    }
    public List<Request> getAllRequests(){
        return requestRepository.findAll();
    }
    public List<Request> getAllNewRequestsForUser(UUID userId){
        return requestRepository.findAllRequestsForUserByStatus(userId,ReservationStatus.PENDING);
    }
    public  List<Request> findAllRequestsForUserByStatus(UUID userId,ReservationStatus status){
        return requestRepository.findAllRequestsForUserByStatus(userId,status);
    }
    public  List<Request> findAllnonResurvedRequestsForUser(UUID userId){
        return requestRepository.findAllRequestsForUserExceptStatus(userId,ReservationStatus.RESERVED);
    }

    public void statusRequest(long id,ReservationStatus status) {
        Request r =getRequestById(id);
        r.setStatus(status);
        save(r);
    }
    public void acceptRequest(long id, Ambulancetypes type) {
        Request r =getRequestById(id);
        r.setStatus(ReservationStatus.ACCEPTED);
        r.setCarType( type);
        save(r);
    }
//    //    hospitals shull accept within 10min
//    @Scheduled(cron = "0 */10 * * * *") // Executes every 10 minutes
//    public void updateOldPendingRequests() {
//        LocalDateTime tenMinutesAgo = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
//        List<Request> pendingRequests = requestRepository.findPendingAndCreatedAtBefore(tenMinutesAgo);
//
//        for (Request request : pendingRequests) {
//            request.setStatus(ReservationStatus.Deleted);
//            requestRepository.save(request);
//        }
//    }
    //    patient shall confirm within 30 min ( after  about 40 min of creation)
// Scheduled task to delete accepted requests older than 40 minutes
    @Scheduled(fixedRate = 600000) // Executes every 10 minutes
    public void deleteOldAcceptedRequests() {
        LocalDateTime fortyMinutesAgo = LocalDateTime.now().minus(40, ChronoUnit.MINUTES);
        List<Request> acceptedRequests = requestRepository.findAcceptedAndCreatedAtBefore( fortyMinutesAgo);
        for (Request request : acceptedRequests) {
            request.setStatus(ReservationStatus.Deleted);
            requestRepository.save(request);

        }
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        List<Request> pendingRequests = requestRepository.findPendingAndCreatedAtBefore(tenMinutesAgo);
        for (Request request : pendingRequests) {
            request.setStatus(ReservationStatus.Deleted);
            requestRepository.save(request);
        }
    }
    public  boolean AreUserDetailsComplete() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return false; // User not logged in
        }

        if (user.getRole() == Role.PATIENT){
            Patient patient = patientService.getPatientById(user.getId());
            return patient != null && patient.isVerified();
        } else if (user.getRole() == Role.HOSPITAL) {
            Hospital hospital = hospitalService.getHospitalById(user.getId());
            return hospital != null && hospital.getAddress().getLongitude() != 0 && hospital.getAddress().getLatitude() != 0;
        } else if (user.getRole() == Role.AMBULANCE) {
            Ambulance ambulance = ambulanceService.getAmbulanceById(user.getId());
            return ambulance != null && ambulance.getAddress().getLongitude() != 0 && ambulance.getAddress().getLatitude() != 0;
        } else {
            return false; // Unsupported user role
        }
    }




}

