package edu.ntnu.idatt2106_09.backend.service.user;

import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    public ResponseEntity<UserDto> updateUser(String oldEmail, UserDto userDto);
    public ResponseEntity<UserDto> getUserById(Integer id);
}
