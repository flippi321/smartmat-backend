package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "grocery_item_fridge")
public class GroceryItemFridge {
    @EmbeddedId
    private GroceryItemFridgeId id;

    public GroceryItemFridge(Fridge fridge, GroceryItem groceryItem) {
        this.fridge = fridge;
        this.grocery_item = groceryItem;
        this.id = new GroceryItemFridgeId(fridge.getFridgeId(), groceryItem.getGroceryItemId());
    }

    @ManyToOne//(fetch = FetchType.LAZY)
    @MapsId("fridgeId")
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne//(fetch = FetchType.LAZY)
    @MapsId("groceryItemId")
    @JoinColumn(name = "grocery_item_id")
    private GroceryItem grocery_item;

    @Column(name = "name")
    private String name;
}

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
class GroceryItemFridgeId implements Serializable {
    private Long fridgeId;
    private Long groceryItemId;
}
