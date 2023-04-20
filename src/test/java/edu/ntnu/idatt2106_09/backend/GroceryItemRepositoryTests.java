package edu.ntnu.idatt2106_09.backend;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemFridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
public class GroceryItemRepositoryTests {
    @Autowired
    private GroceryItemRepository underTest;

}
