package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {


}