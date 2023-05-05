package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    public void addGroceryItem(GroceryItem groceryItem, double amount) {
        GroceryItemShoppinglist groceryItemShoppinglist = new GroceryItemShoppinglist(this, groceryItem, amount);
        groceries.add(groceryItemShoppinglist);
    }

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
}
