import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RecipeService } from './recipe.service';

@Injectable({ providedIn: 'root' })
export class ImageService {
  constructor(private recipes: RecipeService) {}
  uploadRecipeImage(recipeId: number, file: File): Observable<void> {
    return this.recipes.uploadImage(recipeId, file);
  }
  getRecipeImage(recipeId: number): Observable<Blob> {
    return this.recipes.image(recipeId);
  }
}
