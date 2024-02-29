package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.users.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AmbulanceRepository extends JpaRepository<Ambulance, UUID> {
    @Query("SELECT p FROM Ambulance p WHERE   p.Agency =?1")
    List<Ambulance> findAllAmbulanceForAgency(@Param("agencyId") AmbulanceAgency agency);

}