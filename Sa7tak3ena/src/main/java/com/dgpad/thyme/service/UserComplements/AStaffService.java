package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.Paramedic;
import com.dgpad.thyme.repository.AStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AStaffService {
    @Autowired
    private AStaffRepository staffRepository;

    public Paramedic save(Paramedic hs){
        return staffRepository.save(hs);
    }
    public Paramedic getStaffById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }
    public List<Paramedic> getAllAStaff(){
        return staffRepository.findAll();
    }

    public void deleteStaff(long id) {
        staffRepository.deleteById(id);
    }
}

