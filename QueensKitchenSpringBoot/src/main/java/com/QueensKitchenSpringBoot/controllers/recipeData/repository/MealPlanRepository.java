package com.QueensKitchenSpringBoot.controllers.recipeData.repository;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.MealPlanEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface MealPlanRepository extends JpaRepository<MealPlanEntry,Long> {
    List<MealPlanEntry> findByUserIdAndDayBetweenOrderByDayAscMealAsc(Long userId,LocalDate from,LocalDate to);
    Optional<MealPlanEntry> findByUserIdAndDayAndMeal(Long userId,LocalDate day,String meal);
    void deleteByUserIdAndDayAndMeal(Long userId,LocalDate day,String meal);
    void deleteByRecipe_Id(Long recipeId);
}
