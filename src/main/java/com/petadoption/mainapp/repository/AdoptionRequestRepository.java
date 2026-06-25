package com.petadoption.mainapp.repository;

import com.petadoption.mainapp.entity.AdoptionRequest;
import com.petadoption.mainapp.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, UUID> {

    List<AdoptionRequest> findByPetId(UUID petId);

    List<AdoptionRequest> findByRequesterId(UUID requesterId);

    List<AdoptionRequest> findByPetIdAndStatus(UUID petId, RequestStatus status);
}
