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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }
/*
    @Test
    void getAllCategories() {
        // Given
        Category category1 = new Category();
        Category category2 = new Category();
        entityManager.persist(category1);
        entityManager.persist(category2);

        // When
        Set<Category> categories = categoryRepository.getAllCategories();

        // Then
        assertThat(categories).containsExactlyInAnyOrder(category1, category2);
    }

 */
}
