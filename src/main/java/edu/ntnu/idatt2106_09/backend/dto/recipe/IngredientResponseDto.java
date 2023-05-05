package edu.ntnu.idatt2106_09.backend.dto.recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponseDto extends IngredientDTO {
    private LocalDateTime timestamp;

}
