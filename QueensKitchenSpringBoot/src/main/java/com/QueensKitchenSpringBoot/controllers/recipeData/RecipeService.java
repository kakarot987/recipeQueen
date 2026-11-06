package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.recipeData.entity.*;
import com.QueensKitchenSpringBoot.controllers.recipeData.payload.*;
import com.QueensKitchenSpringBoot.controllers.recipeData.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.JoinType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

@Service
public class RecipeService {
    private final RecipeRepository recipes;
    private final FavoriteRepository favorites;
    public RecipeService(RecipeRepository recipes, FavoriteRepository favorites) {
        this.recipes = recipes; this.favorites = favorites;
    }
    private static String filter(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        return input.trim();
    }
    private Recipe requireVisible(Long id, Long userId) {
        Recipe recipe = recipes.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        if (!recipe.isPublished() && !recipe.getUserId().equals(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        return recipe;
    }
    private Recipe requireOwner(Long id, Long userId) {
        Recipe recipe = requireVisible(id, userId);
        if (!recipe.getUserId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your recipe");
        return recipe;
    }

    @Transactional(readOnly = true)
    public Page<RecipeView> search(Long userId, String query, String ingredient, String category,
                                   boolean mine, int page, int size) {
        if (page < 0 || size < 1 || size > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination");
        String title = filter(query), ingredientFilter = filter(ingredient), categoryFilter = filter(category);
        Specification<Recipe> spec = (root, criteria, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.or(cb.isTrue(root.get("published")), cb.equal(root.get("userId"), userId)));
            if (mine) predicates.add(cb.equal(root.get("userId"), userId));
            if (title != null) predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase(java.util.Locale.ROOT) + "%"));
            if (categoryFilter != null) predicates.add(cb.equal(cb.lower(root.get("category")), categoryFilter.toLowerCase(java.util.Locale.ROOT)));
            if (ingredientFilter != null) {
                criteria.distinct(true);
                predicates.add(cb.like(cb.lower(root.join("recipeIngredientList", JoinType.INNER).get("name")),
                    "%" + ingredientFilter.toLowerCase(java.util.Locale.ROOT) + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return recipes.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
            .map(RecipeView::new);
    }
    @Transactional(readOnly = true)
    public RecipeView get(Long id, Long userId) { return new RecipeView(requireVisible(id, userId)); }

    private void updateFields(Recipe recipe, RequestRecipeBody body) {
        recipe.setTitle(body.getTitle().trim());
        recipe.setCategory(body.getCategory().trim());
        recipe.setRecipeDescription(body.getCategoryDescription().trim());
        recipe.setPublished(body.isPublished());
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (RecipeIngredient i : body.getRecipeIngredientList())
            ingredients.add(new RecipeIngredient(i.getName().trim(), i.getIngredientQuantity().trim()));
        recipe.getRecipeIngredientList().clear();
        recipe.getRecipeIngredientList().addAll(ingredients);
        List<RecipeSteps> steps = new ArrayList<>();
        for (RecipeSteps s : body.getRecipeStepsList())
            steps.add(new RecipeSteps(s.getNumber().trim(), s.getHeadline().trim(), s.getDescription().trim()));
        recipe.getRecipeSteps().clear();
        recipe.getRecipeSteps().addAll(steps);
    }
    @Transactional
    public RecipeView create(Long userId, RequestRecipeBody body) {
        Recipe recipe = new Recipe();
        recipe.setUserId(userId);
        updateFields(recipe, body);
        return new RecipeView(recipes.saveAndFlush(recipe));
    }
    @Transactional
    public RecipeView update(Long userId, Long id, RequestRecipeBody body) {
        Recipe recipe = requireOwner(id, userId);
        updateFields(recipe, body);
        return new RecipeView(recipes.saveAndFlush(recipe));
    }
    @Transactional
    public void delete(Long userId, Long id) {
        Recipe recipe = requireOwner(id, userId);
        favorites.deleteByRecipe_Id(id);
        recipes.delete(recipe);
    }
    @Transactional(readOnly = true)
    public List<RecipeView> listFavorites(Long userId) {
        List<RecipeView> list = new ArrayList<>();
        for (Favorite favorite : favorites.findByUserId(userId)) {
            Recipe recipe = favorite.getRecipe();
            if (recipe.isPublished() || recipe.getUserId().equals(userId))
                list.add(new RecipeView(recipe));
        }
        return list;
    }
    @Transactional
    public void favorite(Long userId, Long id) {
        Recipe recipe = requireVisible(id, userId);
        if (!favorites.existsByUserIdAndRecipe_Id(userId, id))
            favorites.save(new Favorite(userId, recipe));
    }
    @Transactional
    public void unfavorite(Long userId, Long id) { favorites.deleteByUserIdAndRecipe_Id(userId, id); }

    @Transactional
    public void upload(Long userId, Long id, MultipartFile file) throws IOException {
        Recipe recipe = requireOwner(id, userId);
        if (file == null || file.isEmpty() || file.getSize() > 2 * 1024 * 1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an image up to 2 MB");
        String type = file.getContentType();
        if (!"image/jpeg".equals(type) && !"image/png".equals(type))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JPEG or PNG required");
        byte[] bytes = file.getBytes();
        if (ImageIO.read(new ByteArrayInputStream(bytes)) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image data");
        recipe.setImageData(bytes);
        recipe.setImageContentType(type);
        recipes.save(recipe);
    }
    @Transactional(readOnly = true)
    public Recipe image(Long userId, Long id) {
        Recipe recipe = requireVisible(id, userId);
        if (recipe.getImageData() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        return recipe;
    }
}
