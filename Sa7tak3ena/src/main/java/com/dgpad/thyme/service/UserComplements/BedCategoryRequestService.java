package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.requests.RequestBedCategory;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.BedCategory;
import com.dgpad.thyme.repository.BedCategoryRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BedCategoryRequestService {
    @Autowired
    private BedCategoryRequestRepository bedCategoryRequestRepository;

    public RequestBedCategory save(RequestBedCategory cat){
        return bedCategoryRequestRepository.save(cat);
    }

    public RequestBedCategory getCatById(int id) {
        return bedCategoryRequestRepository.findById(id).orElse(null);
    }
    public void deleteCat(int id) {

        bedCategoryRequestRepository.delete(getCatById(id));
    }
    public List<RequestBedCategory> getAllRequestBedCategories(){
        return bedCategoryRequestRepository.findAll();
    }
}

