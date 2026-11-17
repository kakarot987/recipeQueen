package com.QueensKitchenSpringBoot.controllers.recipeData.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "shopping_items")
public class ShoppingItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id", nullable=false) private Long userId;
    @NotBlank @Size(max=100) private String name;
    @NotBlank @Size(max=120) private String quantity;
    private boolean checked;
    public ShoppingItem() {}
    public ShoppingItem(Long userId, String name, String quantity, boolean checked) {
        this.userId = userId; this.name = name; this.quantity = quantity; this.checked = checked;
    }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public boolean isChecked() { return checked; }
}
