package com.bantvegas.dietnyplan.model;

import lombok.Data;

@Data
public class DietRequest {
    private String name;
    private int age;
    private String gender;
    private double weight;
    private double height;
    private String goal;
    private String preferences;
    private String allergies;
    private String email;
}