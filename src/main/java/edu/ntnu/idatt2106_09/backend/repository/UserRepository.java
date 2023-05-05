package edu.ntnu.idatt2106_09.backend.repository;

import java.util.Optional;
import java.util.Set;

import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 This interface extends JpaRepository for User and Integer.
 It contains methods to find a user by email and find all users in a household.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Returns an optional containing the user with the given email, or an empty optional if not found.
     *
     * @param email The email of the user to find.
     * @return An optional containing the user with the given email, or an empty optional if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Returns a set of all users in the given household.
     *
     * @param household The household to find users for.
     * @return A set of all users in the given household.
     */
    @Query("SELECT u FROM User u WHERE u.household = :household")
    Set<User> findAllByHousehold(@Param("household") Household household);

}
