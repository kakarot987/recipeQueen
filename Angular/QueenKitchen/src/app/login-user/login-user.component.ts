import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { RecipeService, Recipe, RecipeForm, Ingredient, Step } from '../service/recipe.service';
import { PlannerService, ShoppingItem, MealPlanEntry, RecipeReview } from '../service/planner.service';
import { scaledAmount } from '../service/servings.util';
import { TokenStorageService } from '../service/token-storage.service';

@Component({ standalone: false,
  selector: 'app-login-user',
  templateUrl: './login-user.component.html',
  styleUrls: ['./login-user.component.css']
})
export class LoginUserComponent implements OnInit, OnDestroy {
  recipes: Recipe[] = [];
  favoriteIds = new Set<number>();
  favorites: Recipe[] = [];
  imageUrls: Record<number, string> = {};
  activeTab: 'discover' | 'mine' | 'favorites' | 'shopping' | 'planner' = 'discover';
  planDay = new Date().toISOString().slice(0, 10);
  planMeal = 'BREAKFAST';
  planRecipeId: number | null = null;
  plans: MealPlanEntry[] = [];
  reviews: Record<number, RecipeReview[]> = {};
  openReviewId: number | null = null;
  reviewStars = 5;
  reviewComment = '';
  displayServings: Record<number, number> = {};
  profileUrl: string | null = null;
  profileUploading = false;
  search = '';
  ingredient = '';
  category = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;
  busy = false;
  saving = false;
  error = '';
  success = '';
  editingId: number | null = null;
  showEditor = false;
  draft: RecipeForm = this.emptyForm();
  newIngredient: Ingredient = { name: '', ingredientQuantity: '' };
  newStep: Step = { number: '', headline: '', description: '' };
  pendingImage: File | null = null;
  shoppingItems: ShoppingItem[] = [];
  selectedForShopping = new Set<number>();

