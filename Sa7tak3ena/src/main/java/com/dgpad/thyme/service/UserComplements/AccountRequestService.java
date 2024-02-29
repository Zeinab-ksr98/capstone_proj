package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.requests.RequestBedCategory;
import com.dgpad.thyme.model.usercomplements.AccountRequest;
import com.dgpad.thyme.repository.AccountRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountRequestService {
    private final AccountRequestRepository accountRequestRepository;

    public AccountRequestService(AccountRequestRepository accountRequestRepository) {
        this.accountRequestRepository = accountRequestRepository;
    }

    public AccountRequest save(AccountRequest cat){
        return accountRequestRepository.save(cat);
    }

    public  AccountRequest getRequestById(int id) {
        return accountRequestRepository.findById(id).orElse(null);
    }
    public  void deleteRequest(int id) {
        accountRequestRepository.delete(getRequestById(id));
    }
    public  List<AccountRequest> getAllRequest(){
        return accountRequestRepository.findAll();
    }
}

