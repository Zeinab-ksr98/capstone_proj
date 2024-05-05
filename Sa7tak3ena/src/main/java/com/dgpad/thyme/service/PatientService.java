package com.dgpad.thyme.service;


import com.dgpad.thyme.Whatsapp.PatientDTO;
import com.dgpad.thyme.Whatsapp.VerificationCodeRepository;
import com.dgpad.thyme.Whatsapp.VerificationCodes;
import com.dgpad.thyme.Whatsapp.VerificationSender;
import com.dgpad.thyme.model.Image;
import com.dgpad.thyme.model.enums.Ambulancetypes;
import com.dgpad.thyme.model.enums.Gender;
import com.dgpad.thyme.model.enums.ReservationStatus;
import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.users.Patient;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.PatientRepository;
import com.dgpad.thyme.repository.UserRepository;
import com.dgpad.thyme.service.UserComplements.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Autowired
    private ImageService imageService;
    public Patient save(Patient user){
        return patientRepository.save(user);
    }

    public boolean verifyUser(String number, String code) throws Exception {
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
        return true;
    }
    public List<PatientDTO> getUsers(){
        List<Patient> users = patientRepository.findAll();
        List<PatientDTO> userDTOS = new ArrayList<>();
        for(Patient user : users)
            userDTOS.add(new PatientDTO(user.getId().toString(),user.getPhone(), user.getVerifiedPhone()));
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
    public Patient update(Patient currentuser,Patient user,List<MultipartFile> imageFile) throws IOException {

        if (user.getFirstName()!=null)
            currentuser.setFirstName(user.getFirstName());
        if (user.getLastName()!=null)
            currentuser.setLastName(user.getLastName());
        List<Image> images = imageService.saveImages(imageFile);
        currentuser.setIdentityCardImage(images.get(0));

        currentuser.setVerified(true);


        if (user.getAge()!=0)
            currentuser.setAge(user.getAge());
        if (user.getGender()!=null)
            currentuser.setGender(user.getGender());

        if (user.getNationality()!=null)
            currentuser.setNationality(user.getNationality());
        if (user.getInsurance()!=null)
            currentuser.setInsurance(user.getInsurance());

        return save(currentuser);
    }

    public Patient createUser(Patient user) throws Exception {
        Patient newUser = new Patient(user.getUsername(),user.getFirstName(), user.getLastName(), user.getEmail(), passwordEncoder.encode(user.getPassword()), user.getPhone(), user.getGender());
//        send verification random code
        String code =generateRandomString(8);

        VerificationCodes verificationCode = new VerificationCodes(user.getPhone(),code);
        verificationCode = verificationCodeRepository.save(verificationCode);

        //send whatsapp Sms
        String sms="Your Verification code: "+ code;
        boolean sent = verificationSender.sendVerificationCode(user.getPhone(),sms);
        if(!sent){
            verificationCodeRepository.delete(verificationCode);
            throw new Exception("Failed to send the verification code!");
        }
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
    public void acceptRequest(long id, UUID userId, Ambulancetypes type) {
        Patient patient = getPatientById(userId);
        for (Request request : patient.getRequests()) {
            // Check if the id of the request matches the provided id
            if (request.getId() == id) {
                request.setStatus(ReservationStatus.ACCEPTED);
                request.setCarType(type);
                break;
            }
        }
        save(patient);
    }

}