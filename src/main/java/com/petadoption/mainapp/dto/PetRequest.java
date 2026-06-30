package com.petadoption.mainapp.dto;

import com.petadoption.mainapp.entity.PetSpecies;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PetRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Species is required")
    private PetSpecies species;

    private String breed;

    @PositiveOrZero(message = "Age must be zero or positive")
    private Integer age;

    private String description;

    @NotNull(message = "Shelter is required")
    private UUID shelterId;
}
