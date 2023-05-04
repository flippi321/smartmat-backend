package edu.ntnu.idatt2106_09.backend.repository;

import java.util.Optional;
import java.util.Set;

import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.household = :household")
    Set<User> findAllByHousehold(@Param("household") Household household);

}
