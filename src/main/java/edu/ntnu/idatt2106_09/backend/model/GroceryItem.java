package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a grocery item in the application.
 * A grocery item contains information such as its name, expected shelf life, actual shelf life, image link, and
 * associated category. Grocery items can be added to fridges and shopping lists, and used in recipes.
 */
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
     * Constructs a new GroceryItem with the specified ID and name.
     *
     * @param groceryItemId The unique ID of the grocery item.
     * @param name The name of the grocery item.
     */
    public GroceryItem(long groceryItemId, String name) {
        this.groceryItemId = groceryItemId;
        this.name = name;
    }

    /**
     * Constructs a new GroceryItem with the specified ID, name, and actual shelf life.
     *
     * @param groceryItemId The unique ID of the grocery item.
     * @param name The name of the grocery item.
     * @param actualShelfLife The actual shelf life of the grocery item, in days.
     */
    public GroceryItem(long groceryItemId, String name, int actualShelfLife) {
        this.groceryItemId = groceryItemId;
        this.name = name;
        this.actualShelfLife = actualShelfLife;
    }
}
