package com.example.neetflex.model.user;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;
@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", allocationSize = 1)
    private Long id;
    private String type;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
