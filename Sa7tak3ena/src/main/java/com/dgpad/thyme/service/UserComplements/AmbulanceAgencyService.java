package com.dgpad.thyme.service.UserComplements;

import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.repository.AmbulanceAgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmbulanceAgencyService {
    @Autowired
    private AmbulanceAgencyRepository ambulanceAgencyRepository;

    public AmbulanceAgency save(AmbulanceAgency agency){
        return ambulanceAgencyRepository.save(agency);
    }
    public List<AmbulanceAgency> getAllAgencies(){
        return ambulanceAgencyRepository.findAll();
    }

    public AmbulanceAgency getAmbulanceAgencyId(int id) {
        return ambulanceAgencyRepository.findById(id).orElse(null);
    }
    public void deleteAmbulanceAgency(int id) {

        ambulanceAgencyRepository.delete(getAmbulanceAgencyId(id));
    }
}
