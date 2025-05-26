package com.bantvegas.dietnyplan.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DietRequest {

    @NotBlank(message = "Meno je povinné")
    private String name;

    @Min(value = 15, message = "Vek musí byť minimálne 15")
    @Max(value = 99, message = "Vek nesmie presiahnuť 99")
    private int age;

    @NotBlank(message = "Pohlavie je povinné")
    private String gender;

    @Min(value = 40, message = "Váha musí byť minimálne 40 kg")
    @Max(value = 250, message = "Váha nesmie presiahnuť 250 kg")
    private double weight;

    @Min(value = 130, message = "Výška musí byť minimálne 130 cm")
    @Max(value = 250, message = "Výška nesmie presiahnuť 250 cm")
    private double height;

    @NotBlank(message = "Cieľ je povinný")
    private String goal;

    private String preferences;  // Voliteľné
    private String allergies;   // Voliteľné

    @Email(message = "Email musí byť platný")
    @NotBlank(message = "Email je povinný")
    private String email;
}
