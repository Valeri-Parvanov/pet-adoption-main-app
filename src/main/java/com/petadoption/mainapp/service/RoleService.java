package com.petadoption.mainapp.service;

import com.petadoption.mainapp.entity.Role;
import com.petadoption.mainapp.entity.RoleName;
import com.petadoption.mainapp.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getOrCreate(RoleName name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("Creating missing role: {}", name);
                    return roleRepository.save(new Role(name));
                });
    }
}
