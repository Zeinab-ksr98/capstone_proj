package com.dgpad.thyme.model.users;

import com.dgpad.thyme.model.requests.Request;
import com.dgpad.thyme.model.Reservation;
import com.dgpad.thyme.model.enums.Role;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.dgpad.thyme.model.usercomplements.Feedback;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.MappedSuperclass;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false, unique = true)
    private String username;

    @Pattern(regexp = "^[0-9]*$", message = "Phone must contain only digits")
    @Size(min = 3, message = "Phone number must be at least 3 digits")
    private String phone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    @Size(min = 3, message = "Password must be at least 6 characters")
    public String password;

    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @OneToOne
    private Feedback feedback;
    public boolean deleted;
    private boolean enabled;

//    mainly used for knowing the responsible one
    private boolean administrator;
    public User(String username, String email, String password,String phone,Role role, boolean administrator) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone=phone;
        this.role = role;
        this.enabled = true;
        this.deleted = false;
        this.administrator=administrator;
    }
}
//admin is a role that does not have table, but he can do any task he has an authority for it
