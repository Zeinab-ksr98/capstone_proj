package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.Email.EmailService;
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
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.AmbulanceRequestRepository;
import com.dgpad.thyme.repository.RequestRepository;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AmbulanceRequestService {
    @Autowired
    private AmbulanceRequestRepository requestRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private HospitalService hospitalService;


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
    public List<AmbulanceRequest> findAllRequestbystatus(AmbulanceRequestStatus status){
        return requestRepository.findAllRequestbystatus(status);
    }
    public List<AmbulanceRequest> findRequestsWithin24Hours(UUID cuId) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return requestRepository.findRequestsByStatusWithin24Hours(cuId, twentyFourHoursAgo);
    }


    public List<AmbulanceRequest> getAllRequestsForUser(UUID userId){
        return requestRepository.findAllRequestForUser(userId);
    }
    public List<AmbulanceRequest> getAllRequestsForUserWith24hours(UUID userId){
        return requestRepository.findRequestsByStatusWithin24Hours(userId,LocalDateTime.now().minusHours(24));
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
    public void dispatchToAvailableNearest (Address address, String description) throws IOException {
        //        dispatch according to availablity (enabled ones) and have the car type ordered according to distance
        List<Ambulance> filteredAmbulances= addressService.sortAmbulancesByDistance(address,ambulanceService.getAllCompletedAmbulances());
        for (int i = 0; i < 2; i++) {
            Ambulance A = filteredAmbulances.get(i);
            AmbulanceRequest AR =new AmbulanceRequest();
            AR.setAmbulance(A);
            AR.setService(Ambulanceservice.homeService);
            AR.setStatus(AmbulanceRequestStatus.PENDING);
            AR.setSender(userService.getCurrentUser());
            AR.setDescription(description);
            AR.setCreatedAt(LocalDateTime.now());
            AR.setPickupaddress(address);
            save(AR);
            emailService.senddetailsEmail(A.email,3);

        }
    }
    public void hospitalrequestingAmbulance (Address address,String regin,boolean toH,Ambulancetypes car_type,String description) throws IOException {
        Hospital cu =hospitalService.getHospitalById(userService.getCurrentUser().id);
        List<Ambulance> filteredAmbulances = null;
        //        dispatch according to available Ambulances (enabled ones) in the region of the from ( eather the hospital or the given one)
        if (car_type !=Ambulancetypes.any)
            filteredAmbulances=ambulanceService.getAmbulanceByAvailableCarTypeAndRegion(car_type,regin);
        else if (filteredAmbulances ==null || car_type.equals(Ambulancetypes.any) )
            filteredAmbulances =ambulanceService.getAllCompletedAmbulancesinRegion(regin);

        // send a request for all ambulance in that region
        for (int i = 0; i < filteredAmbulances.size()-1; i++) {
            Ambulance A = filteredAmbulances.get(i);
            AmbulanceRequest AR =new AmbulanceRequest();
            AR.setAmbulance(A);
            AR.setService(Ambulanceservice.transfer);
            AR.setStatus(AmbulanceRequestStatus.PENDING);
            AR.setSender(cu);
            AR.setDescription(description);
            AR.setHospital(hospitalService.getHospitalById(cu.id));
            AR.setCreatedAt(LocalDateTime.now());
            AR.setFrom_hospital(true);
            AR.setCar_type(car_type);
            if (toH) {
                AR.setPickupaddress(address);
                AR.setTo(cu.getAddress());
            }
            else {
                AR.setTo(address);
                AR.setPickupaddress(cu.getAddress());
            }
            AR=save(AR);
            cu.ambulanceRequest.add(AR);
            hospitalService.save(cu);
            emailService.senddetailsEmail(A.email,3);

        }
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
    private static final Logger logger = LoggerFactory.getLogger(AmbulanceRequestService.class);

    @Scheduled(cron = "0 */10 * * * *")//every 10 min delete pending requests
    public void updateOldPendingRequests() {
        try {
            LocalDateTime tenMinutesAgo = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
            List<AmbulanceRequest> pendingRequests = requestRepository.findPendingCreatedAtBefore(tenMinutesAgo);

            logger.info("Found {} pending requests created more than 10 minutes ago.", pendingRequests.size());
            if (!pendingRequests.isEmpty()){
                for (AmbulanceRequest request : pendingRequests) {
                    request.setStatus(AmbulanceRequestStatus.Deleted);
                    requestRepository.save(request);
                    logger.info("Deleted pending request with ID: {}", request.getId());
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while updating old pending requests: {}", e.getMessage(), e);
        }
    }
    public Map<Month, Integer> getAcceptedRequestsPerMonth(UUID ambulanceId) {
        Map<Month, Integer> requestsPerMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            LocalDateTime startOfMonth = LocalDateTime.now().withMonth(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth().length(startOfMonth.toLocalDate().isLeapYear())).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            int count = requestRepository.countAcceptedRequestsForAmbulanceInTimeRange(ambulanceId, startOfMonth, endOfMonth);
            requestsPerMonth.put(startOfMonth.getMonth(), count);
        }

        return requestsPerMonth;
    }
    public int getAmbulanceRankForCurrentUserInCurrentMonth(UUID currentUserId) {
        // Retrieve all ambulance IDs
        List<Ambulance> ambulances = ambulanceService.getAllAmbulances();

        // Create a map to store total accepted requests for each ambulance
        Map<UUID, Integer> totalAcceptedRequestsMap = new HashMap<>();

        // Populate the map with ambulance ID and total accepted requests
        for (Ambulance ambulance : ambulances) {
            int totalAcceptedRequests = requestRepository.countAcceptedRequestsForAmbulanceInCurrentMonth(ambulance.getId());
            totalAcceptedRequestsMap.put(ambulance.id, totalAcceptedRequests);
        }

        // Sort the map by value (total accepted requests) in descending order
        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(totalAcceptedRequestsMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Find the index of the current user's ambulance ID in the sorted list
        int rank = -1;
        for (int i = 0; i < sortedEntries.size(); i++) {
            if (sortedEntries.get(i).getKey().equals(currentUserId)) {
                rank = i + 1; // Rank is 1-based index
                break;
            }
        }

        return rank;
    }




    public Map<Month, Integer> getRequestsPerMonth(UUID ambulanceId) {
        Map<Month, Integer> requestsPerMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            LocalDateTime startOfMonth = LocalDateTime.now().withMonth(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth().length(startOfMonth.toLocalDate().isLeapYear())).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            int count = requestRepository.countRequestsForAmbulanceInTimeRange(ambulanceId, startOfMonth, endOfMonth);
            requestsPerMonth.put(startOfMonth.getMonth(), count);
        }

        return requestsPerMonth;
    }
    public Map<Month, Integer> getPatientsPerMonth(UUID ambulanceId) {
        Map<Month, Integer> patientsPerMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            LocalDateTime startOfMonth = LocalDateTime.now().withMonth(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth().length(startOfMonth.toLocalDate().isLeapYear())).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            int count = requestRepository.countUniquePatientsForAmbulanceInTimeRange(ambulanceId, startOfMonth, endOfMonth);
            patientsPerMonth.put(Month.of(i), count);
        }

        return patientsPerMonth;
    }
    public Map<Month, Paramedic> findTopParamedicWithMostRequestsForEachMonthForAmbulance(UUID ambulanceId) {
        Map<Month, Paramedic> topParamedicsByMonth = new TreeMap<>();

        List<Object[]> results = requestRepository.findTopParamedicWithMostRequestsForEachMonthForAmbulance(ambulanceId);

        for (Object[] result : results) {
            Paramedic paramedic = (Paramedic) result[0];
            Integer monthValue = (Integer) result[1];
            Month month = Month.of(monthValue);

            topParamedicsByMonth.put(month, paramedic);
        }

        return topParamedicsByMonth;
    }


}

