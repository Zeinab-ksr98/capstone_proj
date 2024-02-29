package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AmbulanceTypeRepository extends JpaRepository<AmbulanceCar, Long> {


}