package com.petadoption.mainapp.service;

import com.petadoption.mainapp.entity.Shelter;
import com.petadoption.mainapp.exception.ResourceNotFoundException;
import com.petadoption.mainapp.repository.ShelterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public Shelter create(Shelter shelter) {
        Shelter savedShelter = shelterRepository.save(shelter);
        log.info("Created shelter: {}", savedShelter.getName());
        return savedShelter;
    }

    public Shelter getById(UUID id) {
        return shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", id));
    }

    public List<Shelter> getAll() {
        return shelterRepository.findAll();
    }

    public List<Shelter> getByOwnerId(UUID ownerId) {
        return shelterRepository.findByOwnerId(ownerId);
    }

    public Shelter update(UUID id, Shelter updatedData) {
        Shelter shelter = getById(id);
        shelter.setName(updatedData.getName());
        shelter.setAddress(updatedData.getAddress());
        shelter.setPhone(updatedData.getPhone());
        shelter.setEmail(updatedData.getEmail());
        return shelterRepository.save(shelter);
    }

    public void delete(UUID id) {
        Shelter shelter = getById(id);
        shelterRepository.delete(shelter);
        log.info("Deleted shelter: {}", shelter.getName());
    }
}
