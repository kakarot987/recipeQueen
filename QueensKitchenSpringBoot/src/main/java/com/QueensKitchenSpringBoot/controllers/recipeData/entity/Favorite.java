package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "recipe_id"}))
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    public Favorite() {}
    public Favorite(Long userId, Recipe recipe) { this.userId = userId; this.recipe = recipe; }
    public Long getUserId() { return userId; }
    public Recipe getRecipe() { return recipe; }
}
