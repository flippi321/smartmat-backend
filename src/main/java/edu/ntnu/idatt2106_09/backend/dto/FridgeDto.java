package edu.ntnu.idatt2106_09.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FridgeDto {

    private Long fridgeId;
    private String name;
    private HouseholdDto household;
    private Set<GroceryItemDto> groceries = new HashSet<>();
}