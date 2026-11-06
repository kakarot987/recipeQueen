package com.QueensKitchenSpringBoot.controllers.recipeData.repository;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndRecipe_Id(Long userId, Long recipeId);
    void deleteByUserIdAndRecipe_Id(Long userId, Long recipeId);
    void deleteByRecipe_Id(Long recipeId);
}
