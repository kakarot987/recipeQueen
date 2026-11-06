package com.QueensKitchenSpringBoot.controllers.recipeData.repository;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.Recipe;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(value = "SELECT DISTINCT r FROM Recipe r LEFT JOIN r.recipeIngredientList i " +
      "WHERE (r.published = true OR r.userId = :userId) " +
      "AND (:mine = false OR r.userId = :userId) " +
      "AND (:query IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%'))) " +
      "AND (:ingredient IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredient, '%'))) " +
      "AND (:category IS NULL OR LOWER(r.category) = LOWER(:category))",
      countQuery = "SELECT COUNT(DISTINCT r) FROM Recipe r LEFT JOIN r.recipeIngredientList i " +
      "WHERE (r.published = true OR r.userId = :userId) AND (:mine = false OR r.userId = :userId) " +
      "AND (:query IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%'))) " +
      "AND (:ingredient IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredient, '%'))) " +
      "AND (:category IS NULL OR LOWER(r.category) = LOWER(:category))")
    Page<Recipe> search(@Param("userId") Long userId, @Param("mine") boolean mine,
        @Param("query") String query, @Param("ingredient") String ingredient,
        @Param("category") String category, Pageable pageable);
}
