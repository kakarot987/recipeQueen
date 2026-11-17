package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="recipe_reviews", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","recipe_id"}))
public class RecipeReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="user_id",nullable=false) private Long userId;
    @ManyToOne(optional=false,fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id",nullable=false) private Recipe recipe;
    @Column(nullable=false) private int rating;
    @Column(length=1000) private String comment="";
    @Column(nullable=false) private LocalDateTime updatedAt=LocalDateTime.now();
    public RecipeReview(){}
    public RecipeReview(Long userId,Recipe recipe){this.userId=userId;this.recipe=recipe;}
    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public Recipe getRecipe(){return recipe;}
    public int getRating(){return rating;}
    public void setRating(int rating){this.rating=rating;}
    public String getComment(){return comment;}
    public void setComment(String value){comment=value;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
    public void touch(){updatedAt=LocalDateTime.now();}
}
