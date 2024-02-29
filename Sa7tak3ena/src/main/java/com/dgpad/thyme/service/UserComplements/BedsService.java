package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.Beds;
import com.dgpad.thyme.model.usercomplements.HSections;
import com.dgpad.thyme.repository.BedsRepository;
import com.dgpad.thyme.repository.HSectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BedsService {
    @Autowired
    private BedsRepository bedsRepository;

    public Beds save(Beds bed){
        return bedsRepository.save(bed);
    }
    public Beds getBedById(int id) {
        return bedsRepository.findById(id).orElse(null);
    }
    public List<Beds> getAllBeds(){
        return bedsRepository.findAll();
    }

//    public void deleteBed(int id) {
//        bedsRepository.deleteById(id);
//    }
}

