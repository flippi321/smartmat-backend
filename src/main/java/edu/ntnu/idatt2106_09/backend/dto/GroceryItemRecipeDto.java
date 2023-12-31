package edu.ntnu.idatt2106_09.backend.dto;

import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroceryItemRecipeDto {
    private RecipeDTO recipe;
    private GroceryItemDto groceryItem;
    private double amount;


    public GroceryItemRecipeDto(GroceryItemDto groceryItemDto, int amount) {
        this.groceryItem = groceryItemDto;
        this.amount = amount;
    }
}
