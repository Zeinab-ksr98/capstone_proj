package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.enums.Distracts;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.Equipments;
import com.dgpad.thyme.model.users.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AmbulanceRepository extends JpaRepository<Ambulance, UUID> {
    @Query("SELECT e FROM Equipments e WHERE e.ambulance = :ambulance AND e.CreatedAt >= :oneMonthAgo")
    List<Equipments> getEquipmentsWithinMonthForAmbulance(@Param("ambulance") Ambulance ambulance, @Param("oneMonthAgo") LocalDateTime oneMonthAgo);

    @Query("SELECT p FROM Ambulance p WHERE  p.enabled=true and p.Agency =?1")
    List<Ambulance> findAllAmbulanceForAgency(@Param("agencyId") AmbulanceAgency agency);
    @Query("SELECT p FROM Ambulance p WHERE  p.enabled=true and p.Agency =?1 and p.enabled=true and p.address.latitude!=null and p.address.longitude!=null")
    List<Ambulance> findAllCompletedAmbulanceForAgency(@Param("agencyId") AmbulanceAgency agency);
    @Query("SELECT p FROM Ambulance p WHERE  p.enabled=true and p.address.latitude!=null and p.address.longitude!=null")
    List<Ambulance> findAllCompleted();
    @Query("SELECT p FROM Ambulance p WHERE p.address.REGION = :region and  p.enabled=true and p.address.REGION=: region and p.address.latitude!=null and p.address.longitude!=null")
    List<Ambulance> getAllCompletedAmbulancesinRegion( @Param("region") String region);
    @Query("SELECT DISTINCT a FROM Ambulance a JOIN a.ambulanceCars ac WHERE a.enabled = true and a.address.latitude!=null and a.address.longitude!=null and a.Agency = :agency AND ac.type = :type AND ac.status = com.dgpad.thyme.model.enums.AmbulanceStatus.Available")
    List<Ambulance> getAmbulanceByAvailableCarTypeAndAgency(@Param("agency") AmbulanceAgency agency, @Param("type") Ambulancetypes type);

    @Query("SELECT DISTINCT a FROM Ambulance a JOIN a.ambulanceCars ac WHERE a.enabled = true and a.address.latitude!=null and a.address.longitude!=null and  ac.type = :type AND ac.status = com.dgpad.thyme.model.enums.AmbulanceStatus.Available")
    List<Ambulance> getAmbulanceByAvailableCarType( @Param("type") Ambulancetypes type);
    @Query("SELECT DISTINCT a FROM Ambulance a JOIN a.ambulanceCars ac WHERE a.enabled = true and a.address.REGION = :regon and a.address.longitude != null and a.address.latitude != null and ac.type = :type AND ac.status = com.dgpad.thyme.model.enums.AmbulanceStatus.Available")
    List<Ambulance> getAmbulanceByAvailableCarTypeInRegion(@Param("type") Ambulancetypes type, @Param("regon") String regon);

}