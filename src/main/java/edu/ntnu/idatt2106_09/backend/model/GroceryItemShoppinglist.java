package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "shoppinglist")
@Entity
@Table(name = "grocery_item_shoppinglist")
public class GroceryItemShoppinglist {

    @EmbeddedId
    private GroceryItemShoppinglistId id;

    public GroceryItemShoppinglist(Shoppinglist shoppinglist, GroceryItem groceryItem, int amount) {
        this.shoppinglist = shoppinglist;
        this.groceryItem = groceryItem;
        this.id = new GroceryItemShoppinglistId(shoppinglist.getShoppinglistId(), groceryItem.getGroceryItemId());
        this.amount = amount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shoppinglistId")
    @JoinColumn(name = "shoppinglist_id")
    private Shoppinglist shoppinglist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groceryItemId")
    @JoinColumn(name = "grocery_item_id")
    private GroceryItem groceryItem;

    @Column(name = "amount")
    private int amount;

    public Long getGroceryItemId() {
        return groceryItem.getGroceryItemId();
    }
}

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
class GroceryItemShoppinglistId implements Serializable {
    private Long shoppinglistId;
    private Long groceryItemId;
}