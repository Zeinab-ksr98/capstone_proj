package com.dgpad.thyme.Whatsapp;
import com.dgpad.thyme.Whatsapp.requests.AddUserRequest;
import com.dgpad.thyme.Whatsapp.requests.VerifyUserRequest;
import com.dgpad.thyme.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class  WhatsappController{
    private final PatientService patientService;

    @GetMapping(value = "/add-user")
    public ResponseEntity<?> addUser(@Valid @ModelAttribute AddUserRequest parameters, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors())
                return new ResponseEntity<>("Pass the correct parameters", HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok(patientService.savePatient(parameters.getNumber(), parameters.getNumber()));
        }catch (Exception e){
            return new ResponseEntity<>("Failed to create the user", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/verify-user")
    public ResponseEntity<?> verifyUer(@Valid @ModelAttribute VerifyUserRequest verifyUserRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors())
                return new ResponseEntity<>("Pass the correct parameters", HttpStatus.BAD_REQUEST);
            return ResponseEntity.ok(patientService.verifyUser(verifyUserRequest.getNumber(), verifyUserRequest.getCode()));
        }catch (Exception e){
            return new ResponseEntity<>("Failed to verify the user", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/users")
    public ResponseEntity<?> getAllUsers(){
        try {
            return ResponseEntity.ok(patientService.getUsers());
        }catch (Exception e){
            return new ResponseEntity<>("Failed to get the users List", HttpStatus.BAD_REQUEST);
        }
    }
}
