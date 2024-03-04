package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.usercomplements.Feedback;
import com.dgpad.thyme.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback save(Feedback feedback){
        return feedbackRepository.save(feedback);
    }
    public Feedback getFeedById(int id) {
        return feedbackRepository.findById(id).orElse(null);
    }
    public List<Feedback> getAllFeeds(){
        return feedbackRepository.findAll();
    }

    public void deleteFeed(int id) {
        feedbackRepository.deleteById(id);
    }
}

