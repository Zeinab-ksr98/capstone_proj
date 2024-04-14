package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequest, Long> {
    @Query("SELECT o FROM AmbulanceRequest o WHERE o.hospital.id =?1 or o.ambulance.id=?1 or o.sender.id=?1")
    List<AmbulanceRequest> findAllRequestForUser(@Param("userId") UUID userID);
    @Query("SELECT o FROM AmbulanceRequest o WHERE (o.sender.id =?1 or o.hospital.id=?1 or o.ambulance.id=?1) and o.status=?2")
    List<AmbulanceRequest> findAllRequestsForUserByStatus(@Param("userId") UUID userID, @Param("status") AmbulanceRequestStatus status);
    @Query("SELECT a FROM AmbulanceRequest a WHERE a.status = ?1 AND a.createdAt < ?2")
    List<AmbulanceRequest> findByStatusAndCreatedAtBefore(AmbulanceRequestStatus status, LocalDateTime timestamp);

}