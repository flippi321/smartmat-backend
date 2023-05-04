package edu.ntnu.idatt2106_09.backend.dto.recipe;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppinglistDtoWithoutHousehold {
    private Long shoppinglistID;
    private String name;
    private Set<GroceryItemDto> groceries = new HashSet<>();
}
