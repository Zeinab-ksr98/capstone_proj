package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void statusRequest(long id,ReservationStatus status) {
        Request r =getRequestById(id);
        r.setStatus(status);
        save(r);
    }
}

