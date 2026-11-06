package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsImpl;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.Recipe;
import com.QueensKitchenSpringBoot.controllers.recipeData.payload.*;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {
    private final RecipeService service;
    public RecipeController(RecipeService service) { this.service = service; }

    @GetMapping("/recipes")
    public Page<RecipeView> list(@AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String ingredient,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "false") boolean mine,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        return service.search(user.getId(), search, ingredient, category, mine, page, size);
    }
    @GetMapping("/recipes/{id}")
    public RecipeView get(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
        return service.get(id, user.getId());
    }
    @PostMapping("/recipes")
    public ResponseEntity<RecipeView> create(@AuthenticationPrincipal UserDetailsImpl user,
        @Valid @RequestBody RequestRecipeBody request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(user.getId(), request));
    }
    @PutMapping("/recipes/{id}")
    public RecipeView update(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
        @Valid @RequestBody RequestRecipeBody request) {
        return service.update(user.getId(), id, request);
    }
    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
        service.delete(user.getId(), id);
    }
    @PostMapping(value = "/recipes/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void imageUpload(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id,
        @RequestPart("file") MultipartFile file) throws IOException { service.upload(user.getId(), id, file); }

    @GetMapping("/recipes/{id}/image")
    public ResponseEntity<byte[]> image(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
        Recipe recipe = service.image(user.getId(), id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(recipe.getImageContentType()))
            .cacheControl(CacheControl.noStore()).body(recipe.getImageData());
    }
    @GetMapping("/favorites")
    public List<RecipeView> favorites(@AuthenticationPrincipal UserDetailsImpl user) {
        return service.listFavorites(user.getId());
    }
    @PostMapping("/favorites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void favorite(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
        service.favorite(user.getId(), id);
    }
    @DeleteMapping("/favorites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfavorite(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long id) {
        service.unfavorite(user.getId(), id);
    }
}
