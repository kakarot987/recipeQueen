package com.QueensKitchenSpringBoot.controllers.recipeData.repository;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByUserId(Long user_id);
}
