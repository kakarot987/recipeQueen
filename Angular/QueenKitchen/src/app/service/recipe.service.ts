import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Ingredient {
  name: string;
  ingredientQuantity: string;
}
export interface Step {
  number: string;
  headline: string;
  description: string;
}
export interface Recipe {
  id: number;
  userId: number;
  title: string;
  category: string;
  recipeDescription: string;
  published: boolean;
  hasImage: boolean;
  servings: number;
  prepMinutes: number;
  cookMinutes: number;
  recipeIngredientList: Ingredient[];
  recipeSteps: Step[];
}
export interface RecipeForm {
  title: string;
  category: string;
  categoryDescription: string;
  published: boolean;
  servings: number;
  prepMinutes: number;
  cookMinutes: number;
  recipeIngredientList: Ingredient[];
  recipeStepsList: Step[];
}
export interface RecipePage {
  content: Recipe[];
  totalElements: number;
  totalPages: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private readonly url = environment.apiUrl;
  constructor(private http: HttpClient) {}

  list(filters: { search?: string; ingredient?: string; category?: string; mine?: boolean; page?: number; size?: number }): Observable<RecipePage> {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') params = params.set(key, String(value));
    });
    return this.http.get<RecipePage>(this.url + '/recipes', { params });
  }
  get(id: number): Observable<Recipe> { return this.http.get<Recipe>(this.url + '/recipes/' + id); }
  create(form: RecipeForm): Observable<Recipe> { return this.http.post<Recipe>(this.url + '/recipes', form); }
  update(id: number, form: RecipeForm): Observable<Recipe> { return this.http.put<Recipe>(this.url + '/recipes/' + id, form); }
  delete(id: number): Observable<void> { return this.http.delete<void>(this.url + '/recipes/' + id); }
  favorites(): Observable<Recipe[]> { return this.http.get<Recipe[]>(this.url + '/favorites'); }
  favorite(id: number): Observable<void> { return this.http.post<void>(this.url + '/favorites/' + id, {}); }
  unfavorite(id: number): Observable<void> { return this.http.delete<void>(this.url + '/favorites/' + id); }
  uploadImage(id: number, file: File): Observable<void> {
    const body = new FormData();
    body.append('file', file);
    return this.http.post<void>(this.url + '/recipes/' + id + '/image', body);
  }
  image(id: number): Observable<Blob> {
    return this.http.get(this.url + '/recipes/' + id + '/image', { responseType: 'blob' });
  }
}
