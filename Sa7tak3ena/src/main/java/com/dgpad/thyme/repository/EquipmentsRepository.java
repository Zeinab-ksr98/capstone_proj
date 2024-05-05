package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.usercomplements.Equipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentsRepository extends JpaRepository<Equipments, Integer> {

}