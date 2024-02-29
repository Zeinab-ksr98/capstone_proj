package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequest, Long> {
    @Query("SELECT o FROM AmbulanceRequest o WHERE o.hospital.id =?1 or o.ambulance.id=?1")
    List<AmbulanceRequest> findAllRequestForUser(@Param("userId") UUID userID);

}