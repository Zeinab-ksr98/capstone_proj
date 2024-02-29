package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public Address save(Address address){
        return addressRepository.save(address);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }
    public void deleteAddress(Address address) {
        addressRepository.delete(address);
    }
}

