package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Household")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "household_id")
    private int householdId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<Fridge> fridges;
}
