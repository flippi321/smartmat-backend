package edu.ntnu.idatt2106_09.backend.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {
    private Long recipe_id;
    private String name;
    private String description;
    private String imageLink;
    private List<String> steps;

    public RecipeDTO(Long recipe_id, String name, String description){
        this.recipe_id = recipe_id;
        this.name = name;
        this.description = description;
    }
}