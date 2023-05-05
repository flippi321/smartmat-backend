package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "grocery_item_recipe")
public class GroceryItemRecipe {

    @EmbeddedId
    private GroceryItemRecipeId Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groceryItemId")
    @JoinColumn(name = "grocery_item_id")
    private GroceryItem groceryItem;

    @Column(name = "amount")
    private double amount;

    public GroceryItemRecipe(Recipe recipe, GroceryItem groceryItem) {
        this.recipe = recipe;
        this.groceryItem = groceryItem;
        this.Id = new GroceryItemRecipeId(recipe.getRecipe_id(), groceryItem.getGroceryItemId());
    }

}

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
class GroceryItemRecipeId implements Serializable {
    private Long recipeId;
    private Long groceryItemId;
}


