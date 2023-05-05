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

    public GroceryItem(long groceryItemId, String name) {
        this.groceryItemId = groceryItemId;
        this.name = name;
    }

    public GroceryItem(long groceryItemId, String name, int actualShelfLife) {
        this.groceryItemId = groceryItemId;
        this.name = name;
        this.actualShelfLife = actualShelfLife;
    }

}
