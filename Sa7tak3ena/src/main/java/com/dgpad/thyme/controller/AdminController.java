package com.dgpad.thyme.controller;

import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.AmbulanceAgency;
import com.dgpad.thyme.model.usercomplements.BedCategory;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.AccountRequestRepository;
import com.dgpad.thyme.service.AmbulanceService;
import com.dgpad.thyme.service.HospitalService;
import com.dgpad.thyme.service.PatientService;
import com.dgpad.thyme.service.UserComplements.*;
import com.dgpad.thyme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AmbulanceAgencyService ambulanceAgencyService;
    @Autowired
    private BedCategoryService bedCategoryService;
    @Autowired
    private BedCategoryRequestService bedCategoryRequestService;
    @Autowired
    private AccountRequestService accountRequestService;
    @GetMapping("/manage-users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageusers(Model model) {
        model.addAttribute("userrole", userService.getCurrentUser().getRole().toString());
        model.addAttribute("users", userService.getAllUsersNotBlocked());
        model.addAttribute("agencies", ambulanceAgencyService.getAllAgencies());

        return "Admin/manage-users";
    }
    @PostMapping(value = "/admin-create-withRole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String createAdmin(@RequestParam("username") String userName,
                              @RequestParam(value = "publicname", required = false) String publicName,
                              @RequestParam("pass") String pass,
                              @RequestParam("administrator") boolean administrator,
                              @RequestParam("email") String email,
                              @RequestParam("phone") String phone,
                              @RequestParam("role") Role role,
                              @RequestParam(value = "agency", required = false) AmbulanceAgency agency ,Model model) {
        if (role == Role.AMBULANCE && (publicName == null || agency == null)) {
            model.addAttribute("error", "Public Name and Agency are required for Ambulance role.");
            return "error-page";
        }
        Role selectedRole = Role.valueOf(role.name());
    if (selectedRole == Role.HOSPITAL) {
        Hospital hospital = new Hospital(userName, publicName, email, passwordEncoder.encode(pass), phone,administrator);
        hospitalService.save(hospital);
    }
    else if (selectedRole == Role.ADMIN) {
        User admin = new User(userName, email, passwordEncoder.encode(pass), phone,Role.ADMIN,administrator);
        userService.save(admin);
    }
    else if (selectedRole == Role.AMBULANCE) {
        Ambulance ambulance = new Ambulance(userName, publicName, email, passwordEncoder.encode(pass), phone,administrator);
        ambulance.setAgency(agency);
        ambulanceService.save(ambulance);
    }
    return "redirect:/manage-users";
}

//agency
    @GetMapping("/manage-agency")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageAgencies(Model model) {
        model.addAttribute("userrole",userService.getCurrentUser().getRole().toString());
        model.addAttribute("agencies",ambulanceAgencyService.getAllAgencies());
        return "Admin/manage-agencies";
    }
    @PostMapping("/add-agency")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String addAgency(@RequestParam("agencyname") String agencyName) {
        AmbulanceAgency agency = new AmbulanceAgency();
        agency.setName(agencyName);
        ambulanceAgencyService.save(agency);
        return "redirect:/manage-agency";
    }
    @PostMapping("/update-agency")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String updateCategory(@RequestParam("id") int id, @RequestParam("updatedName") String name){

        AmbulanceAgency agency = ambulanceAgencyService.getAmbulanceAgencyId(id);
        agency.setName(name);
        ambulanceAgencyService.save(agency);
        return "redirect:/manage-agency";
    }

    //bed category management (by admin)
    @GetMapping("/manage-bedsCategory")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageCategory(Model model) {
        model.addAttribute("categories",bedCategoryService.getAllBedCategories());
        return "Admin/manage-bedcategory";
    }
    @PostMapping("/add-bedsCategory")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String addbedsCategory(@RequestParam("name") String name) {
        BedCategory bedcat = new BedCategory() ;
        bedcat.setName(name);
        bedCategoryService.save(bedcat);
        return "redirect:/manage-bedsCategory";
    }
    @PostMapping("/update-bedsCategory")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String updatebedsCategory(@RequestParam("id") Long id,@RequestParam("updatename") String name){
        BedCategory bedcat = bedCategoryService.getBedCategoryById(id);
        bedcat.setName(name);
        bedCategoryService.save(bedcat);
        return "redirect:/manage-bedsCategory";
    }
    @GetMapping("/delete-bedsCategory/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deletebedsCategory(@PathVariable Long id) {
        bedCategoryService.deleteBedCategory(id);
        return "redirect:/manage-bedsCategory";
    }
//   view requests
    @GetMapping("/Requests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String manageRequests(Model model) {
        model.addAttribute("requestedaccounts",accountRequestService.getAllRequest());
        model.addAttribute("requestedcategories",bedCategoryRequestService.getAllRequestBedCategories());
        return "Admin/requests";
    }
    //delete request of creating new category
    @GetMapping("/delete-CategoryRequest/{ID}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deleteRequestedBedCategory(@PathVariable int ID) {
        bedCategoryRequestService.deleteCat(ID);
        return "redirect:/Requests";
    }

    //    delete account creation request
    @GetMapping("/delete-AccountRequest/{ID}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deleteAccountRequested(@PathVariable int ID) {
        accountRequestService.deleteRequest(ID);
        return "redirect:/Requests";
    }
}