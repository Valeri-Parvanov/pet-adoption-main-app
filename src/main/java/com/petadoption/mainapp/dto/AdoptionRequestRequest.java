package com.petadoption.mainapp.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdoptionRequestRequest {

    @Size(max = 1000, message = "Message must be at most 1000 characters")
    private String message;
}
