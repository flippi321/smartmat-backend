package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShoppinglistRepositoryTest {

    @Autowired
    private ShoppinglistRepository shoppinglistRepository;

    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void tearDown() {
        shoppinglistRepository.deleteAll();
    }

    @Test
    void getAllShoppinglists() {
        Shoppinglist shoppinglist1 = new Shoppinglist();
        shoppinglist1.setName("Shoppinglist1");
        Shoppinglist shoppinglist2 = new Shoppinglist();
        shoppinglist2.setName("Shoppinglist2");
        entityManager.persist(shoppinglist1);
        entityManager.persist(shoppinglist2);

        Set<Shoppinglist> shoppinglists = shoppinglistRepository.getAllShoppinglists();

        assertThat(shoppinglists).containsExactlyInAnyOrder(shoppinglist1, shoppinglist2);
    }

    @Test
    void findByNameAndHouseholdId() {
        Household household1 = new Household();
        entityManager.persist(household1);
        Household household2 = new Household();
        entityManager.persist(household2);

        Shoppinglist shoppinglist1 = new Shoppinglist();
        shoppinglist1.setName("Shoppinglist1");
        shoppinglist1.setHousehold(household1);
        entityManager.persist(shoppinglist1);

        Shoppinglist shoppinglist2 = new Shoppinglist();
        shoppinglist2.setName("Shoppinglist2");
        shoppinglist2.setHousehold(household2);
        entityManager.persist(shoppinglist2);

        Shoppinglist foundShoppinglist = shoppinglistRepository.findByNameAndHouseholdId("Shoppinglist1", household1.getHouseholdId());

        assertThat(foundShoppinglist).isEqualTo(shoppinglist1);
    }
}
