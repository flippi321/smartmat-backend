package edu.ntnu.idatt2106_09.backend.repository;

import java.util.Optional;

import edu.ntnu.idatt2106_09.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}