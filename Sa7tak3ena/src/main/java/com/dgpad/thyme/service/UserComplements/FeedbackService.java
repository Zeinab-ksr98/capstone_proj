package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.enums.Role;
import com.dgpad.thyme.model.usercomplements.Feedback;
import com.dgpad.thyme.model.users.User;
import com.dgpad.thyme.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    // Method to calculate feedback ratings count
    public Map<String, Integer> calculateFeedbackCounts() {
        List<Feedback> feedbacks = getAllFeeds();
        Map<String, Integer> feedbackCount = new HashMap<>();

        for (Feedback feedback : feedbacks) {
            String rating = feedback.getRate().toString();
            feedbackCount.put(rating, feedbackCount.getOrDefault(rating, 0) + 1);
        }
        return feedbackCount;
    }
}

