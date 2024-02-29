package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.BedCategory;
import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.repository.BedCategoryRepository;
import com.dgpad.thyme.repository.BedsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BedCategoryService {
    @Autowired
    private BedCategoryRepository bedCategoryRepository;

    public BedCategory save(BedCategory bed){
        return bedCategoryRepository.save(bed);
    }
    public BedCategory getBedCategoryById(Long id) {
        return bedCategoryRepository.findById(id).orElse(null);
    }
    public List<BedCategory> getAllBedCategories(){
        return bedCategoryRepository.findAll();
    }

    public void deleteBedCategory(long id) {
        bedCategoryRepository.deleteById(id);
    }
}

