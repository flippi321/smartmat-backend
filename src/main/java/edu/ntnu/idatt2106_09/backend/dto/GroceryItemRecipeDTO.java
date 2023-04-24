package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroceryItemRecipeDTO {
    private RecipeDTO recipe;
    private GroceryItemDTO groceryItem;
    private int amount;
}
