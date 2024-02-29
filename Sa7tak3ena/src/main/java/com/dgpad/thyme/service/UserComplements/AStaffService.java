package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.HSections;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.repository.HSectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HSectionsService {
    @Autowired
    private HSectionsRepository hSectionsRepository;

    public HSections save(HSections hs){
        return hSectionsRepository.save(hs);
    }
    public HSections getHSectionById(Long id) {
        return hSectionsRepository.findById(id).orElse(null);
    }
    public List<HSections> getAllHSections(){
        return hSectionsRepository.findAll();
    }

    public void deleteSection(long id) {
        hSectionsRepository.deleteById(id);
    }
}

