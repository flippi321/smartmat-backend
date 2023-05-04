package edu.ntnu.idatt2106_09.backend.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a registration request containing the user's first name, last name, email, and password.
 * This class is used to store and transfer the user's registration information when creating a new account.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
