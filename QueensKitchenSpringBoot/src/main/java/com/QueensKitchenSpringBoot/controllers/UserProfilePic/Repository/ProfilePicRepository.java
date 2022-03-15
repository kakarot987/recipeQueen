package com.QueensKitchenSpringBoot.controllers.UserProfilePic.Repository;

import com.QueensKitchenSpringBoot.controllers.UserProfilePic.Entity.ProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilePicRepository extends JpaRepository<ProfilePic, String> {
    List<ProfilePic> findByUserId(Long user_id);
}

