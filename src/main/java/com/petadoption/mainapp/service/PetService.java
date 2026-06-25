package com.petadoption.mainapp.service;

import com.petadoption.mainapp.entity.Pet;
import com.petadoption.mainapp.entity.PetStatus;
import com.petadoption.mainapp.exception.ResourceNotFoundException;
import com.petadoption.mainapp.repository.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet create(Pet pet) {
        Pet savedPet = petRepository.save(pet);
        log.info("Created pet: {}", savedPet.getName());
        return savedPet;
    }

    public Pet getById(UUID id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", id));
    }

    public List<Pet> getAll() {
        return petRepository.findAll();
    }

    public List<Pet> getByStatus(PetStatus status) {
        return petRepository.findByStatus(status);
    }

    public List<Pet> getByShelterId(UUID shelterId) {
        return petRepository.findByShelterId(shelterId);
    }

    public Pet update(UUID id, Pet updatedData) {
        Pet pet = getById(id);
        pet.setName(updatedData.getName());
        pet.setSpecies(updatedData.getSpecies());
        pet.setBreed(updatedData.getBreed());
        pet.setAge(updatedData.getAge());
        pet.setDescription(updatedData.getDescription());
        return petRepository.save(pet);
    }

    public void delete(UUID id) {
        Pet pet = getById(id);
        petRepository.delete(pet);
        log.info("Deleted pet: {}", pet.getName());
    }

    public Pet markPending(UUID id) {
        Pet pet = getById(id);
        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new IllegalStateException("Pet must be AVAILABLE to become PENDING, current status: " + pet.getStatus());
        }
        pet.setStatus(PetStatus.PENDING);
        return petRepository.save(pet);
    }

    public Pet markAdopted(UUID id) {
        Pet pet = getById(id);
        if (pet.getStatus() != PetStatus.PENDING) {
            throw new IllegalStateException("Pet must be PENDING to become ADOPTED, current status: " + pet.getStatus());
        }
        pet.setStatus(PetStatus.ADOPTED);
        return petRepository.save(pet);
    }

    public Pet markAvailable(UUID id) {
        Pet pet = getById(id);
        if (pet.getStatus() != PetStatus.PENDING) {
            throw new IllegalStateException("Pet must be PENDING to return to AVAILABLE, current status: " + pet.getStatus());
        }
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setAvailableSince(LocalDateTime.now());
        return petRepository.save(pet);
    }

    public List<Pet> getExpiredCandidates(LocalDateTime cutoff) {
        return petRepository.findByStatusAndAvailableSinceBefore(PetStatus.AVAILABLE, cutoff);
    }

    public Pet markExpired(UUID id) {
        Pet pet = getById(id);
        pet.setStatus(PetStatus.EXPIRED);
        return petRepository.save(pet);
    }
}
