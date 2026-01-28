package com.example.neetflex.controller;

import com.example.neetflex.dto.RequestDetailsForSubscription;
import com.example.neetflex.dto.response.RequestDetails;
import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.patterns.FactoryMethodImpl.VideoPlayerFactory;
import com.example.neetflex.patterns.bridge.abstraction.FamilySubscriptionManager;
import com.example.neetflex.patterns.bridge.abstraction.PremiumSubscriptionManager;
import com.example.neetflex.patterns.bridge.abstraction.StandardSubscriptionManager;
import com.example.neetflex.patterns.bridge.abstraction.SubscriptionManager;
import com.example.neetflex.patterns.bridge.impl.BasicSubscriptionHandler;
import com.example.neetflex.patterns.bridge.impl.FamilySubscriptionHandler;
import com.example.neetflex.patterns.bridge.impl.ISubscriptionHandler;
import com.example.neetflex.patterns.bridge.impl.PremiumSubscriptionHandler;
import com.example.neetflex.service.impl.SubscriptionService;
import jakarta.persistence.Basic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/subscribe")
    public void subscribe(@RequestBody RequestDetailsForSubscription request) {

        ISubscriptionHandler handler = null;
        SubscriptionManager subscriptionManager = null;

        switch (request.getSubType())
        {
            case Basic -> {
                handler = new BasicSubscriptionHandler();
                subscriptionManager = new StandardSubscriptionManager(handler);
                break;
            }
            case Premium -> {
                handler = new PremiumSubscriptionHandler() ;
                subscriptionManager = new PremiumSubscriptionManager(handler);
                break;
            }
            case Family -> {
                handler = new FamilySubscriptionHandler() ;
                subscriptionManager = new FamilySubscriptionManager(handler);
                break;
            }

            default -> throw new IllegalArgumentException("Invalid subType");



        }

        subscriptionManager.subscribe(request.getUsername(), request.getSubType(), subscriptionService);




    }

    @GetMapping("/isUser/subscribed/")
    public boolean verifySubscription(@RequestParam String username) {
        return subscriptionService.verifySubscription(username);
    }

}
