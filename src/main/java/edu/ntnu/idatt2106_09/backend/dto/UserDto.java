package edu.ntnu.idatt2106_09.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private int id;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
}
