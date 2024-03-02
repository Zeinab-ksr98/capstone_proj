package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.Ambulanceservice;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.Paramedic;
import com.dgpad.thyme.repository.AmbulanceRequestRepository;
import com.dgpad.thyme.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
}

