package com.petadoption.mainapp.service;

import com.petadoption.mainapp.entity.AdoptionRequest;
import com.petadoption.mainapp.entity.RequestStatus;
import com.petadoption.mainapp.exception.ResourceNotFoundException;
import com.petadoption.mainapp.repository.AdoptionRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AdoptionRequestService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetService petService;

    public AdoptionRequestService(AdoptionRequestRepository adoptionRequestRepository, PetService petService) {
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.petService = petService;
    }

    public AdoptionRequest create(UUID petId, UUID requesterId, String message) {
        petService.markPending(petId);

        AdoptionRequest request = new AdoptionRequest();
        request.setPetId(petId);
        request.setRequesterId(requesterId);
        request.setMessage(message);

        AdoptionRequest savedRequest = adoptionRequestRepository.save(request);
        log.info("Created adoption request {} for pet {}", savedRequest.getId(), petId);
        return savedRequest;
    }

    public AdoptionRequest getById(UUID id) {
        return adoptionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdoptionRequest", id));
    }

    public List<AdoptionRequest> getAll() {
        return adoptionRequestRepository.findAll();
    }

    public List<AdoptionRequest> getByPetId(UUID petId) {
        return adoptionRequestRepository.findByPetId(petId);
    }

    public List<AdoptionRequest> getByRequesterId(UUID requesterId) {
        return adoptionRequestRepository.findByRequesterId(requesterId);
    }

    public AdoptionRequest approve(UUID id) {
        AdoptionRequest request = getById(id);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request must be PENDING to approve, current status: " + request.getStatus());
        }
        petService.markAdopted(request.getPetId());
        request.setStatus(RequestStatus.APPROVED);
        request.setDecidedAt(LocalDateTime.now());
        log.info("Approved adoption request {}", id);
        return adoptionRequestRepository.save(request);
    }

    public AdoptionRequest reject(UUID id) {
        AdoptionRequest request = getById(id);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request must be PENDING to reject, current status: " + request.getStatus());
        }
        petService.markAvailable(request.getPetId());
        request.setStatus(RequestStatus.REJECTED);
        request.setDecidedAt(LocalDateTime.now());
        log.info("Rejected adoption request {}", id);
        return adoptionRequestRepository.save(request);
    }

    public AdoptionRequest cancel(UUID id) {
        AdoptionRequest request = getById(id);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request must be PENDING to cancel, current status: " + request.getStatus());
        }
        petService.markAvailable(request.getPetId());
        request.setStatus(RequestStatus.CANCELLED);
        request.setDecidedAt(LocalDateTime.now());
        log.info("Cancelled adoption request {}", id);
        return adoptionRequestRepository.save(request);
    }
}
