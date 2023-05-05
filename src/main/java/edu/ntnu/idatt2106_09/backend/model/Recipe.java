package edu.ntnu.idatt2106_09.backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * Represents a recipe containing information about ingredients, instructions, and other relevant details.
 * The Recipe class stores information about the ingredients, quantities, cooking instructions, and other details
 * required to prepare a meal. It may also include optional information such as images or nutritional information.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipe_id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ElementCollection
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "step_order")
    @Column(name = "step")
    private List<String> steps = new ArrayList<>();

    @Column(name = "image_link")
    private String imageLink;

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<GroceryItemRecipe> groceries = new HashSet<>();

    /**
     * Adds a grocery item to the recipe. This method creates a new GroceryItemRecipe object with the current recipe
     * and the provided grocery item, and adds it to the groceries list.
     *
     * @param groceryItem The grocery item to be added to the recipe.
     */
    public void addGroceryItem(GroceryItem groceryItem) {
        GroceryItemRecipe groceryItemRecipe = new GroceryItemRecipe(this, groceryItem);
        groceries.add(groceryItemRecipe);
    }

    /**
     * Removes a grocery item from the recipe. This method iterates through the groceries list to find the
     * GroceryItemRecipe object with the specified grocery item, removes it from the list, and sets its recipe and
     * grocery item references to null.
     *
     * @param groceryItem The grocery item to be removed from the recipe.
     */
    public void removeGroceryItem(GroceryItem groceryItem) {
        for (Iterator<GroceryItemRecipe> iterator = groceries.iterator();
             iterator.hasNext(); ) {
            GroceryItemRecipe groceryItemRecipe = iterator.next();

            if (groceryItemRecipe.getRecipe().equals(this) &&
                    groceryItemRecipe.getGroceryItem().equals(groceryItem)) {
                iterator.remove();
                groceryItemRecipe.setRecipe(null);
                groceryItemRecipe.setGroceryItem(null);
            }
        }
    }
}
