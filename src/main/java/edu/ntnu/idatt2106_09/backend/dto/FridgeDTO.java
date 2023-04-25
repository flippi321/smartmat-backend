package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FridgeDTO {

    private Long fridgeId;
    private String name;
    private HouseholdDTO household;
    private Set<GroceryItemFridgeDTO> groceries = new HashSet<>();

}
