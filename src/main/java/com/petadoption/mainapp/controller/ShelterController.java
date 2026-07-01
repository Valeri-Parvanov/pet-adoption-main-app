package com.petadoption.mainapp.controller;

import com.petadoption.mainapp.config.UserPrincipal;
import com.petadoption.mainapp.dto.ShelterRequest;
import com.petadoption.mainapp.entity.Shelter;
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
@RequestMapping("/shelters")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("shelters", shelterService.getAll());
        return "shelters/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("shelterRequest", new ShelterRequest());
        return "shelters/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("shelterRequest") ShelterRequest shelterRequest,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserPrincipal currentUser) {
        if (bindingResult.hasErrors()) {
            return "shelters/form";
        }

        Shelter shelter = new Shelter();
        shelter.setName(shelterRequest.getName());
        shelter.setAddress(shelterRequest.getAddress());
        shelter.setPhone(shelterRequest.getPhone());
        shelter.setEmail(shelterRequest.getEmail());
        shelter.setOwnerId(currentUser.getId());

        shelterService.create(shelter);

        return "redirect:/shelters";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id,
                               @AuthenticationPrincipal UserPrincipal currentUser,
                               Model model) {
        Shelter shelter = shelterService.getById(id);
        if (!shelter.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can edit this shelter");
        }

        ShelterRequest shelterRequest = new ShelterRequest();
        shelterRequest.setName(shelter.getName());
        shelterRequest.setAddress(shelter.getAddress());
        shelterRequest.setPhone(shelter.getPhone());
        shelterRequest.setEmail(shelter.getEmail());

        model.addAttribute("shelterRequest", shelterRequest);
        model.addAttribute("shelterId", id);
        return "shelters/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                          @Valid @ModelAttribute("shelterRequest") ShelterRequest shelterRequest,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserPrincipal currentUser,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shelterId", id);
            return "shelters/edit";
        }

        Shelter shelter = shelterService.getById(id);
        if (!shelter.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can edit this shelter");
        }

        Shelter updatedData = new Shelter();
        updatedData.setName(shelterRequest.getName());
        updatedData.setAddress(shelterRequest.getAddress());
        updatedData.setPhone(shelterRequest.getPhone());
        updatedData.setEmail(shelterRequest.getEmail());

        shelterService.update(id, updatedData);

        return "redirect:/shelters";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id,
                          @AuthenticationPrincipal UserPrincipal currentUser) {
        Shelter shelter = shelterService.getById(id);
        if (!shelter.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can delete this shelter");
        }
        shelterService.delete(id);
        return "redirect:/shelters";
    }
}
