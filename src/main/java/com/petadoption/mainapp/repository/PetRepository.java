package com.petadoption.mainapp.repository;

import com.petadoption.mainapp.entity.Pet;
import com.petadoption.mainapp.entity.PetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, UUID> {

    List<Pet> findByStatus(PetStatus status);

    List<Pet> findByShelterId(UUID shelterId);

    List<Pet> findByStatusAndAvailableSinceBefore(PetStatus status, LocalDateTime cutoff);
}
