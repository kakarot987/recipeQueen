package com.QueensKitchenSpringBoot.controllers.recipeData;

import com.QueensKitchenSpringBoot.controllers.Authentication.security.UserDetailsImpl;
import com.QueensKitchenSpringBoot.controllers.recipeData.entity.ShoppingItem;
import com.QueensKitchenSpringBoot.controllers.recipeData.repository.ShoppingItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {
    private final ShoppingItemRepository repository;
    public ShoppingListController(ShoppingItemRepository repository) { this.repository=repository; }
    public static class ItemInput {
        @NotBlank @Size(max=100) public String name;
        @NotBlank @Size(max=120) public String quantity;
        public boolean checked;
    }

    @GetMapping @Transactional(readOnly=true)
    public List<ShoppingItem> list(@AuthenticationPrincipal UserDetailsImpl user) {
        return repository.findByUserIdOrderById(user.getId());
    }

    @PutMapping @Transactional
    public List<ShoppingItem> replace(@AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @NotNull List<@Valid ItemInput> items) {
        if (items.size()>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum 100 items");
        repository.deleteByUserId(user.getId());
        repository.flush();
        return repository.saveAll(items.stream().map(i -> new ShoppingItem(
            user.getId(), i.name.trim(), i.quantity.trim(), i.checked)).collect(Collectors.toList()));
    }
}
