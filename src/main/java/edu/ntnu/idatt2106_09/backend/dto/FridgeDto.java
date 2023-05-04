package edu.ntnu.idatt2106_09.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FridgeDto {
    private Long fridgeId;
    private String name;
    private HouseholdDto household;
}