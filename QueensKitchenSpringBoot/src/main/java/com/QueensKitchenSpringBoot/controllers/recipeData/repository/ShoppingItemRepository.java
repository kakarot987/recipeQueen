package com.QueensKitchenSpringBoot.controllers.recipeData.repository;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
    List<ShoppingItem> findByUserIdOrderById(Long userId);
    void deleteByUserId(Long userId);
}
