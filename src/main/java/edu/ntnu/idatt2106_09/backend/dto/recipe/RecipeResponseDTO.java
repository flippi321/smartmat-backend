package edu.ntnu.idatt2106_09.backend.dto.recipe;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponseDTO extends RecipeDTO{
    private List<IngredientDTO> ingredients;
}

