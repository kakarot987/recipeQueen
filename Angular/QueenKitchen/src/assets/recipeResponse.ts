import { RecipeIngredient } from "./ingredientRecipe";
import { RecipeSteps } from "./recipeSteps";

export class RecipeResponse{
    userId : any
    title : any
    category : any
    categoryDescription  : any

    recipeIngredient : Array<RecipeIngredient> = []

    recipeSteps : Array<RecipeSteps> = []
}
