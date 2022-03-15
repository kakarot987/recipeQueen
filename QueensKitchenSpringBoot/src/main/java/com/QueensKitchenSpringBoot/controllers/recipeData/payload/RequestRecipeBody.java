package com.QueensKitchenSpringBoot.controllers.recipeData.payload;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeIngredient;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeSteps;

import java.util.ArrayList;
import java.util.List;

public class RequestRecipeBody {

    private Long userId;
    private String title;
    private String category;
    private String categoryDescription;

    private List<RecipeIngredient> recipeIngredientList = new ArrayList<RecipeIngredient>();

    private List<RecipeSteps> recipeStepsList = new ArrayList<RecipeSteps>();

    public RequestRecipeBody(Long userId, String title, String category, String categoryDescription, List<RecipeIngredient> recipeIngredientList, List<RecipeSteps> recipeStepsList) {
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.categoryDescription = categoryDescription;
        this.recipeIngredientList = recipeIngredientList;
        this.recipeStepsList = recipeStepsList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public List<RecipeIngredient> getRecipeIngredientList() {
        return recipeIngredientList;
    }

    public void setRecipeIngredientList(List<RecipeIngredient> recipeIngredientList) {
        this.recipeIngredientList = recipeIngredientList;
    }

    public List<RecipeSteps> getRecipeStepsList() {
        return recipeStepsList;
    }

    public void setRecipeStepsList(List<RecipeSteps> recipeStepsList) {
        this.recipeStepsList = recipeStepsList;
    }
}
