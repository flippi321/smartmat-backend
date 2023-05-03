package edu.ntnu.idatt2106_09.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoppinglistTest {

    private Shoppinglist shoppinglist;

    @BeforeEach
    public void setUp() {
        shoppinglist = new Shoppinglist();
    }

    @Test
    public void testAddGroceryItem() {
        GroceryItem groceryItem = new GroceryItem();
        int amount = 1;

        shoppinglist.addGroceryItem(groceryItem, amount);

        assertThat(shoppinglist.getGroceries()).hasSize(1);
        assertThat(shoppinglist.getGroceries().iterator().next().getGroceryItem()).isEqualTo(groceryItem);
        assertThat(shoppinglist.getGroceries().iterator().next().getAmount()).isEqualTo(amount);
    }

    @Test
    public void testRemoveGroceryItem() {
        GroceryItem groceryItem = new GroceryItem();
        int amount = 1;

        shoppinglist.addGroceryItem(groceryItem, amount);
        shoppinglist.removeGroceryItem(groceryItem);

        assertThat(shoppinglist.getGroceries()).isEmpty();
    }
}
