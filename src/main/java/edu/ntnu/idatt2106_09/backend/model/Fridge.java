package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Fridge")
public class Fridge {
    @EmbeddedId
    private FridgeId fridgeId;

    @ManyToOne
    @MapsId("householdId")
    @JoinColumn(name = "household_id")
    private Household household;

}
