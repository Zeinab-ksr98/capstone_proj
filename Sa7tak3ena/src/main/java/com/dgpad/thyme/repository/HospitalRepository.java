package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.usercomplements.BedCategory;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {
    @Query("SELECT DISTINCT h FROM Hospital h JOIN h.availableBeds b WHERE b.category.name = :categoryName AND b.nbBeds > 0")
    List<Hospital> findHospitalsWithAvailableCategoryBeds(@Param("categoryName") String categoryName);
    @Query("SELECT b.nbBeds FROM Hospital h JOIN h.availableBeds b WHERE h.id = :hospitalId and b.category.name= :categoryName")
    Integer getBedCountForCategoryinHospital(@Param("hospitalId") UUID hospitalId,@Param("categoryName") String categoryName);
    @Query("SELECT  h FROM Hospital h  WHERE h.enabled=true and  h.address.latitude!=null and h.address.longitude!=null")
    List<Hospital> getAllEnabledHospitals();
    @Query("SELECT h FROM Hospital h JOIN FETCH h.reservations r WHERE h.enabled = true AND h.address.latitude IS NOT NULL AND h.address.longitude IS NOT NULL ORDER BY SIZE(h.reservations) DESC")
    List<Hospital> getAllHospitalsSortedByReservationSize();
    @Query("SELECT DISTINCT r.patient FROM Hospital h JOIN h.reservations r WHERE h.id = :hospital")
    List<Patient> findPatientsByHospital(@Param("hospital") UUID hospital);
    @Query("SELECT bc FROM BedCategory bc " +
            "WHERE bc.id NOT IN " +
            "(SELECT b.category.id FROM Hospital h JOIN h.availableBeds b WHERE h.id = :hospitalId)")
    List<BedCategory> findBedCategoriesNotInHospital(@Param("hospitalId") UUID hospitalId);

}