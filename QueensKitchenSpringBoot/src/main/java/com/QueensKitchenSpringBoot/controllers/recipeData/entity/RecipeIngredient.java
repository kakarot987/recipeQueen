package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "recipeIngredients")
public class RecipeIngredient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;
    @NotBlank @Size(max = 100) @Column(nullable = false)
    private String name;
    @NotBlank @Size(max = 80) @Column(nullable = false)
    private String ingredientQuantity;
    public RecipeIngredient() {}
    public RecipeIngredient(String name, String quantity) { this.name = name; this.ingredientQuantity = quantity; }
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getIngredientQuantity() { return ingredientQuantity; }
    public void setIngredientQuantity(String value) { ingredientQuantity = value; }
}
