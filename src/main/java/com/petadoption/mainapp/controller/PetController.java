package com.petadoption.mainapp.controller;

import com.petadoption.mainapp.dto.PetRequest;
import com.petadoption.mainapp.entity.Pet;
import com.petadoption.mainapp.entity.PetStatus;
import com.petadoption.mainapp.entity.PetSpecies;
import com.petadoption.mainapp.service.PetService;
import com.petadoption.mainapp.service.ShelterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final ShelterService shelterService;

    public PetController(PetService petService, ShelterService shelterService) {
        this.petService = petService;
        this.shelterService = shelterService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pets", petService.getByStatus(PetStatus.AVAILABLE));
        return "pets/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("petRequest", new PetRequest());
        model.addAttribute("shelters", shelterService.getAll());
        model.addAttribute("species", PetSpecies.values());
        return "pets/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("petRequest") PetRequest petRequest,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shelters", shelterService.getAll());
            model.addAttribute("species", PetSpecies.values());
            return "pets/form";
        }

        Pet pet = new Pet();
        pet.setName(petRequest.getName());
        pet.setSpecies(petRequest.getSpecies());
        pet.setBreed(petRequest.getBreed());
        pet.setAge(petRequest.getAge());
        pet.setDescription(petRequest.getDescription());
        pet.setShelter(shelterService.getById(petRequest.getShelterId()));

        petService.create(pet);

        return "redirect:/pets";
    }
}
