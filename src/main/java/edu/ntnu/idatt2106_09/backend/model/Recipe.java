package edu.ntnu.idatt2106_09.backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<GroceryItemRecipe> groceries = new HashSet<>();

    public void addGroceryItem(GroceryItem groceryItem) {
        GroceryItemRecipe groceryItemRecipe = new GroceryItemRecipe(this, groceryItem);
        groceries.add(groceryItemRecipe);
    }

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
