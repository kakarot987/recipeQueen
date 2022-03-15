package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "recipeIngredients")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String ingredientQuantity;
    
    public RecipeIngredient(@NotNull String name,@NotNull String ingredientQuantity) {
        this.name = name;
        this.ingredientQuantity = ingredientQuantity;
    }

    public RecipeIngredient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredientQuantity() {
        return ingredientQuantity;
    }

    public void setIngredientQuantity(String ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }
}
