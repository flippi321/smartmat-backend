package edu.ntnu.idatt2106_09.backend.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an authentication response containing the user's email, first name, last name, and ID.
 * This class is used to store and transfer the user's basic information upon successful authentication.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    //@JsonProperty("access_token")
    //private String accessToken;
    //@JsonProperty("refresh_token")
    //private String refreshToken;
    @JsonProperty("email")
    private String email;
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("id")
    private Integer id;
}
