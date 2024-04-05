package com.dgpad.thyme.service;


import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.AmbulanceRepository;
import com.dgpad.thyme.repository.HospitalRepository;
import com.dgpad.thyme.repository.PatientRepository;
import com.dgpad.thyme.repository.UserRepository;
import com.dgpad.thyme.security.UserInfoDetails;
import com.dgpad.thyme.service.UserComplements.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public Map<Role, Double> calculateRolePercentage(Role[] allRoles) {
        int totalUsers = getAllUsers().size();
        Map<Role, Double> rolePercentage = new HashMap<>();

        int hospitalCount = getAllUsersByRole(Role.HOSPITAL).size();
        int ambulanceCount = getAllUsersByRole(Role.AMBULANCE).size();
        int patientCount = getAllUsersByRole(Role.PATIENT).size();
        int adminCount = getAllUsersByRole(Role.ADMIN).size();

        rolePercentage.put(Role.HOSPITAL, ((double) hospitalCount / totalUsers) * 100); // Fixed division issue
        rolePercentage.put(Role.AMBULANCE, ((double) ambulanceCount / totalUsers) * 100); // Fixed division issue
        rolePercentage.put(Role.PATIENT, ((double) patientCount / totalUsers) * 100); // Fixed division issue
        rolePercentage.put(Role.ADMIN, ((double) adminCount / totalUsers) * 100); // Fixed division issue

        return rolePercentage;
    }
    public Optional<User> findUserByPhone(String phone){
        return userRepository.findUserByPhone(phone);
    }

    public Optional<User> findUserByUserName(String username){
        return userRepository.findUserByUserName(username);
    }
    public Optional<User> findUserByEmailAndUserName(String username,String email){
        return userRepository.findUserByEmailAndName(email,username);
    }
    public User save(User user){
        return userRepository.save(user);
    }
    public User update(User currentuser,User user){
        if(user.getUsername()!=null)
            currentuser.setUsername(user.getUsername());
        if (user.getEmail()!=null)
            currentuser.setEmail(user.getEmail());
        if (user.getPhone()!=null)
            currentuser.setPhone(user.getPhone());
        return save(currentuser);
    }
    public Boolean userNameExists(String username){ return userRepository.existsByUsername(username);}
    public Boolean userPhoneExists(String phone){ return userRepository.existsByPhone(phone);}

    public Optional<User> findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public Boolean userEmailExists(String email){ return userRepository.existsByEmail(email);}

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public List<User> getAllUsersNotBlocked(){
        return userRepository.findAllNotBlocked();
    }

    public User getUserById(UUID id){
        return userRepository.findById(id).orElse(null);
    }

    public User createAdmin(User user){
        User newUser = new User(user.getUsername(),user.getEmail(), passwordEncoder.encode(user.getPassword()), user.getPhone(),Role.ADMIN, user.isAdministrator());
        return save(newUser);
    }

    public User BlockUser(UUID id){
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> value.setDeleted(true));
        return user.orElse(null);
    }
    public User deActivateUser(UUID id){
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> value.setEnabled(false));
        return user.orElse(null);
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserInfoDetails) {
            UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();
            return userInfoDetails.getUser();
        }

        return null;
    }

    public User activate(UUID id){
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> value.setEnabled(true));
        return user.orElse(null);
    }

    public boolean validId(UserDetails userDetails, String id){
        UserInfoDetails userInfoDetails = (UserInfoDetails) userDetails;
        return userInfoDetails.getUserId().equals(id);
    }
    public class UnsupportedUserTypeException extends RuntimeException {
        public UnsupportedUserTypeException(String message) {
            super(message);
        }
    }
    public List<User> getAllUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }



}
