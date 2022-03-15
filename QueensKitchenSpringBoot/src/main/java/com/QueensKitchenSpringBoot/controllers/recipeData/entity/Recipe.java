package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipeInfo")
public class Recipe {

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    private Long userId;

    @NotNull
    @Column
    private String title;

    @NotNull
    @Column
    private String category;

    @NotNull
    @Column
    private String recipeDescription;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
    orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredientList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL
    ,orphanRemoval = true)
    private List<RecipeSteps> recipeSteps = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userID) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public List<RecipeIngredient> getRecipeIngredientList() {
        return recipeIngredientList;
    }

    public void setRecipeIngredientList(List<RecipeIngredient> recipeIngredientList) {
        this.recipeIngredientList = recipeIngredientList;
    }

    public List<RecipeSteps> getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(List<RecipeSteps> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    public Recipe() {
    }

    public Recipe(@NotNull Long userId, @NotNull String title, @NotNull String category, @NotNull String recipeDescription) {
        // this.id = IdGenerator
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.recipeDescription = recipeDescription;
    }

}