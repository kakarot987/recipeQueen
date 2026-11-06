package com.QueensKitchenSpringBoot.controllers.recipeData.payload;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class RequestRecipeBody {
    @NotBlank @Size(max = 160)
    private String title;
    @NotBlank @Size(max = 60)
    private String category;
    @NotBlank @Size(max = 2000)
    private String categoryDescription;
    @NotEmpty @Valid
    private List<RecipeIngredient> recipeIngredientList = new ArrayList<>();
    @NotEmpty @Valid
    private List<RecipeSteps> recipeStepsList = new ArrayList<>();
    private boolean published = true;

    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String value) { categoryDescription = value; }
    public List<RecipeIngredient> getRecipeIngredientList() { return recipeIngredientList; }
    public void setRecipeIngredientList(List<RecipeIngredient> value) { recipeIngredientList = value; }
    public List<RecipeSteps> getRecipeStepsList() { return recipeStepsList; }
    public void setRecipeStepsList(List<RecipeSteps> value) { recipeStepsList = value; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean value) { published = value; }
}
