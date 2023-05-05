package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the association between a ShoppingList and a GroceryItem. This class is used to store the relationship
 * between a particular shopping list and its required grocery items, allowing for easy management of items to be
 * purchased and their respective quantities.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "shoppinglist")
@Entity
@Table(name = "grocery_item_shoppinglist")
public class GroceryItemShoppinglist {

    @EmbeddedId
    private GroceryItemShoppinglistId id;

    /**
     * Constructs a new GroceryItemShoppinglist object with the given Shoppinglist, GroceryItem, and amount.
     *
     * @param shoppinglist the Shoppinglist where the GroceryItem is listed.
     * @param groceryItem the GroceryItem being added to the Shoppinglist.
     * @param amount the quantity of the GroceryItem in the Shoppinglist.
     */
    public GroceryItemShoppinglist(Shoppinglist shoppinglist, GroceryItem groceryItem, double amount) {
        this.shoppinglist = shoppinglist;
        this.groceryItem = groceryItem;
        this.id = new GroceryItemShoppinglistId(shoppinglist.getShoppinglistId(), groceryItem.getGroceryItemId(), LocalDateTime.now());
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
    private double amount;

    /**
     * Retrieves the identifier of the associated GroceryItem.
     *
     * @return the identifier of the associated GroceryItem.
     */
    public Long getGroceryItemId() {
        return groceryItem.getGroceryItemId();
    }

    /**
     * Retrieves the timestamp representing when the GroceryItem was added to the Shoppinglist.
     *
     * @return the timestamp of when the GroceryItem was added to the Shoppinglist.
     */
    public LocalDateTime getTimestamp() {
        return id.getTimestamp();
    }
}

/**
 * Represents the composite primary key for the GroceryItemShoppingList entity.
 * This key is composed of the ShoppingList and GroceryItem identifiers as well as a timestamp to ensure uniqueness
 * and maintain the relationship between a ShoppingList and its required GroceryItems.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
class GroceryItemShoppinglistId implements Serializable {
    private Long shoppinglistId;
    private Long groceryItemId;
    private LocalDateTime timestamp;

    /**
     * Retrieves the timestamp for the GroceryItemShoppinglistId, which represents the time when the GroceryItem was
     * added to the Shoppinglist.
     *
     * @return the timestamp for the GroceryItemShoppinglistId.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
