package edu.ntnu.idatt2106_09.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroceryItemShoppinglistDto extends GroceryItemDto {
    private int amount;
}
