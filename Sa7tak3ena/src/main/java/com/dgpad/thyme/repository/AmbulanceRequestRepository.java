package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.enums.AmbulanceRequestStatus;
import com.dgpad.thyme.model.requests.AmbulanceRequest;
import com.dgpad.thyme.model.usercomplements.Paramedic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequest, Long> {
    @Query("SELECT o FROM AmbulanceRequest o WHERE o.hospital.id =?1 or o.ambulance.id=?1 or o.sender.id=?1")
    List<AmbulanceRequest> findAllRequestForUser(@Param("userId") UUID userID);
    @Query("SELECT o FROM AmbulanceRequest o WHERE o.status=?1")
    List<AmbulanceRequest> findAllRequestbystatus(@Param("status") AmbulanceRequestStatus status);
    @Query("SELECT ar FROM AmbulanceRequest ar WHERE (ar.sender.id=?1 or ar.ambulance.id=?1) and  ar.createdAt >=?2")
    List<AmbulanceRequest> findRequestsByStatusWithin24Hours(@Param("userId") UUID userID,@Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo);

    @Query("SELECT o FROM AmbulanceRequest o WHERE (o.sender.id =?1 or o.hospital.id=?1 or o.ambulance.id=?1) and o.status=?2")
    List<AmbulanceRequest> findAllRequestsForUserByStatus(@Param("userId") UUID userID, @Param("status") AmbulanceRequestStatus status);
    @Query("SELECT a FROM AmbulanceRequest a WHERE a.status = ?1 AND a.createdAt < ?2")
    List<AmbulanceRequest> findByStatusAndCreatedAtBefore(AmbulanceRequestStatus status, LocalDateTime timestamp);
    @Query("SELECT o FROM AmbulanceRequest o WHERE  o.status = com.dgpad.thyme.model.enums.AmbulanceRequestStatus.PENDING AND o.createdAt<:timestamp")
    List<AmbulanceRequest> findPendingCreatedAtBefore( LocalDateTime timestamp);

    // Count the number of unique patients for a given ambulance within a specified time range
    @Query("SELECT COUNT(DISTINCT o.sender.id) FROM AmbulanceRequest o WHERE o.ambulance.id = :ambulanceId AND o.createdAt BETWEEN :startDateTime AND :endDateTime")
    int countUniquePatientsForAmbulanceInTimeRange(@Param("ambulanceId") UUID ambulanceId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
    @Query("SELECT COUNT(o) FROM AmbulanceRequest o WHERE o.ambulance.id = :ambulanceId AND o.status = com.dgpad.thyme.model.enums.AmbulanceRequestStatus.ACCEPTED AND o.createdAt BETWEEN :startDateTime AND :endDateTime")
    int countAcceptedRequestsForAmbulanceInTimeRange(@Param("ambulanceId") UUID ambulanceId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
    @Query("SELECT COUNT(o) FROM AmbulanceRequest o WHERE o.ambulance.id = :ambulanceId " +
            "AND o.status = com.dgpad.thyme.model.enums.AmbulanceRequestStatus.ACCEPTED " +
            "AND FUNCTION('MONTH', o.createdAt) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', o.createdAt) = FUNCTION('YEAR', CURRENT_DATE)")
    int countAcceptedRequestsForAmbulanceInCurrentMonth(@Param("ambulanceId") UUID ambulanceId);

    // Count the number of ambulance requests for a given ambulance within a specified time range
    @Query("SELECT COUNT(o) FROM AmbulanceRequest o WHERE o.ambulance.id = :ambulanceId  AND o.createdAt  BETWEEN :startDateTime AND :endDateTime")
    int countRequestsForAmbulanceInTimeRange(@Param("ambulanceId") UUID ambulanceId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
    @Query("SELECT ar.staff, MONTH(ar.createdAt) AS month FROM AmbulanceRequest ar WHERE ar.ambulance.id = :ambulanceId GROUP BY ar.staff.id, MONTH(ar.createdAt) ORDER BY month , COUNT(ar) DESC")
    List<Object[]> findTopParamedicWithMostRequestsForEachMonthForAmbulance(@Param("ambulanceId") UUID ambulanceId);

}