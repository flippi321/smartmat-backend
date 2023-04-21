package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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

    @NaturalId
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "household_id", referencedColumnName = "household_id")
    private Household household;

    @OneToMany(
            mappedBy = "fridge",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<GroceryItemFridge> groceries = new HashSet<>();

    public void addGroceryItem(GroceryItem groceryItem) {
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(this, groceryItem);
        groceries.add(groceryItemFridge);
        groceryItem.getFridges().add(groceryItemFridge);
    }

    public void removeGroceryItem(GroceryItem groceryItem) {
        for (Iterator<GroceryItemFridge> iterator = groceries.iterator();
             iterator.hasNext(); ) {
            GroceryItemFridge groceryItemFridge = iterator.next();

            if (groceryItemFridge.getFridge().equals(this) &&
                    groceryItemFridge.getGroceryItem().equals(groceryItem)) {
                iterator.remove();
                groceryItemFridge.getGroceryItem().getFridges().remove(groceryItemFridge);
                groceryItemFridge.setFridge(null);
                groceryItemFridge.setGroceryItem(null);
            }
        }
    }
}
