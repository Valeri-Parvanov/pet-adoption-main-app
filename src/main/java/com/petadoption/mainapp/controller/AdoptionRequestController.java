package com.petadoption.mainapp.controller;

import com.petadoption.mainapp.config.UserPrincipal;
import com.petadoption.mainapp.dto.AdoptionRequestRequest;
import com.petadoption.mainapp.entity.AdoptionRequest;
import com.petadoption.mainapp.entity.Pet;
import com.petadoption.mainapp.service.AdoptionRequestService;
import com.petadoption.mainapp.service.PetService;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;
    private final PetService petService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService, PetService petService) {
        this.adoptionRequestService = adoptionRequestService;
        this.petService = petService;
    }

    @GetMapping("/pets/{petId}/requests/new")
    public String showCreateForm(@PathVariable UUID petId, Model model) {
        model.addAttribute("pet", petService.getById(petId));
        model.addAttribute("adoptionRequestRequest", new AdoptionRequestRequest());
        return "requests/form";
    }

    @PostMapping("/pets/{petId}/requests")
    public String create(@PathVariable UUID petId,
                          @Valid @ModelAttribute("adoptionRequestRequest") AdoptionRequestRequest requestForm,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserPrincipal currentUser,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pet", petService.getById(petId));
            return "requests/form";
        }

        adoptionRequestService.create(petId, currentUser.getId(), requestForm.getMessage());

        return "redirect:/requests/my";
    }

    @GetMapping("/requests/my")
    public String myRequests(@AuthenticationPrincipal UserPrincipal currentUser, Model model) {
        model.addAttribute("requests", adoptionRequestService.getByRequesterId(currentUser.getId()));
        return "requests/my";
    }

    @GetMapping("/requests/incoming")
    public String incomingRequests(@AuthenticationPrincipal UserPrincipal currentUser, Model model) {
        List<AdoptionRequest> incoming = adoptionRequestService.getAll().stream()
                .filter(request -> belongsToOwner(request, currentUser.getId()))
                .collect(Collectors.toList());
        model.addAttribute("requests", incoming);
        return "requests/incoming";
    }

    @PostMapping("/requests/{id}/approve")
    public String approve(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        AdoptionRequest request = adoptionRequestService.getById(id);
        if (!belongsToOwner(request, currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can approve this request");
        }
        adoptionRequestService.approve(id);
        return "redirect:/requests/incoming";
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        AdoptionRequest request = adoptionRequestService.getById(id);
        if (!belongsToOwner(request, currentUser.getId())) {
            throw new IllegalStateException("Only the shelter owner can reject this request");
        }
        adoptionRequestService.reject(id);
        return "redirect:/requests/incoming";
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancel(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        AdoptionRequest request = adoptionRequestService.getById(id);
        if (!request.getRequesterId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only the requester can cancel this request");
        }
        adoptionRequestService.cancel(id);
        return "redirect:/requests/my";
    }

    private boolean belongsToOwner(AdoptionRequest request, UUID ownerId) {
        Pet pet = petService.getById(request.getPetId());
        return pet.getShelter().getOwnerId().equals(ownerId);
    }
}
