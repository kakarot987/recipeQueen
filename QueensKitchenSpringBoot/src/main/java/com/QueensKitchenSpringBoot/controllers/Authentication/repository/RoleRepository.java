package com.QueensKitchenSpringBoot.controllers.Authentication.repository;

import com.QueensKitchenSpringBoot.controllers.Authentication.models.ERole;
import com.QueensKitchenSpringBoot.controllers.Authentication.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
