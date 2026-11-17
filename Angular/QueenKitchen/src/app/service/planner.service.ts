import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ShoppingItem { name: string; quantity: string; checked: boolean; }
export interface MealPlanEntry { id: number; day: string; meal: string; recipeId: number; recipeTitle: string; }
export interface RecipeReview { id: number; userId: number; rating: number; comment: string; updatedAt: string; }

@Injectable({ providedIn: 'root' })
export class PlannerService {
  private base = environment.apiUrl;
  constructor(private http: HttpClient) {}
  shopping(): Observable<ShoppingItem[]> { return this.http.get<ShoppingItem[]>(this.base + '/shopping-list'); }
  saveShopping(items: ShoppingItem[]): Observable<ShoppingItem[]> {
    return this.http.put<ShoppingItem[]>(this.base + '/shopping-list', items);
  }
  week(start: string): Observable<MealPlanEntry[]> {
    return this.http.get<MealPlanEntry[]>(this.base + '/meal-plan', { params: { start } });
  }
  plan(date: string, meal: string, recipeId: number): Observable<MealPlanEntry> {
    return this.http.put<MealPlanEntry>(this.base + '/meal-plan/' + date + '/' + meal, { recipeId });
  }
  unplan(date: string, meal: string): Observable<void> {
    return this.http.delete<void>(this.base + '/meal-plan/' + date + '/' + meal);
  }
  reviews(recipeId: number): Observable<RecipeReview[]> {
    return this.http.get<RecipeReview[]>(this.base + '/recipes/' + recipeId + '/reviews');
  }
  rate(recipeId: number, rating: number, comment: string): Observable<RecipeReview> {
    return this.http.put<RecipeReview>(this.base + '/recipes/' + recipeId + '/reviews/mine', { rating, comment });
  }
  removeReview(recipeId: number): Observable<void> {
    return this.http.delete<void>(this.base + '/recipes/' + recipeId + '/reviews/mine');
  }
  uploadProfile(file: File): Observable<void> {
    const body = new FormData(); body.append('file', file);
    return this.http.post<void>(this.base + '/me/profile-photo', body);
  }
  profile(): Observable<Blob> {
    return this.http.get(this.base + '/me/profile-photo', { responseType: 'blob' });
  }
}
