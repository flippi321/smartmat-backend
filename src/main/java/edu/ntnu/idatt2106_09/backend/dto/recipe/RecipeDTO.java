package edu.ntnu.idatt2106_09.backend.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {
    private Long recipe_id;
    private String name;
    private String description;
}