package edu.ntnu.idatt2106_09.backend.dto;

import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
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
