package com.bantvegas.dietnyplan.entity;

import jakarta.persistence.*; // SPRÁVNY IMPORT pre Spring Boot 3.x
import lombok.Data;

@Entity
@Table(name = "diet_orders")
@Data
public class DietOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int age;

    @Column(nullable = false)
    private String gender;

    private double weight;

    private double height;

    @Column(nullable = false)
    private String goal;

    private String preferences;

    private String allergies;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String plan;

    private String token;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;
}
