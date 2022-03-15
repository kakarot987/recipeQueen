import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RecipeIngredient } from "src/assets/ingredientRecipe";
import { RecipeSteps } from "src/assets/recipeSteps";

const AUTH_API = 'http://localhost:5000/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class RecipeService {

  constructor(private http: HttpClient) { 
  }

  recipeIngredient : Array<RecipeIngredient> = []
  recipeSteps : Array<RecipeSteps> = []


  addrecipe(userId:any,title : any, category:any, categoryDescription:any
    ,recipeIngredientList : Array<RecipeIngredient> , 
    recipeStepsList :  Array<RecipeSteps> ):Observable<any>{
      return this.http.post(AUTH_API + 'addRecipe' ,{
      userId,
      title,
      category,
      categoryDescription,
      recipeIngredientList,
      recipeStepsList
    } , httpOptions);
  }

  getrecipe(userId : any):Observable<any>{
    const param = new HttpParams().set('userId',userId)
    console.log(userId)
     return this.http.post(AUTH_API + 'getRecipe/' + userId, httpOptions);
  }
}
