package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.usercomplements.Paramedic;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AStaffRepository extends JpaRepository<Paramedic, Long> {


}