package com.QueensKitchenSpringBoot.controllers.recipeData.payload;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeView {
    public final Long id;
    public final Long userId;
    public final String title;
    public final String category;
    public final String recipeDescription;
    public final boolean published;
    public final boolean hasImage;
    public final int servings, prepMinutes, cookMinutes;
    public final List<RecipeIngredient> recipeIngredientList;
    public final List<RecipeSteps> recipeSteps;
    public RecipeView(Recipe recipe) {
        id = recipe.getId();
        userId = recipe.getUserId();
        title = recipe.getTitle();
        category = recipe.getCategory();
        recipeDescription = recipe.getRecipeDescription();
        published = recipe.isPublished();
        hasImage = recipe.getImageContentType() != null;
        servings = recipe.getServings(); prepMinutes = recipe.getPrepMinutes(); cookMinutes = recipe.getCookMinutes();
        recipeIngredientList = new ArrayList<>(recipe.getRecipeIngredientList());
        recipeSteps = new ArrayList<>(recipe.getRecipeSteps());
    }
}
