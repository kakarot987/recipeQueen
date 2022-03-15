package com.QueensKitchenSpringBoot.controllers.Authentication.repository;

import com.QueensKitchenSpringBoot.controllers.Authentication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	@Query("SELECT c FROM User u WHERE u.email = ?1")
	public User findByEmail(String email);

	public User findByResetPasswordToken(String token);
}