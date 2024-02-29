package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.requests.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT o FROM Reservation o WHERE o.hospital.id =?1 or o.patient.id=?1")
    List<Reservation> findAllReservationsForUser(@Param("userId") UUID userID);
}