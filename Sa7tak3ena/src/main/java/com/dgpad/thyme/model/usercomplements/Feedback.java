package com.dgpad.thyme.model.usercomplements;

import com.dgpad.thyme.model.enums.feedbackRating;
import com.dgpad.thyme.model.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Feedback {
    @Id
    @Column(name = "feed_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private feedbackRating rate;
    private  String improvement;
    @OneToOne
    private User user;
}
