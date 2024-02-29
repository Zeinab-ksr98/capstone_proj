package com.dgpad.thyme.controller;
import com.dgpad.thyme.model.enums.Gender;
import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.PatientService;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PatientService patientService;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/create-initial-admin")
    public String createInitialAdmin(){
        User user = new User("Admin","zeinabksr98@gmail.com","123","03010106",Role.ADMIN);
        userService.createAdmin(user);
        return "patient Successfully Created";

    }
    @GetMapping(value = "/create-initial-patient")
    public String createInitialPatient(){
        System.out.println("hi");
        Patient user = new Patient("zksr","z","ksr","pzksr@gmail.com","123","03010101",Gender.Female);
        Patient patient = patientService.createUser(user);
        System.out.println("done");
        return "patient Successfully Created";

    }
    @GetMapping(value = "/create-initial-hospital")
    public String createInitialHospital(){
        Hospital user = new Hospital("ZHUMC","Al Zahraa Hospital University Medical Center ","zksr@gmail.com","123","03010101");
        Hospital hospital = hospitalService.createUser(user);
        System.out.println("done");
        return "hospital Successfully Created";

    }
    @PostMapping(value = "/create")
    public String createUser(@ModelAttribute("newuser") Patient user, BindingResult bindingResult, Model model){
        if(userService.userNameExists(user.getUsername())){
            model.addAttribute("user",user);
            bindingResult.rejectValue("username", "error.user", "Username already exists");
            return "account/SignIn";
        }
        if(userService.userEmailExists(user.getEmail())){
            model.addAttribute("user",user);
            bindingResult.rejectValue("email", "error.user", "Email already exists");
            return "account/SignIn";
        }
        try {
            user.setRole(Role.PATIENT);
            patientService.createUser(user);
            return "redirect:/home";
        } catch (Exception e) {
            return "account/SignIn";
        }
    }
    @GetMapping(value = "/",  produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> getUsers(){
        try{
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(value = "/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("id") String id){
        try{
            return new ResponseEntity<>(userService.getUserById(UUID.fromString(id)), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(value = "/block/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String Block(@PathVariable("id") String id){
        userService.save(userService.BlockUser(UUID.fromString(id)));
        userService.save(userService.deActivateUser(UUID.fromString(id)));
        return "redirect:/manage-users";

    }
    @GetMapping(value = "/deactivate/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deActivateUser(@PathVariable("id") String id){
        userService.save(userService.deActivateUser(UUID.fromString(id)));
        return "redirect:/manage-users";

    }

    @GetMapping(value = "/activate/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String activateUser(@PathVariable("id") String id){
        userService.save(userService.activate(UUID.fromString(id)));
        return "redirect:/manage-users";
    }

}
