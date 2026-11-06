package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="meal_plan_entries", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","meal_day","meal_name"}))
public class MealPlanEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="user_id",nullable=false) private Long userId;
    @Column(name="meal_day",nullable=false) private LocalDate day;
    @Column(name="meal_name",nullable=false,length=16) private String meal;
    @ManyToOne(optional=false,fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id",nullable=false) private Recipe recipe;
    public MealPlanEntry() {}
    public MealPlanEntry(Long userId,LocalDate day,String meal,Recipe recipe) {
        this.userId=userId;this.day=day;this.meal=meal;this.recipe=recipe;
    }
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public LocalDate getDay(){return day;}
    public String getMeal(){return meal;}
    public Recipe getRecipe(){return recipe;}
    public void setRecipe(Recipe value){recipe=value;}
}
