package com.example.neetflex.service.impl;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.model.user.Subscription;
import com.example.neetflex.model.user.User;
import com.example.neetflex.repositories.SubscriptionRepository;
import com.example.neetflex.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service

public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

@Autowired
    public SubscriptionService(UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public void subscribeUser(String username, SubscriptionType subType, Double price) {

        Optional<User> user = userRepository.findByUsername(username);
        boolean userSubscribed = subscriptionRepository.existsByUserId(user.get().getId());

        String subscriptionType = subType.toString();
        LocalDate now = LocalDate.now();

        if(user.isEmpty()) {
            return;
        }

        if(userSubscribed) {
            System.out.println("User already subscribed");
            return;
        }




        Subscription subscription = new Subscription();
        subscription.setUser(user.get());
        subscription.setActive(true);
        subscription.setStartDate(now);
        subscription.setEndDate(now.plusDays(30));
        subscription.setPrice(price);
        subscription.setType(subscriptionType);

        try {
            subscriptionRepository.save(subscription);
            System.out.println("Subscription saved");
        }
        catch (Exception e) {
            System.out.println("Subscription could not be saved" + e.getMessage());
        }


    }

    public boolean verifySubscription(String username) {

    Optional<User> user = userRepository.findByUsername(username);
    return subscriptionRepository.existsByUserId(user.get().getId());

    }
}
