package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query(value = "select * from user where phone = ?1", nativeQuery = true)
    Optional<Patient> findPatientByNumber(String phone);

}