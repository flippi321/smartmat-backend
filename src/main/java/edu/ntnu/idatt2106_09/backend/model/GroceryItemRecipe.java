package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Represents the association between a Recipe and a GroceryItem. This class is used to store the relationship between
 * a particular recipe and its required grocery items, allowing for easy retrieval of ingredients for specific recipes
 * and management of ingredient quantities.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "grocery_item_recipe")
public class GroceryItemRecipe {

    @EmbeddedId
    private GroceryItemRecipeId Id;

    /**
     * Constructs a new GroceryItemRecipe object with the given Recipe and GroceryItem.
     *
     * @param recipe the Recipe that the GroceryItem is a part of.
     * @param groceryItem the GroceryItem being added to the Recipe.
     */
    public GroceryItemRecipe(Recipe recipe, GroceryItem groceryItem) {
        this.recipe = recipe;
        this.groceryItem = groceryItem;
        this.Id = new GroceryItemRecipeId(recipe.getRecipe_id(), groceryItem.getGroceryItemId());
    }

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
}

/**
 * Represents the composite primary key for the GroceryItemRecipe entity.
 * This key is composed of the Recipe and GroceryItem identifiers as well as a timestamp to ensure uniqueness and
 * maintain the relationship between a Recipe and its required GroceryItems.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
class GroceryItemRecipeId implements Serializable {
    private Long recipeId;
    private Long groceryItemId;
}


