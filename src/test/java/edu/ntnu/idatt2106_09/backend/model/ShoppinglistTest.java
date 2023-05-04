package edu.ntnu.idatt2106_09.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppinglistTest {

    private Shoppinglist shoppinglist;

    @BeforeEach
    public void setUp() {
        shoppinglist = new Shoppinglist();
    }

    @Test
    public void testAddGroceryItem() {
        GroceryItem groceryItem = new GroceryItem();
        double amount = 1;

        shoppinglist.addGroceryItem(groceryItem, amount);

        assertThat(shoppinglist.getGroceries()).hasSize(1);
        assertThat(shoppinglist.getGroceries().iterator().next().getGroceryItem()).isEqualTo(groceryItem);
        assertThat(shoppinglist.getGroceries().iterator().next().getAmount()).isEqualTo(amount);
    }

    @Test
    public void testRemoveGroceryItem() {
        Shoppinglist shoppinglist = new Shoppinglist();
        GroceryItem groceryItem = new GroceryItem();
        double amount = 1;
        shoppinglist.addGroceryItem(groceryItem, amount);
        assertEquals(1, shoppinglist.getGroceries().size());
        LocalDateTime timestamp = shoppinglist.getGroceries().iterator().next().getTimestamp();
        shoppinglist.removeGroceryItem(groceryItem, timestamp);
        assertEquals(0, shoppinglist.getGroceries().size());
    }
}