  constructor(private api: RecipeService, private planner: PlannerService, private tokens: TokenStorageService, private router: Router) {}
  get username(): string { return this.tokens.getUser()?.username || 'Cook'; }
  get userId(): number { return this.tokens.getUser()?.id; }
  ngOnInit(): void {
    if (!this.tokens.getToken()) { this.router.navigate(['/home']); return; }
    this.loadFavorites();
    this.load();
    this.planner.shopping().subscribe({ next: items => this.shoppingItems = items, error: () => this.error = 'Shopping list unavailable' });
    this.loadProfile();
  }
  ngOnDestroy(): void {
    Object.values(this.imageUrls).forEach(url => URL.revokeObjectURL(url));
    if (this.profileUrl) URL.revokeObjectURL(this.profileUrl);
  }
  emptyForm(): RecipeForm {
    return { title: '', category: 'veg', categoryDescription: '', published: true, servings: 2, prepMinutes: 10, cookMinutes: 20,
      recipeIngredientList: [], recipeStepsList: [] };
  }
  setTab(tab: 'discover' | 'mine' | 'favorites' | 'shopping' | 'planner'): void {
    this.activeTab = tab; this.page = 0; this.error = ''; this.success = '';
    if (tab === 'favorites') this.loadFavorites();
    else if (tab === 'planner') this.loadPlan();
    else if (tab !== 'shopping') this.load();
  }
  load(): void {
    this.busy = true; this.error = '';
    this.api.list({ search: this.search, ingredient: this.ingredient, category: this.category,
      mine: this.activeTab === 'mine', page: this.page, size: 9 }).subscribe({
      next: data => {
        this.recipes = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.busy = false;
        this.loadImages(this.recipes);
      },
      error: () => { this.busy = false; this.error = 'Unable to load recipes. Please sign in again if your session expired.'; }
    });
  }
  applySearch(): void { this.page = 0; this.load(); }
  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.page = page; this.load();
  }
  loadFavorites(): void {
    this.api.favorites().subscribe({
      next: data => {
        this.favorites = data; this.favoriteIds = new Set(data.map(r => r.id));
        this.loadImages(data);
      },
      error: () => { this.error = 'Unable to load favorites.'; }
    });
  }
  loadImages(recipes: Recipe[]): void {
    recipes.filter(recipe => recipe.hasImage && !this.imageUrls[recipe.id]).forEach(recipe => {
      this.api.image(recipe.id).subscribe({
        next: blob => { if (blob.type.startsWith('image/')) this.imageUrls[recipe.id] = URL.createObjectURL(blob); },
        error: () => { /* image is optional */ }
      });
    });
  }
  toggleFavorite(recipe: Recipe): void {
    const wasFavorite = this.favoriteIds.has(recipe.id);
    const action = wasFavorite ? this.api.unfavorite(recipe.id) : this.api.favorite(recipe.id);
    action.subscribe({
      next: () => this.loadFavorites(),
      error: () => { this.error = 'Could not update favorite.'; }
    });
  }
  openCreate(): void {
    this.editingId = null; this.draft = this.emptyForm(); this.newIngredient = { name: '', ingredientQuantity: '' };
    this.newStep = { number: '', headline: '', description: '' }; this.pendingImage = null; this.showEditor = true; this.error = '';
  }
  openEdit(recipe: Recipe): void {
    this.editingId = recipe.id;
    this.draft = {
      title: recipe.title, category: recipe.category, categoryDescription: recipe.recipeDescription,
      published: recipe.published, servings: recipe.servings, prepMinutes: recipe.prepMinutes, cookMinutes: recipe.cookMinutes,
      recipeIngredientList: recipe.recipeIngredientList.map(i => ({ name: i.name, ingredientQuantity: i.ingredientQuantity })),
      recipeStepsList: recipe.recipeSteps.map(s => ({ number: s.number, headline: s.headline, description: s.description }))
    };
    this.pendingImage = null; this.showEditor = true; this.error = '';
  }
  addIngredient(): void {
    if (!this.newIngredient.name.trim() || !this.newIngredient.ingredientQuantity.trim()) return;
    this.draft.recipeIngredientList.push({ name: this.newIngredient.name.trim(), ingredientQuantity: this.newIngredient.ingredientQuantity.trim() });
    this.newIngredient = { name: '', ingredientQuantity: '' };
  }
  addStep(): void {
    if (!this.newStep.headline.trim() || !this.newStep.description.trim()) return;
    this.draft.recipeStepsList.push({ number: String(this.draft.recipeStepsList.length + 1),
      headline: this.newStep.headline.trim(), description: this.newStep.description.trim() });
    this.newStep = { number: '', headline: '', description: '' };
  }
  chooseImage(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (!['image/png', 'image/jpeg'].includes(file.type) || file.size > 2 * 1024 * 1024) {
      this.error = 'Choose a JPEG or PNG up to 2 MB'; input.value = ''; return;
    }
    this.pendingImage = file;
  }
  save(): void {
    if (!this.draft.title.trim() || !this.draft.categoryDescription.trim() ||
      this.draft.recipeIngredientList.length === 0 || this.draft.recipeStepsList.length === 0) {
      this.error = 'Add a title, description, at least one ingredient and at least one step.'; return;
    }
    this.saving = true; this.error = '';
    const action = this.editingId === null ? this.api.create(this.draft) : this.api.update(this.editingId, this.draft);
    action.subscribe({
      next: recipe => {
        if (this.pendingImage) {
          this.api.uploadImage(recipe.id, this.pendingImage).subscribe({
            next: () => this.saved(),
            error: () => { this.saved(); this.error = 'Recipe saved, but image upload failed.'; }
          });
        } else this.saved();
      },
      error: err => { this.saving = false; this.error = err.error?.message || 'Could not save recipe. Check the fields.'; }
    });
  }
  saved(): void {
    this.saving = false; this.showEditor = false; this.success = 'Recipe saved.';
    Object.values(this.imageUrls).forEach(url => URL.revokeObjectURL(url));
    this.imageUrls = {};
    this.load(); this.loadFavorites();
  }
  remove(recipe: Recipe): void {
    if (!window.confirm('Delete "' + recipe.title + '" permanently?')) return;
    this.api.delete(recipe.id).subscribe({
      next: () => { this.success = 'Recipe deleted.'; this.load(); this.loadFavorites(); },
      error: () => { this.error = 'Delete failed.'; }
    });
  }

  changeServings(recipe: Recipe, value: number): void {
    this.displayServings[recipe.id] = Math.min(50, Math.max(1, Number(value) || recipe.servings));
  }
  amount(recipe: Recipe, quantity: string): string {
    return scaledAmount(quantity, recipe.servings || 2, this.displayServings[recipe.id] || recipe.servings || 2);
  }
  loadProfile(): void {
    this.planner.profile().subscribe({
      next: image => {
        if (this.profileUrl) URL.revokeObjectURL(this.profileUrl);
        this.profileUrl = URL.createObjectURL(image);
      },
      error: () => { /* no profile photo yet */ }
    });
  }
  uploadProfile(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    if (!['image/jpeg', 'image/png'].includes(file.type) || file.size > 2097152) {
      this.error = 'Profile photo must be PNG/JPEG under 2MB'; return;
    }
    this.profileUploading = true;
    this.planner.uploadProfile(file).subscribe({
      next: () => { this.profileUploading = false; this.loadProfile(); this.success = 'Profile photo updated'; },
      error: () => { this.profileUploading = false; this.error = 'Profile photo upload failed'; }
    });
  }
  openReviews(recipe: Recipe): void {
    this.openReviewId = this.openReviewId === recipe.id ? null : recipe.id;
    if (this.openReviewId !== null) this.loadReviews(recipe.id);
  }
  loadReviews(id: number): void {
    this.planner.reviews(id).subscribe({
      next: rows => { this.reviews[id] = rows; const mine = rows.find(r => r.userId === this.userId);
        if (mine) { this.reviewStars = mine.rating; this.reviewComment = mine.comment; }
        else { this.reviewStars = 5; this.reviewComment = ''; }
      },
      error: () => this.error = 'Could not load reviews'
    });
  }
  rate(recipe: Recipe): void {
    this.planner.rate(recipe.id, this.reviewStars, this.reviewComment).subscribe({
      next: () => this.loadReviews(recipe.id),
      error: () => this.error = 'Could not save your review'
    });
  }
  removeMyReview(recipe: Recipe): void {
    this.planner.removeReview(recipe.id).subscribe({
      next: () => this.loadReviews(recipe.id),
      error: () => this.error = 'Could not remove review'
    });
  }
  loadPlan(): void {
    this.planner.week(this.planDay).subscribe({
      next: entries => this.plans = entries,
      error: () => this.error = 'Could not load meal plan'
    });
  }
  assignPlan(): void {
    if (!this.planRecipeId) { this.error = 'Choose a recipe first'; return; }
    this.planner.plan(this.planDay, this.planMeal, this.planRecipeId).subscribe({
      next: () => { this.success = 'Meal planned'; this.loadPlan(); },
      error: () => this.error = 'Could not schedule this meal'
    });
  }
  removePlan(item: MealPlanEntry): void {
    this.planner.unplan(item.day, item.meal).subscribe({
      next: () => this.loadPlan(),
      error: () => this.error = 'Could not remove this meal'
    });
  }
  saveShoppingList(): void {
    this.planner.saveShopping(this.shoppingItems).subscribe({
      next: items => this.shoppingItems = items,
      error: () => this.error = 'Could not save shopping list'
    });
  }

  toggleShopping(recipe: Recipe): void {
    this.selectedForShopping.add(recipe.id);
    recipe.recipeIngredientList.forEach(ingredient => {
      const existing = this.shoppingItems.find(i => i.name.toLowerCase() === ingredient.name.toLowerCase());
      if (existing) existing.quantity += ', ' + ingredient.ingredientQuantity;
      else this.shoppingItems.push({ name: ingredient.name, quantity: ingredient.ingredientQuantity, checked: false });
    });
    this.saveShoppingList();
    this.success = 'Ingredients added to saved shopping list';
  }
  removeShoppingItem(index: number): void {
    this.shoppingItems.splice(index, 1);
    this.saveShoppingList();
  }
  logout(): void { this.tokens.signOut(); this.router.navigate(['/home']); }
}
