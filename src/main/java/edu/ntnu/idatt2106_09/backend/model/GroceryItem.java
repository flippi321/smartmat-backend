package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "grocery_item")
public class GroceryItem {

    @Id
    @GeneratedValue//(strategy = GenerationType.IDENTITY)
    @Column(name = "grocery_item_id")
    private Long groceryItemId;

    @OneToMany(
            mappedBy = "grocery_item",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<GroceryItemFridge> fridgeSet = new ArrayList<>();

    /**
    // Many-to-many connection with Ad. Ad is parent in this case.
    @ManyToMany(mappedBy = "items")
    private Set<Fridge> fridges = new HashSet<>();
    */
}
