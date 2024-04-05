package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.repository.RequestRepository;
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
    //    hospitals shull accept within 10min
    @Scheduled(cron = "0 */10 * * * *") // Executes every 10 minutes
    public void updateOldPendingRequests() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minus(10, ChronoUnit.MINUTES);
        List<Request> pendingRequests = requestRepository.findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, tenMinutesAgo);

        for (Request request : pendingRequests) {
            request.setStatus(ReservationStatus.Deleted);
            requestRepository.save(request);
        }
    }
    //    patient shall confirm within 30 min ( after  about 40 min of creation)
// Scheduled task to delete accepted requests older than 40 minutes
    @Scheduled(fixedRate = 2400000) // Executes every 40 minutes
    public void deleteOldAcceptedRequests() {
        LocalDateTime fortyMinutesAgo = LocalDateTime.now().minus(40, ChronoUnit.MINUTES);
        List<Request> acceptedRequests = requestRepository.findByStatusAndCreatedAtBefore(ReservationStatus.ACCEPTED, fortyMinutesAgo);

        for (Request request : acceptedRequests) {
            request.setStatus(ReservationStatus.Deleted);
            requestRepository.save(request);

        }
    }


}

