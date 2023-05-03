package edu.ntnu.idatt2106_09.backend.dto;

import edu.ntnu.idatt2106_09.backend.model.Category;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class GroceryItemDto implements Serializable {

    private Long groceryItemId;
    private String name;
    private int expectedShelfLife;
    private int actualShelfLife;
    private String imageLink;
    private Category category;

    public GroceryItemDto(long groceryItemId, String name) {
        this.groceryItemId = groceryItemId;
        this.name = name;
    }
}
