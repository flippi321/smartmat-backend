package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "grocery_item")
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grocery_item_id")
    private Long groceryItemId;

    private String name;

    public GroceryItem(String name) {
        this.name = name;
    }

    @OneToMany(
            mappedBy = "grocery_item",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<GroceryItemFridge> fridgeSet = new HashSet<>();

    public void addFridge(Fridge fridge) {
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(fridge,this);
        fridgeSet.add(groceryItemFridge);
        fridge.getGroceryItemSet().add(groceryItemFridge);
    }

    public void removeFridge(Fridge fridge) {
        for (Iterator<GroceryItemFridge> iterator = fridgeSet.iterator();
             iterator.hasNext(); ) {
            GroceryItemFridge GroceryItemFridge = iterator.next();

            if (GroceryItemFridge.getGrocery_item().equals(this) &&
                    GroceryItemFridge.getFridge().equals(fridge)) {
                iterator.remove();
                GroceryItemFridge.getFridge().getGroceryItemSet().remove(GroceryItemFridge);
                GroceryItemFridge.setGrocery_item(null);
                GroceryItemFridge.setFridge(null);
            }
        }
    }
}
