package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppinglistDto {

    private Long shoppinglistID;
    private String name;
    private Set<GroceryItemDto> groceries = new HashSet<>();
}
