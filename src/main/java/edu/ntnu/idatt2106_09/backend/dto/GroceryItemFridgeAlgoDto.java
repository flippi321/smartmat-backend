package edu.ntnu.idatt2106_09.backend.dto;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroceryItemFridgeAlgoDto {
    private FridgeDto fridgeDto;
    private GroceryItemDto groceryItem;
    private double amount;
    private LocalDate purchaseDate;
    private LocalDate expirationDate;
    private LocalDateTime timeStamp;

    public GroceryItemFridgeAlgoDto(GroceryItemDto groceryItemDto, LocalDate expirationDate) {
        this.groceryItem = groceryItemDto;
        this.expirationDate = expirationDate;
    }
    public GroceryItemFridgeAlgoDto(GroceryItemDto groceryItemDto, LocalDate expirationDate, int amount) {
        this.groceryItem = groceryItemDto;
        this.expirationDate = expirationDate;
        this.amount = amount;
    }


}
