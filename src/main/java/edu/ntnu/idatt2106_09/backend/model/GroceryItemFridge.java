package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "grocery_item_fridge")
public class GroceryItemFridge {
    @EmbeddedId
    private GroceryItemFridgeId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("fridgeId")
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("groceryItemId")
    @JoinColumn(name = "grocery_item_id")
    private GroceryItem grocery_item;

    @Column(name = "name")
    private String name;
}

@Embeddable
class GroceryItemFridgeId implements Serializable {
    private Long fridgeId;
    private Long groceryItemId;
}
