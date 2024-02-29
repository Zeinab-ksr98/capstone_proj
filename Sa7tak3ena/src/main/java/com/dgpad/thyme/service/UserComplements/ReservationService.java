package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.repository.AddressRepository;
import com.dgpad.thyme.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation save(Reservation reservation){
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
    public void deleteReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
    public List<Reservation> getAllReservationForCustomer(UUID userId){
        return reservationRepository.findAllReservationsForUser(userId);
    }
}

