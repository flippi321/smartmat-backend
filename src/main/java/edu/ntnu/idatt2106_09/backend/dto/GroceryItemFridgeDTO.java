package edu.ntnu.idatt2106_09.backend.dto;

import java.time.LocalDate;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroceryItemFridgeDTO {
    private FridgeDTO fridgeDTO;
    private GroceryItemDTO groceryItem;
    private int amount;
    private LocalDate purchaseDate;
    private LocalDate expirationDate;
}