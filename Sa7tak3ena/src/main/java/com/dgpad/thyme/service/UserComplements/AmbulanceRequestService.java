package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Ambulanceservice;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.Paramedic;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.repository.AmbulanceRequestRepository;
import com.dgpad.thyme.repository.RequestRepository;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AmbulanceRequestService {
    @Autowired
    private AmbulanceRequestRepository requestRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private AmbulanceService ambulanceService;


    public AmbulanceRequest save(AmbulanceRequest request){
        return requestRepository.save(request);
    }

    public AmbulanceRequest getRequestById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }
    public void deleteRequest(long id) {
        requestRepository.delete(getRequestById(id));
    }
    public List<AmbulanceRequest> getAllRequests(){
        return requestRepository.findAll();
    }
    public List<AmbulanceRequest> getAllRequestsForUser(UUID userId){
        return requestRepository.findAllRequestForUser(userId);
    }
    public List<AmbulanceRequest> findAllAmbulanceRequestsForUserByStatus(UUID userId, AmbulanceRequestStatus status){
        return requestRepository.findAllRequestsForUserByStatus(userId, status);
    }
    public AmbulanceRequest update(long id,Ambulanceservice survice,Ambulancetypes carType,Paramedic paramedic,String description, String equipment){
        // Retrieve the existing request from the service
        AmbulanceRequest existingRequest = getRequestById(id);

        if (survice!= Ambulanceservice.inDoorService) {
            existingRequest.setService(survice);
            existingRequest.setDescription(description);
        }
        else
            existingRequest.setDescription(description +"emergency transfer");


        existingRequest.setCar_type(carType);
        existingRequest.setStaff(paramedic);
        if (equipment!=null)
            existingRequest.setEquipments(equipment);

        // Save the modified request back to the database
        return save(existingRequest);

    }
    public AmbulanceRequest updateNonapped(long id,String description, String equipment){
        // Retrieve the existing request from the service
        AmbulanceRequest existingRequest = getRequestById(id);

        existingRequest.setDescription(description);
        if (equipment!=null)
            existingRequest.setEquipments(equipment);
        // Save the modified request back to the database
        return save(existingRequest);

    }
    public AmbulanceRequest update(long id,Paramedic paramedic, String equipment){
        // Retrieve the existing request from the service
        AmbulanceRequest existingRequest = getRequestById(id);
        existingRequest.setStaff(paramedic);
        if (equipment!=null)
            existingRequest.setEquipments(equipment);
        // Save the modified request back to the database
        return save(existingRequest);

    }
    public void dispatchToAvailableNearest (Address address){
        //        dispatch according to availablity (enabled ones) ordered according to distance
        List<Ambulance> filteredAmbulances= addressService.sortAmbulancesByDistance(address,ambulanceService.getAllCompletedAmbulances());
        for (int i = 0; i < 2; i++) {
            Ambulance A = filteredAmbulances.get(i);
            AmbulanceRequest AR =new AmbulanceRequest();
            AR.setAmbulance(A);
            AR.setService(Ambulanceservice.homeService);
            AR.setStatus(AmbulanceRequestStatus.PENDING);
            AR.setSender(userService.getCurrentUser());
            AR.setCreatedAt(LocalDateTime.now());
            AR.setPickupaddress(address);
            save(AR);
        }
    }
    public List<AmbulanceRequest> getAllAmbulanceRequestsForUserWithin24hs(UUID userId){
        // Retrieve all reservations for the user
        List<AmbulanceRequest> allReservations = getAllRequestsForUser(userId);

        // Filter reservations within the last 24 hours
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<AmbulanceRequest> reservationsWithin24Hours = allReservations.stream()
                .filter(reservation -> reservation.getCreatedAt().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());
        // Add both lists to the model
        return reservationsWithin24Hours;
    }
//    history requests
    public List<AmbulanceRequest> getAllAmbulanceRequestsForUserOutside24hs(UUID userId){
        // Retrieve all reservations for the user
        List<AmbulanceRequest> allReservations = getAllRequestsForUser(userId);
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        // Filter reservations outside 24 hours
        List<AmbulanceRequest> reservationsOutside24Hours = allReservations.stream()
                .filter(reservation -> !reservation.getCreatedAt().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());

        // Add both lists to the model
        return reservationsOutside24Hours;
    }
    //    ambulance shull accept within 10min
    @Scheduled(cron = "0 */10 * * * *") // Executes every 10 minutes
    public void updateOldPendingRequests() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        List<AmbulanceRequest> pendingRequests = requestRepository.findByStatusAndCreatedAtBefore(AmbulanceRequestStatus.PENDING, tenMinutesAgo);

        for (AmbulanceRequest request : pendingRequests) {
            request.setStatus(AmbulanceRequestStatus.Deleted);
            requestRepository.save(request);
        }
    }

}

