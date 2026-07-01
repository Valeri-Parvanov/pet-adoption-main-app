package com.petadoption.mainapp.controller;

import com.petadoption.mainapp.config.UserPrincipal;
import com.petadoption.mainapp.dto.PetRequest;
import com.petadoption.mainapp.entity.Pet;
import com.petadoption.mainapp.entity.PetStatus;
import com.petadoption.mainapp.entity.PetSpecies;
import com.petadoption.mainapp.service.PetService;
import com.petadoption.mainapp.service.ShelterService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

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

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id,
                               @AuthenticationPrincipal UserPrincipal currentUser,
                               Model model) {
        Pet pet = petService.getById(id);
        if (!pet.getShelter().getOwnerId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can edit this pet");
        }

        PetRequest petRequest = new PetRequest();
        petRequest.setName(pet.getName());
        petRequest.setSpecies(pet.getSpecies());
        petRequest.setBreed(pet.getBreed());
        petRequest.setAge(pet.getAge());
        petRequest.setDescription(pet.getDescription());
        petRequest.setShelterId(pet.getShelter().getId());

        model.addAttribute("petRequest", petRequest);
        model.addAttribute("petId", id);
        model.addAttribute("shelters", shelterService.getAll());
        model.addAttribute("species", PetSpecies.values());
        return "pets/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                          @Valid @ModelAttribute("petRequest") PetRequest petRequest,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserPrincipal currentUser,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("petId", id);
            model.addAttribute("shelters", shelterService.getAll());
            model.addAttribute("species", PetSpecies.values());
            return "pets/edit";
        }

        Pet pet = petService.getById(id);
        if (!pet.getShelter().getOwnerId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can edit this pet");
        }

        Pet updatedData = new Pet();
        updatedData.setName(petRequest.getName());
        updatedData.setSpecies(petRequest.getSpecies());
        updatedData.setBreed(petRequest.getBreed());
        updatedData.setAge(petRequest.getAge());
        updatedData.setDescription(petRequest.getDescription());
        updatedData.setShelter(shelterService.getById(petRequest.getShelterId()));

        petService.update(id, updatedData);

        return "redirect:/pets";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id,
                          @AuthenticationPrincipal UserPrincipal currentUser) {
        Pet pet = petService.getById(id);
        if (!pet.getShelter().getOwnerId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can delete this pet");
        }
        petService.delete(id);
        return "redirect:/pets";
    }
}
