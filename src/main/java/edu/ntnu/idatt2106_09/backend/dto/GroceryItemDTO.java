package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroceryItemDTO implements Serializable {

    private Long groceryItemId;
    private String name;
    private int expectedExpirationDays;


}
