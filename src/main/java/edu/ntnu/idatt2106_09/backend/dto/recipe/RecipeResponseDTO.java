package edu.ntnu.idatt2106_09.backend.dto.recipe;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<IngredientDTO> ingredients;
}

