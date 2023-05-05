package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a fridge in the application associated with a specific household.
 * A fridge contains a collection of grocery items and their corresponding details, such as quantity, purchase date,
 * and expiration date. It also provides methods for adding, removing, and updating grocery items within the fridge.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "groceries")
@ToString
@Entity
@Table(name = "fridge")
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fridge_id", nullable = false)
    private Long fridgeId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "household_id", referencedColumnName = "household_id", unique = true)
    private Household household;

    @OneToMany(
            mappedBy = "fridge",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<GroceryItemFridge> groceries = new HashSet<>();

    /**
     * Adds a grocery item to the fridge along with the specified amount.
     * This method creates a new GroceryItemFridge object to associate the item with the fridge.
     *
     * @param groceryItem The grocery item to be added to the fridge.
     * @param amount The quantity of the grocery item to be added.
     */
    public void addGroceryItem(GroceryItem groceryItem, double amount, LocalDate expirationDate) {
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(this, groceryItem, amount, expirationDate);
        groceries.add(groceryItemFridge);
    }

    /**
     * Removes a grocery item from the fridge based on the provided timestamp.
     * This method iterates through the groceries list and removes the specified item with the given timestamp.
     *
     * @param groceryItem The grocery item to be removed from the fridge.
     * @param timestamp The timestamp of the grocery item to be removed.
     */
    public void removeGroceryItem(GroceryItem groceryItem, LocalDateTime timestamp) {
        for (Iterator<GroceryItemFridge> iterator = groceries.iterator();
             iterator.hasNext(); ) {
            GroceryItemFridge groceryItemFridge = iterator.next();

            if (groceryItemFridge.getFridge().equals(this) &&
                    groceryItemFridge.getGroceryItem().equals(groceryItem) &&
                    groceryItemFridge.getTimestamp().equals(timestamp)) {
                iterator.remove();
                groceryItemFridge.setFridge(null);
                groceryItemFridge.setGroceryItem(null);
            }
        }
    }

    /**
     * Updates the grocery item's amount, actual shelf life, and expiration date in the fridge based on the provided timestamp.
     *
     * @param groceryItem The grocery item to be updated.
     * @param amount The new amount for the grocery item.
     * @param timestamp The timestamp of the grocery item to be updated.
     */
    public void updateGroceryItem(GroceryItem groceryItem, double amount, LocalDate expirationDate, LocalDateTime timestamp) {
        for (GroceryItemFridge groceryItemFridge : groceries) {
            if (groceryItemFridge.getFridge().equals(this) &&
                    groceryItemFridge.getGroceryItem().equals(groceryItem) &&
                    groceryItemFridge.getTimestamp().equals(timestamp)) {
                    groceryItemFridge.setAmount(amount);
                    groceryItemFridge.setExpirationDate(expirationDate);

                return;
            }
        }
    }
}
