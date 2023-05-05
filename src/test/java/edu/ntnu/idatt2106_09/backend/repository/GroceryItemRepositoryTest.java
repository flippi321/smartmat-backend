package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Category;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.Household;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

@DataJpaTest
class GroceryItemRepositoryTest {

    @Autowired
    private GroceryItemRepository groceryItemRepository;
    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void tearDown() {
        groceryItemRepository.deleteAll();
    }
/*
    @Test
    void getAllGroceryItems() {
        // Given
        Category category = new Category();
        entityManager.persist(category);
        GroceryItem item1 = new GroceryItem(null, "item1", 7, 8, "test", category);
        GroceryItem item2 = new GroceryItem(null, "item2", 14, 15, "test", category);
        entityManager.persist(item1);
        entityManager.persist(item2);

        // When
        Set<GroceryItem> items = groceryItemRepository.getAllGroceryItems();

        // Then
        assertThat(items).containsExactlyInAnyOrder(item1, item2);
    }

 */
}
