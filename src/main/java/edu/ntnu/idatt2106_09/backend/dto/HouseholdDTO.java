package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HouseholdDTO {

    private Long householdId;
    private String name;
    private FridgeDTO fridge;

}
