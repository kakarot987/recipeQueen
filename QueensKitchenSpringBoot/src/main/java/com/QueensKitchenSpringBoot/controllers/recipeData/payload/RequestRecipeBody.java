package com.QueensKitchenSpringBoot.controllers.recipeData.payload;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Min(1) @Max(50) private int servings = 2;
    @Min(0) @Max(1440) private int prepMinutes = 10;
    @Min(0) @Max(1440) private int cookMinutes = 20;

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
    public int getServings() { return servings; }
    public void setServings(int value) { servings = value; }
    public int getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(int value) { prepMinutes = value; }
    public int getCookMinutes() { return cookMinutes; }
    public void setCookMinutes(int value) { cookMinutes = value; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean value) { published = value; }
}
