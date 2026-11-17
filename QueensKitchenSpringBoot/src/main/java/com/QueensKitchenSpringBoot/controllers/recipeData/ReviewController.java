package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsImpl;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import com.QueensKitchenSpringBoot.controllers.recipeData.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes/{recipeId}/reviews")
public class ReviewController {
    private final RecipeReviewRepository reviews;
    private final RecipeRepository recipes;
    private final RecipeService visibility;
    public ReviewController(RecipeReviewRepository reviews,RecipeRepository recipes,RecipeService visibility) {
        this.reviews=reviews;this.recipes=recipes;this.visibility=visibility;
    }
    public static class ReviewInput {
        @Min(1) @Max(5) public int rating;
        @Size(max=1000) public String comment;
    }
    public static class ReviewView {
        public Long id,userId;
        public int rating;
        public String comment;
        public LocalDateTime updatedAt;
        public ReviewView(RecipeReview value) {
            id=value.getId();userId=value.getUserId();rating=value.getRating();
            comment=value.getComment();updatedAt=value.getUpdatedAt();
        }
    }
    @GetMapping @Transactional(readOnly=true)
    public List<ReviewView> list(@AuthenticationPrincipal UserDetailsImpl user,@PathVariable Long recipeId) {
        visibility.get(recipeId,user.getId());
        return reviews.findByRecipe_IdOrderByUpdatedAtDesc(recipeId).stream()
            .map(ReviewView::new).collect(Collectors.toList());
    }
    @PutMapping("/mine") @Transactional
    public ReviewView rate(@AuthenticationPrincipal UserDetailsImpl user,@PathVariable Long recipeId,
        @Valid @RequestBody ReviewInput input) {
        visibility.get(recipeId,user.getId());
        Recipe recipe=recipes.getById(recipeId);
        RecipeReview review=reviews.findByRecipe_IdAndUserId(recipeId,user.getId())
            .orElseGet(()->new RecipeReview(user.getId(),recipe));
        review.setRating(input.rating);
        review.setComment(input.comment==null?"":input.comment.trim());
        review.touch();
        return new ReviewView(reviews.saveAndFlush(review));
    }
    @DeleteMapping("/mine") @Transactional @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal UserDetailsImpl user,@PathVariable Long recipeId) {
        visibility.get(recipeId,user.getId());
        reviews.deleteByRecipe_IdAndUserId(recipeId,user.getId());
    }
}
