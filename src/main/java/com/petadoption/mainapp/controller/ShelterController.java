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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
