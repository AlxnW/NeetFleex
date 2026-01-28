package com.example.neetflex.repositories;


import com.example.neetflex.model.user.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByUserId(Long userId);

}
