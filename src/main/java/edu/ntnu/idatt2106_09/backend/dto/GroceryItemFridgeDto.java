package edu.ntnu.idatt2106_09.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroceryItemFridgeDto extends GroceryItemDto {
    private double amount;
    private long days_since_purchase;
    private long days_until_spoilt;
}

