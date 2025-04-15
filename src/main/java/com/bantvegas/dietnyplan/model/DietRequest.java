package com.bantvegas.dietnyplan.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DietRequest {

    @NotBlank(message = "Meno je povinné")
    private String name;

    @Min(15)
    @Max(99)
    private int age;

    @NotBlank(message = "Pohlavie je povinné")
    private String gender;

    @Min(40)
    @Max(250)
    private double weight;

    @Min(130)
    @Max(250)
    private double height;

    @NotBlank(message = "Cieľ je povinný")
    private String goal;

    private String preferences;
    private String allergies;

    @Email(message = "Email musí byť platný")
    @NotBlank(message = "Email je povinný")
    private String email;
}