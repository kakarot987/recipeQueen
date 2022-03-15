package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "recipeSteps")
public class RecipeSteps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @NotNull
    @Column
    private String number;

    @NotNull
    @Column
    private String headline;

    @NotNull
    @Column
    private String description;

    public RecipeSteps() {
    }

    public RecipeSteps(@NotNull String number,@NotNull String headline,@NotNull String description) {
        this.number = number;
        this.headline = headline;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
