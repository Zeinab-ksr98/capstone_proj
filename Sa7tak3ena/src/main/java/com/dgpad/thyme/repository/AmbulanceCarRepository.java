package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface AmbulanceCarRepository extends JpaRepository<AmbulanceCar, Integer> {
    @Query("SELECT ac FROM AmbulanceCar ac WHERE ac.status = ?1 and ac.ambulance.id = ?2")
    List<AmbulanceCar> findAllUserCarByStatus(AmbulanceStatus status, UUID ambulanceId);
    @Query("SELECT ac FROM AmbulanceCar ac WHERE ac.status = ?1 and ac.ambulance.id = ?2 and ac.type=?3")
    List<AmbulanceCar> findAllUserTypedCarsByStatus(AmbulanceStatus status, UUID ambulanceId, Ambulancetypes type);
}