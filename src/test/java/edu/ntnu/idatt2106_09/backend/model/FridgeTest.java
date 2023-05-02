package edu.ntnu.idatt2106_09.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FridgeTest {

    private Fridge fridge;

    @BeforeEach
    public void setUp() {
        fridge = new Fridge();
    }

    @Test
    public void testAddGroceryItem() {
        GroceryItem groceryItem = new GroceryItem();
        int amount = 1;

        fridge.addGroceryItem(groceryItem, amount);

        assertThat(fridge.getGroceries()).hasSize(1);
        assertThat(fridge.getGroceries().iterator().next().getGroceryItem()).isEqualTo(groceryItem);
        assertThat(fridge.getGroceries().iterator().next().getAmount()).isEqualTo(amount);
    }

    @Test
    public void testRemoveGroceryItem() {
        GroceryItem groceryItem = new GroceryItem();
        int amount = 1;

        fridge.addGroceryItem(groceryItem, amount);
        fridge.removeGroceryItem(groceryItem);

        assertThat(fridge.getGroceries()).isEmpty();
    }
}
