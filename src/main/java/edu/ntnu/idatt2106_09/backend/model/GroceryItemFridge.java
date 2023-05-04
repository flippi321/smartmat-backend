package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "fridge")
@Entity
@Table(name = "grocery_item_fridge")
public class GroceryItemFridge {

    @EmbeddedId
    private GroceryItemFridgeId id;

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

    public Long getGroceryItemId() {
        return groceryItem.getGroceryItemId();
    }

    public LocalDateTime getTimestamp() {
        return id.getTimestamp();
    }
}

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
