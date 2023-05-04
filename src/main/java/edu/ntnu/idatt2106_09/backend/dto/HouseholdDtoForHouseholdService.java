package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HouseholdDtoForHouseholdService {

    private Long householdId;
    private String name;
    private long invitationNr;
    private FridgeDtoWithoutHousehold fridge;
    private ShoppinglistDto shoppinglist;
    private UserDto userDto;

}
