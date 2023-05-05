package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the association between a Fridge and a GroceryItem, including the amount of the items in the fridge and
 * its expiration date. This class is used to keep track of each GroceryItem's details within a specific Fridge.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "fridge")
@Entity
@Table(name = "grocery_item_fridge")
public class GroceryItemFridge {

    @EmbeddedId
    private GroceryItemFridgeId id;

    /**
     * Constructs a new GroceryItemFridge object with the given Fridge, GroceryItem, and amount.
     * Sets the purchase date to the current date and calculates the expiration date based on the GroceryItem's actual
     * shelf life.
     *
     * @param fridge the Fridge where the GroceryItem is stored.
     * @param groceryItem the GroceryItem being stored in the Fridge.
     * @param amount the quantity of the GroceryItem in the Fridge.
     */
    public GroceryItemFridge(Fridge fridge, GroceryItem groceryItem, double amount) {
        this.fridge = fridge;
        this.groceryItem = groceryItem;
        this.id = new GroceryItemFridgeId(fridge.getFridgeId(), groceryItem.getGroceryItemId(), LocalDateTime.now());
        this.amount = amount;
        this.purchaseDate = LocalDate.now();
        this.expirationDate = LocalDate.now().plusDays(groceryItem.getActualShelfLife());
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fridgeId")
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groceryItemId")
    @JoinColumn(name = "grocery_item_id")
    private GroceryItem groceryItem;

    @Column(name = "amount")
    private double amount;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate = LocalDate.now();

    @Column(name = "expiration_date")
    private LocalDate expirationDate = LocalDate.now();

    /**
     * Retrieves the identifier of the associated GroceryItem.
     *
     * @return the identifier of the associated GroceryItem
     */
    public Long getGroceryItemId() {
        return groceryItem.getGroceryItemId();
    }

    /**
     * Retrieves the timestamp representing when the GroceryItem was added to the Fridge.
     *
     * @return the timestamp of when the GroceryItem was added to the Fridge
     */
    public LocalDateTime getTimestamp() {
        return id.getTimestamp();
    }
}

/**
 * Represents the composite primary key for the GroceryItemFridge entity.
 * This key is composed of the Fridge and GroceryItem identifiers as well as a timestamp to ensure uniqueness and
 * maintain the relationship between a Fridge and its contained GroceryItems.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
class GroceryItemFridgeId implements Serializable {
    private Long fridgeId;
    private Long groceryItemId;
    private LocalDateTime timestamp;

    /**
     * Retrieves the timestamp for the GroceryItemFridgeId, which represents the time when the GroceryItem was added to
     * the Fridge.
     *
     * @return the timestamp for the GroceryItemFridgeId.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
