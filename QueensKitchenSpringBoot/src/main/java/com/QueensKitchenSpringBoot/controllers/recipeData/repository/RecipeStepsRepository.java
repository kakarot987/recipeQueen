package com.QueensKitchenSpringBoot.controllers.recipeData.repository;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeSteps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface RecipeStepsRepository extends JpaRepository<RecipeSteps, Long> {
}
