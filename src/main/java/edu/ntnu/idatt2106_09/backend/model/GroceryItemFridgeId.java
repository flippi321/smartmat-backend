package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class GroceryItemFridgeId implements Serializable {

    @Column(name = "fridge_id")
    private Long fridgeId;

    @Column(name = "grocery_item_id")
    private Long groceryItemId;
}
