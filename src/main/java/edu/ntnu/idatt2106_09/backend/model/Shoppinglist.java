package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a shopping list containing a collection of grocery items to be purchased.
 * The ShoppingList class stores a list of grocery items and their quantities needed for purchase. It enables management
 * of the shopping list, such as adding or removing items, and updating quantities as needed.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "groceries")
@ToString
@Entity
@Table(name = "shoppinglist")
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Shoppinglist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shoppinglist_id", nullable = false)
    private Long shoppinglistId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "household_id", referencedColumnName = "household_id", unique = true)
    private Household household;

    @OneToMany(
            mappedBy = "shoppinglist",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<GroceryItemShoppinglist> groceries = new HashSet<>();

    /**
     * Adds a grocery item to the shopping list with a specified quantity. This method creates a new
     * GroceryItemShoppinglist object with the current shopping list, the provided grocery item and the given quantity,
     * then adds it to the groceries list.
     *
     * @param groceryItem The grocery item to be added to the shopping list.
     * @param amount The quantity of the grocery item to be added.
     */
    public void addGroceryItem(GroceryItem groceryItem, double amount, int actualShelfLife) {
        GroceryItemShoppinglist groceryItemShoppinglist = new GroceryItemShoppinglist(this, groceryItem, amount, actualShelfLife);
        groceries.add(groceryItemShoppinglist);
    }

    /**
     * Removes a grocery item from the shopping list based on the provided timestamp. This method iterates through the
     * groceries list to find the GroceryItemShoppinglist object with the specified grocery item and timestamp, removes
     * it from the list, and sets its shopping list and grocery item references to null.
     *
     * @param groceryItem The grocery item to be removed from the shopping list.
     * @param timestamp The LocalDateTime object representing the timestamp of the grocery item to be removed.
     */
    public void removeGroceryItem(GroceryItem groceryItem, LocalDateTime timestamp) {
        for (Iterator<GroceryItemShoppinglist> iterator = groceries.iterator();
             iterator.hasNext(); ) {
            GroceryItemShoppinglist groceryItemShoppinglist = iterator.next();

            if (groceryItemShoppinglist.getShoppinglist().equals(this) &&
                    groceryItemShoppinglist.getGroceryItem().equals(groceryItem) &&
                    groceryItemShoppinglist.getTimestamp().equals(timestamp)) {
                iterator.remove();
                groceryItemShoppinglist.setShoppinglist(null);
                groceryItemShoppinglist.setGroceryItem(null);
            }
        }
    }

    public void updateGroceryItem(GroceryItem groceryItem, double amount, int actualShelfLife, LocalDateTime timestamp) {
        for (GroceryItemShoppinglist groceryItemShoppinglist : groceries) {
            if (groceryItemShoppinglist.getShoppinglist().equals(this) &&
                    groceryItemShoppinglist.getGroceryItem().equals(groceryItem) &&
                    groceryItemShoppinglist.getTimestamp().equals(timestamp)) {
                groceryItemShoppinglist.setAmount(amount);
                groceryItemShoppinglist.setActualShelfLife(actualShelfLife);

                return;
            }
        }
    }
}
