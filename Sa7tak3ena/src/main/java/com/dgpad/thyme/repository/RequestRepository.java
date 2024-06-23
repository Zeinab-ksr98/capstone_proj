package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.requests.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT a FROM Request a WHERE a.status = com.dgpad.thyme.model.enums.ReservationStatus.ACCEPTED AND a.CreatedAt < :timestamp")
    List<Request> findAcceptedAndCreatedAtBefore(@Param("timestamp") LocalDateTime timestamp);
    @Query("SELECT o FROM Request o WHERE o.hospital.id =?1 or o.patient.id=?1")
    List<Request> findAllRequestsForUser(@Param("userId") UUID userID);
    @Query("SELECT o FROM Request o WHERE (o.patient.id=?1 or o.hospital.id=?1) and o.status=?2")
    List<Request> findAllRequestsForUserByStatus(@Param("userId") UUID userID, @Param("status")ReservationStatus status);
    @Query("SELECT o FROM Request o WHERE (o.patient.id=?1) and o.status!=?2")
    List<Request> findAllRequestsForUserExceptStatus(@Param("userId") UUID userID, @Param("status")ReservationStatus status);
    @Query("SELECT a FROM Request a WHERE a.status = com.dgpad.thyme.model.enums.ReservationStatus.PENDING AND a.CreatedAt < :timestamp")
    List<Request> findPendingAndCreatedAtBefore(@Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT ar FROM Request ar WHERE ar.patient.id=?1 or ar.hospital.id=?1  and  ar.CreatedAt >=?2")
    List<Request> findRequestsByStatusWithin24Hours(@Param("userId") UUID userID,@Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo);


}