package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "grocery_item")
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grocery_item_id")
    private Long groceryItemId;

    @Column(name = "name")
    private String name;

    @Column(name = "expected_shelf_life")
    private int expectedShelfLife;

    @Column(name = "actual_shelf_life")
    private int actualShelfLife;

    @Column(name = "image_link")
    private String imageLink;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;


    /**
    @OneToMany(
            mappedBy = "groceryItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<GroceryItemFridge> fridges = new HashSet<>();
    */

    /**
    public void addFridge(Fridge fridge) {
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(fridge,this);
        fridges.add(groceryItemFridge);
        fridge.getGroceries().add(groceryItemFridge);
    }
     */

    /**
    public void removeFridge(Fridge fridge) {
        for (Iterator<GroceryItemFridge> iterator = fridges.iterator();
             iterator.hasNext(); ) {
            GroceryItemFridge groceryItemFridge = iterator.next();

            if (groceryItemFridge.getGroceryItem().equals(this) &&
                    groceryItemFridge.getFridge().equals(fridge)) {
                iterator.remove();
                groceryItemFridge.getFridge().getGroceries().remove(groceryItemFridge);
                groceryItemFridge.setGroceryItem(null);
                groceryItemFridge.setFridge(null);
            }
        }
    }
     */
}
