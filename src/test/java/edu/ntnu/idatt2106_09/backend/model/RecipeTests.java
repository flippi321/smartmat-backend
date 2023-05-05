package edu.ntnu.idatt2106_09.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemRecipe;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class RecipeTests {

    private Recipe recipe;

    @BeforeEach
    void setUpTest() {
        recipe = new Recipe();
        recipe.setName("Pasta Carbonara");
        recipe.setDescription("A classic Italian dish");
    }

    @Test
    void addGroceryItemTest() {
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setName("Pasta");

        recipe.addGroceryItem(groceryItem);

        Set<GroceryItemRecipe> expectedGroceries = new HashSet<>();
        expectedGroceries.add(new GroceryItemRecipe(recipe, groceryItem));

        assertEquals(expectedGroceries, recipe.getGroceries());
    }

    @Test
    void removeGroceryItemTest() {
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setName("Pasta");

        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setName("Egg");

        recipe.addGroceryItem(groceryItem1);
        recipe.addGroceryItem(groceryItem2);

        recipe.removeGroceryItem(groceryItem1);

        Set<GroceryItemRecipe> expectedGroceries = new HashSet<>();
        expectedGroceries.add(new GroceryItemRecipe(recipe, groceryItem2));

        assertEquals(expectedGroceries, recipe.getGroceries());
    }
}


