package com.QueensKitchenSpringBoot.controllers.recipeData.repository;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
}
