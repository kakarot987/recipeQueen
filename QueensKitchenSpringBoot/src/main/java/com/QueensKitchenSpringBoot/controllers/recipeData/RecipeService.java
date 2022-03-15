package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.Recipe;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeIngredient;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.RecipeSteps;
import com.QueensKitchenSpringBoot.controllers.recipeData.payload.RequestRecipeBody;
import com.QueensKitchenSpringBoot.controllers.recipeData.payload.ResponseRecipeBody;
import com.QueensKitchenSpringBoot.controllers.recipeData.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository repository;

    @Transactional
    public ResponseEntity<?> saveRecipe(RequestRecipeBody requestRecipeBody){

        try{
            Recipe recipe = new Recipe(
                    requestRecipeBody.getUserId(),
                    requestRecipeBody.getTitle(),
                    requestRecipeBody.getCategory(),
                    requestRecipeBody.getCategoryDescription()
            );

            List<RecipeIngredient> ingredientList = new ArrayList<>();
            for(int i=0;i<requestRecipeBody.getRecipeIngredientList().size();i++){
                System.out.println("Ingredient name--------"+requestRecipeBody.getRecipeIngredientList().get(i).getName());
                System.out.println("Ingredient Quantity--------"+requestRecipeBody.getRecipeIngredientList().get(i).getIngredientQuantity());

                ingredientList.add(
                        new RecipeIngredient(
                                requestRecipeBody.getRecipeIngredientList().get(i).getName(),
                                requestRecipeBody.getRecipeIngredientList().get(i).getIngredientQuantity()
                        )
                );
            }
            System.out.println("Ingredient List size------------------------"+ingredientList.size());

            List<RecipeSteps> stepList = new ArrayList<>();
            for(int i=0;i<requestRecipeBody.getRecipeStepsList().size();i++){
                System.out.println("Step Number-----------"+requestRecipeBody.getRecipeStepsList().get(i).getNumber());
                System.out.println("Step Headline-----------"+requestRecipeBody.getRecipeStepsList().get(i).getHeadline());
                System.out.println("Step Discription-----------"+requestRecipeBody.getRecipeStepsList().get(i).getDescription());

                stepList.add(
                        new RecipeSteps(
                                requestRecipeBody.getRecipeStepsList().get(i).getNumber(),
                                requestRecipeBody.getRecipeStepsList().get(i).getHeadline(),
                                requestRecipeBody.getRecipeStepsList().get(i).getDescription()
                        )
                );
            }

            recipe.setRecipeIngredientList(ingredientList);
            recipe.setRecipeSteps(stepList);
            repository.save(recipe);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Recipe Added Successfully");
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error Occured");
        }
    }

    public ResponseEntity<?> getRecipe(Long userId){
        List<Recipe> recipes = repository.findByUserId(userId);
        System.out.println(recipes.size());
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipes);
    }
}
