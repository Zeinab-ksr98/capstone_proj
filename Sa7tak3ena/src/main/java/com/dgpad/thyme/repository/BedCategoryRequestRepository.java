package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.requests.RequestBedCategory;
import com.dgpad.thyme.model.usercomplements.Address;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BedCategoryRequestRepository extends JpaRepository<RequestBedCategory, Integer> {


}