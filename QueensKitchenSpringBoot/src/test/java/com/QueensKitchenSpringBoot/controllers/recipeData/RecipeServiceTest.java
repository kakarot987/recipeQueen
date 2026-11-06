package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import com.QueensKitchenSpringBoot.controllers.recipeData.payload.*;
import com.QueensKitchenSpringBoot.controllers.recipeData.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {
    private RecipeRepository recipes;
    private FavoriteRepository favorites;
    private RecipeService service;

    @BeforeEach void setup() {
        recipes = mock(RecipeRepository.class);
        favorites = mock(FavoriteRepository.class);
        service = new RecipeService(recipes, favorites);
    }

    private Recipe ownedBy(Long owner, boolean published) {
        Recipe recipe = new Recipe();
        recipe.setId(7L);
        recipe.setUserId(owner);
        recipe.setPublished(published);
        recipe.setTitle("Soup");
        recipe.setCategory("veg");
        recipe.setRecipeDescription("Delicious");
        return recipe;
    }

    @Test void cannotDeleteAnotherUsersRecipe() {
        when(recipes.findById(7L)).thenReturn(Optional.of(ownedBy(1L, true)));
        assertThrows(ResponseStatusException.class, () -> service.delete(2L, 7L));
        verify(recipes, never()).delete(any(Recipe.class));
    }

    @Test void draftIsInvisibleToOtherUsers() {
        when(recipes.findById(7L)).thenReturn(Optional.of(ownedBy(1L, false)));
        assertThrows(ResponseStatusException.class, () -> service.get(7L, 2L));
    }

    @Test void cannotFavoriteAnotherUsersDraft() {
        when(recipes.findById(7L)).thenReturn(Optional.of(ownedBy(1L, false)));
        assertThrows(ResponseStatusException.class, () -> service.favorite(2L, 7L));
        verify(favorites, never()).save(any(Favorite.class));
    }

    @Test void searchRejectsInvalidPagination() {
        assertThrows(ResponseStatusException.class, () -> service.search(1L, null, null, null, false, -1, 10));
        assertThrows(ResponseStatusException.class, () -> service.search(1L, null, null, null, false, 0, 51));
        verifyNoInteractions(recipes);
    }

    @Test void searchReturnsEmptyPage() {
        when(recipes.search(eq(1L), eq(false), isNull(), isNull(), isNull(), any(Pageable.class)))
            .thenReturn(Page.empty());
        Page<RecipeView> result = service.search(1L, "  ", null, "", false, 0, 12);
        assertTrue(result.isEmpty());
    }

    @Test void ownerCanDelete() {
        when(recipes.findById(7L)).thenReturn(Optional.of(ownedBy(1L, true)));
        service.delete(1L, 7L);
        verify(favorites).deleteByRecipe_Id(7L);
        verify(recipes).delete(any(Recipe.class));
    }
}
