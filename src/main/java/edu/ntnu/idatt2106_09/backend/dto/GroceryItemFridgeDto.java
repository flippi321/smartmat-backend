package edu.ntnu.idatt2106_09.backend.dto;

        import edu.ntnu.idatt2106_09.backend.model.Category;
        import edu.ntnu.idatt2106_09.backend.model.Fridge;
        import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
        import jakarta.persistence.*;
        import lombok.Getter;
        import lombok.Setter;
        import lombok.NoArgsConstructor;
        import lombok.AllArgsConstructor;
        import lombok.Builder;

        import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroceryItemFridgeDto {

    private Fridge fridge;
    private GroceryItem groceryItem;
    private int amount;
    private LocalDate purchaseDate;
    private LocalDate expirationDate;
}

