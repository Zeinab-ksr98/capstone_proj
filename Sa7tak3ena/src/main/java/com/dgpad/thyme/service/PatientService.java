package com.dgpad.thyme.service;


import com.dgpad.thyme.Whatsapp.PatientDTO;
import com.dgpad.thyme.Whatsapp.VerificationCodeRepository;
import com.dgpad.thyme.Whatsapp.VerificationCodes;
import com.dgpad.thyme.Whatsapp.VerificationSender;
import com.dgpad.thyme.model.enums.Gender;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.PatientRepository;
import com.dgpad.thyme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private VerificationSender verificationSender;
    public Patient save(Patient user){
        return patientRepository.save(user);
    }
    public PatientDTO savePatient(String number, String password) throws Exception {
        String code =generateRandomString(8);

        VerificationCodes verificationCode = new VerificationCodes(number,code);
        verificationCode = verificationCodeRepository.save(verificationCode);

        boolean sent = verificationSender.sendVerificationCode(number, code);
        if(!sent){
            verificationCodeRepository.delete(verificationCode);
            throw new Exception("Failed to send the verification code!");
        }

        Patient user = new Patient(number, password, false);
        user = patientRepository.save(user);

        return new PatientDTO(user.getId().toString(), user.getPhone(), user.isVerifiedPhone());
    }
    public PatientDTO verifyUser(String number, String code) throws Exception {
        Patient user = patientRepository.findPatientByNumber(number).orElse(null);
        if(user == null)
            throw new Exception("failed to find user with the supported number");

        VerificationCodes verificationCode = verificationCodeRepository.findCodeByNumber(number).orElse(null);
        if(verificationCode == null)
            throw new Exception("Number has no code");
        if(!code.equals(verificationCode.getCode()))
            throw new Exception("Failed to verify the number the code is incorrect");
        user.setVerifiedPhone(true);
        user = patientRepository.save(user);
        return new PatientDTO(user.getId().toString(), user.getPhone(), user.isVerifiedPhone());
    }
    public List<PatientDTO> getUsers(){
        List<Patient> users = patientRepository.findAll();
        List<PatientDTO> userDTOS = new ArrayList<>();
        for(Patient user : users)
            userDTOS.add(new PatientDTO(user.getId().toString(),user.getPhone(), user.isVerifiedPhone()));
        return userDTOS;
    }
    public static String generateRandomString(int length) {
        // Define the character set from which to generate the random string
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a StringBuilder to build the random string
        StringBuilder randomStringBuilder = new StringBuilder();

        // Create an instance of the Random class
        Random random = new Random();

        // Generate the random string by appending random characters from the charset
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            char randomChar = charset.charAt(randomIndex);
            randomStringBuilder.append(randomChar);
        }

        // Convert StringBuilder to String and return
        return randomStringBuilder.toString();
    }
    public Patient update(Patient currentuser,Patient user){
        if (user.getFirstName()!=null)
            currentuser.setFirstName(user.getFirstName());
        if (user.getLastName()!=null)
            currentuser.setLastName(user.getLastName());
        if (user.getIdentityCardImage()!=null)
            currentuser.setIdentityCardImage(user.getIdentityCardImage());
        if (user.getSelfie()!=null)
            currentuser.setSelfie(user.getSelfie());
        if (user.getAge()!=0)
            currentuser.setAge(user.getAge());
        if (user.isVerified())
            currentuser.setVerified(user.isVerified());
        if (user.getGender()!=null)
            currentuser.setGender(user.getGender());
        if (user.getNationality()!=null)
            currentuser.setNationality(user.getNationality());
        if (user.getInsurance()!=null)
            currentuser.setInsurance(user.getInsurance());
        if (user.getUsername()!=null)
            currentuser.setUsername(user.getUsername());
        if (user.getEmail()!=null)
            currentuser.setEmail(user.getEmail());
        if (user.getPassword()!=null)
            currentuser.setPassword(user.getPassword());
        if (user.getPhone()!=null)
            currentuser.setPhone(user.getPhone());
        return save(currentuser);
    }

    public Patient createUser(Patient user){
        Patient newUser = new Patient(user.getUsername(),user.getFirstName(), user.getLastName(), user.getEmail(), passwordEncoder.encode(user.getPassword()), user.getPhone(), user.getGender());
        return save(newUser);
    }
    public Patient getPatientById(UUID id){
        return patientRepository.findById(id).orElse(null);
    }
    public void statusRequest(long id, UUID userId, ReservationStatus status) {
        Patient patient = getPatientById(userId);
        for (Request request : patient.getRequests()) {
            // Check if the id of the request matches the provided id
            if (request.getId() == id) {
                request.setStatus(status);
                break;
            }
        }
        save(patient);
    }

}