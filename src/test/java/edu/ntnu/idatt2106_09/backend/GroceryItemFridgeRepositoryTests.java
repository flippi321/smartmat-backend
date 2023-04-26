package edu.ntnu.idatt2106_09.backend;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
@DataJdbcTest
public class GroceryItemFridgeRepositoryTests {
    @Autowired
    private GroceryItemFridgeRepository underTest;

    //eksempel på test. Metodenavnet må ikke nødvendigvis være det samme. I dette tilfellet er det fridge med id 2, og
    //groceryItem med id 6 som matches i mange-til-mange-tabellen. Nå er det ikke noe konstruktør i GroceryItemFridge,
    //så da vil testen uansett faile.
    @Test
    void checkIfAddedGroceryItemExistsTest(){
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(2, 6);

        underTest.save(groceryItemFridge);

        boolean expected = underTest.exists(6);

        assertThat(expected).isTrue();
    }
}
*/
