package com.petadoption.mainapp.repository;

import com.petadoption.mainapp.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShelterRepository extends JpaRepository<Shelter, UUID> {

    List<Shelter> findByOwnerId(UUID ownerId);
}
