package com.dgpad.thyme.repository;

import com.dgpad.thyme.model.usercomplements.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {


}