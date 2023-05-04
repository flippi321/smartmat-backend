package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
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

    public void addGroceryItem(GroceryItem groceryItem, int amount) {
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(this, groceryItem, amount);
        groceries.add(groceryItemFridge);
    }

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

    public void updateGroceryItem(GroceryItem groceryItem, int amount, int actualShelfLife, boolean negative) {
        for (GroceryItemFridge groceryItemFridge : groceries) {
            if (groceryItemFridge.getGroceryItem().equals(groceryItem)) {
                groceryItemFridge.setAmount(amount);
                if(negative == false){
                    groceryItemFridge.setExpirationDate(groceryItemFridge.getExpirationDate().plusDays(actualShelfLife));
                }else if(negative == true) {
                    groceryItemFridge.setExpirationDate(groceryItemFridge.getExpirationDate().minusDays(actualShelfLife));
                }
                return;
            }
        }
    }
}
