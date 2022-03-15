package com.QueensKitchenSpringBoot.controllers.Authentication.repository;

import com.QueensKitchenSpringBoot.controllers.Authentication.models.ERole;
import com.QueensKitchenSpringBoot.controllers.Authentication.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
