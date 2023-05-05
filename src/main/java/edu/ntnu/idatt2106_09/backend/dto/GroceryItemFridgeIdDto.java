package edu.ntnu.idatt2106_09.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroceryItemFridgeIdDto {
    private Long fridgeId;
    private Long groceryItemId;
    private LocalDateTime timestamp;
}
