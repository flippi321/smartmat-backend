package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a household containing information about members and their associated fridge.
 * The Household class stores information about the members and their fridge, and enables management of grocery items
 * within the fridge.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "household")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "name")
    private String name;

    @Column(name = "invitationNr", unique = true)
    private Long invitationNr;

    @OneToOne(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private Fridge fridge;

    @OneToOne(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shoppinglist shoppinglist;
}
