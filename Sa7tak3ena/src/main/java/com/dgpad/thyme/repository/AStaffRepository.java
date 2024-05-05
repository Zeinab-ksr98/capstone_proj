package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.usercomplements.Paramedic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;


public interface AStaffRepository extends JpaRepository<Paramedic, Long> {

}