package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.repository.AmbulanceRequestRepository;
import com.dgpad.thyme.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class AmbulanceRequestService {
    @Autowired
    private AmbulanceRequestRepository requestRepository;

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
}

