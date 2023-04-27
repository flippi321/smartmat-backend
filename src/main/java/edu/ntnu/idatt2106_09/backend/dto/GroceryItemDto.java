package edu.ntnu.idatt2106_09.backend.dto;

import edu.ntnu.idatt2106_09.backend.model.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroceryItemDto {
    private Long groceryItemId;
    private String name;
    private int expected_shelf_life;
    private Category category;
}
