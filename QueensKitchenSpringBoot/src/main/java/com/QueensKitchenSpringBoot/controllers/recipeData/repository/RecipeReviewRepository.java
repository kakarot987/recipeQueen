package com.QueensKitchenSpringBoot.controllers.recipeData.repository;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface RecipeReviewRepository extends JpaRepository<RecipeReview,Long> {
    List<RecipeReview> findByRecipe_IdOrderByUpdatedAtDesc(Long id);
    Optional<RecipeReview> findByRecipe_IdAndUserId(Long id,Long userId);
    void deleteByRecipe_IdAndUserId(Long recipeId,Long userId);
    void deleteByRecipe_Id(Long recipeId);
}
