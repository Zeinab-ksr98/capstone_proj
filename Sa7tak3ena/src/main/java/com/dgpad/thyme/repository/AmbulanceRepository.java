package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.users.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AmbulanceRepository extends JpaRepository<Ambulance, UUID> {
    @Query("SELECT p FROM Ambulance p WHERE  p.enabled=true and p.Agency =?1")
    List<Ambulance> findAllAmbulanceForAgency(@Param("agencyId") AmbulanceAgency agency);
    @Query("SELECT p FROM Ambulance p WHERE  p.enabled=true and p.address!=null")
    List<Ambulance> findAllCompleted();
    @Query("SELECT DISTINCT a FROM Ambulance a JOIN a.ambulanceCars ac WHERE a.enabled = true and a.address!=null and a.Agency = :agency AND ac.type = :type AND ac.status = com.dgpad.thyme.model.enums.AmbulanceStatus.Available")
    List<Ambulance> getAmbulanceByAvailableCarTypeAndAgency(@Param("agency") AmbulanceAgency agency, @Param("type") Ambulancetypes type);


}