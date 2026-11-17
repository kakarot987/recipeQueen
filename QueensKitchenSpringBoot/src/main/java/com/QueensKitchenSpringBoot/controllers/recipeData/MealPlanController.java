package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsImpl;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import com.QueensKitchenSpringBoot.controllers.recipeData.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meal-plan")
public class MealPlanController {
    private final MealPlanRepository plans;
    private final RecipeService recipes;
    private final RecipeRepository recipeRepository;
    public MealPlanController(MealPlanRepository plans,RecipeService recipes,RecipeRepository recipeRepository) {
        this.plans=plans;this.recipes=recipes;this.recipeRepository=recipeRepository;
    }
    public static class PlanRequest { public Long recipeId; }
    public static class PlanView {
        public Long id,recipeId;
        public LocalDate day;
        public String meal,recipeTitle;
        public PlanView(MealPlanEntry entry) {
            id=entry.getId();day=entry.getDay();meal=entry.getMeal();
            recipeId=entry.getRecipe().getId();recipeTitle=entry.getRecipe().getTitle();
        }
    }
    private static String validMeal(String value) {
        String meal=value.toUpperCase(Locale.ROOT);
        if (!Arrays.asList("BREAKFAST","LUNCH","DINNER").contains(meal))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Meal must be breakfast, lunch or dinner");
        return meal;
    }
    @GetMapping @Transactional(readOnly=true)
    public List<PlanView> week(@AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam String start) {
        LocalDate from;
        try { from=LocalDate.parse(start); }
        catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid date"); }
        return plans.findByUserIdAndDayBetweenOrderByDayAscMealAsc(user.getId(),from,from.plusDays(6))
            .stream().map(PlanView::new).collect(Collectors.toList());
    }
    @PutMapping("/{date}/{meal}") @Transactional
    public PlanView assign(@AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable String date,@PathVariable String meal,@RequestBody PlanRequest request) {
        LocalDate day;
        try { day=LocalDate.parse(date); }
        catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid date"); }
        String slot=validMeal(meal);
        if (request.recipeId==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Recipe required");
        // Verify draft/public visibility before linking to a private meal plan.
        recipes.get(request.recipeId,user.getId());
        Recipe recipe=recipeRepository.getById(request.recipeId);
        MealPlanEntry entry=plans.findByUserIdAndDayAndMeal(user.getId(),day,slot)
            .orElseGet(() -> new MealPlanEntry(user.getId(),day,slot,recipe));
        entry.setRecipe(recipe);
        return new PlanView(plans.saveAndFlush(entry));
    }
    @DeleteMapping("/{date}/{meal}") @Transactional @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable String date,@PathVariable String meal) {
        LocalDate day;
        try { day=LocalDate.parse(date); }
        catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid date"); }
        plans.deleteByUserIdAndDayAndMeal(user.getId(),day,validMeal(meal));
    }
}
