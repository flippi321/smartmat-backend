package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
public class GroceryItemRepositoryTests {
    @Autowired
    private GroceryItemRepository underTest;

}
