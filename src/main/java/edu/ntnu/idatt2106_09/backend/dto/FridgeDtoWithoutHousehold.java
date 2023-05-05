package edu.ntnu.idatt2106_09.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FridgeDtoWithoutHousehold {
    private Long fridgeId;
    private String name;
}
