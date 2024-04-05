package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.enums.ReservationType;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.repository.AddressRepository;
import com.dgpad.thyme.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BedsService bedsService;

    public Reservation save(Reservation reservation){
        return reservationRepository.save(reservation);
    }
    public Reservation reserveEmergency (Hospital hospital, AmbulanceCar car,String description ){
        Reservation hr=new Reservation();
        hr.setReservationType(ReservationType.EMERGENCY);
        Beds b = bedsService.findemergencybedidbyhospital(hospital.id);
        b.setNbBeds(b.getNbBeds()-1);
        hr.setHospital(hospital);
        hr.setAmbulanceCar(car);
        hr.setStatus(ReservationStatus.RESERVED);
        hr.setCreatedAt(LocalDateTime.now());
        hr.setMedicalRecord(description);
        return save(hr);
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
    public List<Reservation> getAllReservationForCustomerWithin24hs(UUID userId){
        // Retrieve all reservations for the user
        List<Reservation> allReservations = getAllReservationForCustomer(userId);

        // Filter reservations within the last 24 hours
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<Reservation> reservationsWithin24Hours = allReservations.stream()
                .filter(reservation -> reservation.getCreatedAt().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());
        // Add both lists to the model
        return reservationsWithin24Hours;
    }
    public List<Reservation> getAllReservationForCustomerOutside24hs(UUID userId){
        // Retrieve all reservations for the user
        List<Reservation> allReservations = getAllReservationForCustomer(userId);
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        // Filter reservations outside 24 hours
        List<Reservation> reservationsOutside24Hours = allReservations.stream()
                .filter(reservation -> !reservation.getCreatedAt().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());

        // Add both lists to the model
        return reservationsOutside24Hours;
    }


}

