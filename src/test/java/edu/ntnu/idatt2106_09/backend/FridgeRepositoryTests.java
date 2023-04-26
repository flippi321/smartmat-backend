package edu.ntnu.idatt2106_09.backend;

import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
public class FridgeRepositoryTests {
    @Autowired
    private FridgeRepository underTest;

}
