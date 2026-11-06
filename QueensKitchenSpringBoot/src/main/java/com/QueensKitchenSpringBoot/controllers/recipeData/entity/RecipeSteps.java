package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "recipeSteps")
public class RecipeSteps {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;
    @NotBlank @Size(max = 20) private String number;
    @NotBlank @Size(max = 120) private String headline;
    @NotBlank @Size(max = 2000) private String description;
    public RecipeSteps() {}
    public RecipeSteps(String number, String headline, String description) {
        this.number = number; this.headline = headline; this.description = description;
    }
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getNumber() { return number; }
    public void setNumber(String value) { number = value; }
    public String getHeadline() { return headline; }
    public void setHeadline(String value) { headline = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
}
