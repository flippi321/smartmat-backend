package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
/*
    @Test
    void findByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        entityManager.persist(user);

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findAllByHousehold() {
        Household household = new Household();
        entityManager.persist(household);
        User user1 = new User();
        user1.setHousehold(household);
        entityManager.persist(user1);
        User user2 = new User();
        user2.setHousehold(household);
        entityManager.persist(user2);

        Set<User> result = userRepository.findAllByHousehold(household);

        assertThat(result).containsExactlyInAnyOrder(user1, user2);
    }

 */
}
