package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "fridge")
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fridge_id", nullable = false)
    private Long fridgeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "household_id", referencedColumnName = "household_id")
    private Household household;

    @OneToMany(
            mappedBy = "fridge",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<GroceryItemFridge> groceryItemList = new ArrayList<>();

    /**
    // Many to many connection to CalendarDate modelled by the "calendar" table (not modelled)
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
    @JoinTable(name = "grocery_item_fridge", joinColumns = { @JoinColumn(name = "fridge_id") }, inverseJoinColumns = {
            @JoinColumn(name = "grocery_item_id") })
    private Set<GroceryItem> items = new HashSet<>();
    */
}
