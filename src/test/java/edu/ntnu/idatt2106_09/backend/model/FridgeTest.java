package edu.ntnu.idatt2106_09.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FridgeTest {

    private Fridge fridge;

    @BeforeEach
    public void setUp() {
        fridge = new Fridge();
    }

    @Test
    public void testAddGroceryItem() {
        GroceryItem groceryItem = new GroceryItem();
        double amount = 1.0;

        fridge.addGroceryItem(groceryItem, amount, LocalDate.now());

        assertThat(fridge.getGroceries()).hasSize(1);
        assertThat(fridge.getGroceries().iterator().next().getGroceryItem()).isEqualTo(groceryItem);
        assertThat(fridge.getGroceries().iterator().next().getAmount()).isEqualTo(amount);
    }


    @Test
    public void testRemoveGroceryItem() {
        Fridge fridge = new Fridge();
        GroceryItem groceryItem = new GroceryItem();
        double amount = 1.0;
        fridge.addGroceryItem(groceryItem, amount, LocalDate.now());
        assertEquals(1, fridge.getGroceries().size());
        LocalDateTime timestamp = fridge.getGroceries().iterator().next().getTimestamp();
        fridge.removeGroceryItem(groceryItem, timestamp);
        assertEquals(0, fridge.getGroceries().size());
    }

}
