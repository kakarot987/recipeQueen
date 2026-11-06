package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipeInfo")
public class Recipe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, length = 60) private String category;
    @Column(nullable = false, length = 2000) private String recipeDescription;
    @Column(nullable = false) private boolean published = true;
    @Column(nullable = false) private int servings = 2;
    @Column(nullable = false) private int prepMinutes = 10;
    @Column(nullable = false) private int cookMinutes = 20;
    @Lob @Basic(fetch = FetchType.LAZY) private byte[] imageData;
    @Column(length = 30) private String imageContentType;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "recipe_info_recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_recipe_id"), inverseJoinColumns = @JoinColumn(name = "recipe_ingredient_list_ingredient_id", unique = true))
    private List<RecipeIngredient> recipeIngredientList = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "recipe_info_recipe_steps", joinColumns = @JoinColumn(name = "recipe_recipe_id"), inverseJoinColumns = @JoinColumn(name = "recipe_steps_step_id", unique = true))
    private List<RecipeSteps> recipeSteps = new ArrayList<>();

    public int getServings() { return servings; }
    public void setServings(int value) { servings = value; }
    public int getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(int value) { prepMinutes = value; }
    public int getCookMinutes() { return cookMinutes; }
    public void setCookMinutes(int value) { cookMinutes = value; }
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getUserId() { return userId; }
    public void setUserId(Long value) { userId = value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public String getRecipeDescription() { return recipeDescription; }
    public void setRecipeDescription(String value) { recipeDescription = value; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean value) { published = value; }
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] value) { imageData = value; }
    public String getImageContentType() { return imageContentType; }
    public void setImageContentType(String value) { imageContentType = value; }
    public List<RecipeIngredient> getRecipeIngredientList() { return recipeIngredientList; }
    public void setRecipeIngredientList(List<RecipeIngredient> value) { recipeIngredientList = value; }
    public List<RecipeSteps> getRecipeSteps() { return recipeSteps; }
    public void setRecipeSteps(List<RecipeSteps> value) { recipeSteps = value; }
}
