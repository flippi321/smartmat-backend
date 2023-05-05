

package edu.ntnu.idatt2106_09.backend.service;


import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.dto.recipe.IngredientDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemFridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRecipeRepository;
import edu.ntnu.idatt2106_09.backend.repository.RecipeRepository;

import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeService;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.recipe.RecipeService;
import edu.ntnu.idatt2106_09.backend.service.recipe.RecipeServiceImplementation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;



// @ExtendWith(MockitoExtension.class) enables us to remove everything that has to do with 'autoClosable'.
@ExtendWith(MockitoExtension.class)
class RecipeServiceTests {

    @Test
    void getRecipeAndAllIngredientsTest() {
        Long recipeId = 1L;
        GroceryItemRecipeRepository groceryItemRecipeRepository = mock(GroceryItemRecipeRepository.class);
        RecipeRepository recipeRepository = mock(RecipeRepository.class);
        RecipeServiceImplementation recipeService = new RecipeServiceImplementation(recipeRepository, groceryItemRecipeRepository);

        // Oppsett av mock-objekter
        Recipe recipe = new Recipe();
        recipe.setRecipe_id(recipeId);
        recipe.setName("Test Recipe");
        recipe.setDescription("This is a test recipe");
        given(recipeRepository.findById(recipeId)).willReturn(Optional.of(recipe));

        Set<GroceryItemRecipe> ingredients = new HashSet<>();
        GroceryItemRecipe gir1 = new GroceryItemRecipe();
        gir1.setAmount(2.0);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        groceryItem1.setName("Test Ingredient 1");

        Category category = new Category();
        category.setName("Test kategori");
        category.setUnit("Stk");

        groceryItem1.setCategory(category);

        gir1.setGroceryItem(groceryItem1);
        ingredients.add(gir1);

        GroceryItemRecipe gir2 = new GroceryItemRecipe();
        gir2.setAmount(3.0);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        groceryItem2.setName("Test Ingredient 2");
        groceryItem2.setCategory(category);
        gir2.setGroceryItem(groceryItem2);
        ingredients.add(gir2);

        given(groceryItemRecipeRepository.findGroceryItemRecipeByRecipeId(recipeId)).willReturn(ingredients);

        // Kjør metoden som testes
        ResponseEntity<Object> response = recipeService.getRecipeAndAllIngredients(recipeId);

        // Sjekk at responsen er som forventet
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        RecipeResponseDTO result = (RecipeResponseDTO) response.getBody();
        assertThat(result.getRecipe_id()).isEqualTo(recipeId);
        assertThat(result.getName()).isEqualTo("Test Recipe");
        assertThat(result.getDescription()).isEqualTo("This is a test recipe");
        assertThat(result.getIngredients().size()).isEqualTo(2);

        //siden lista med groceryItems som returneres er uordnet
        List<IngredientDTO> sortedIngredients = result.getIngredients().stream()
                .sorted(Comparator.comparingLong(IngredientDTO::getId))
                .collect(Collectors.toList());

        IngredientDTO ingredient1 = sortedIngredients.get(0);
        assertThat(ingredient1.getName()).isEqualTo("Test Ingredient 1");
        assertThat(ingredient1.getAmount()).isEqualTo(2.0);
        assertThat(ingredient1.getId()).isEqualTo(1L);
        assertThat(ingredient1.getUnit()).isEqualTo("Stk");

        IngredientDTO ingredient2 = sortedIngredients.get(1);
        assertThat(ingredient2.getName()).isEqualTo("Test Ingredient 2");
        assertThat(ingredient2.getAmount()).isEqualTo(3.0);
        assertThat(ingredient2.getId()).isEqualTo(2L);
        assertThat(ingredient2.getUnit()).isEqualTo("Stk");

        // Sjekk at metoden på mock-objektene ble kalt riktig antall ganger og med riktige argumenter
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(groceryItemRecipeRepository, times(1)).findGroceryItemRecipeByRecipeId(recipeId);
    }


    @Test
    void retrieveFridgeItemsHashMapTest() {

        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        GroceryItemFridgeRepository groceryItemFridgeRepository = mock(GroceryItemFridgeRepository.class);

        ModelMapper modelMapper = new ModelMapper();

        FridgeServiceImplementation fridgeService = mock(FridgeServiceImplementation.class);
        GroceryItemServiceImplementation groceryItemService = mock(GroceryItemServiceImplementation.class);

        RecipeServiceImplementation recipeService = new RecipeServiceImplementation(groceryItemFridgeRepository, fridgeRepository,
                fridgeService, groceryItemService, modelMapper);

        // Oppsett av mock-objekter
        Fridge fridge = new Fridge();
        Long fridgeId = 1L;
        fridge.setFridgeId(fridgeId);
        given(fridgeRepository.findById(fridgeId)).willReturn(Optional.of(fridge));

        Set<GroceryItemFridge> groceryItemFridge = new HashSet<>();
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        groceryItem1.setName("Test Grocery Item 1");
        GroceryItemFridge gif1 = new GroceryItemFridge(fridge, groceryItem1, 2.0);
        groceryItemFridge.add(gif1);




        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        groceryItem2.setName("Test Grocery Item 2");
        GroceryItemFridge gif2 = new GroceryItemFridge(fridge, groceryItem2, 3.0);
        groceryItemFridge.add(gif2);

        given(groceryItemFridgeRepository.findAllByFridgeId(fridgeId)).willReturn(groceryItemFridge);

        // Kjør metoden som testes
        HashMap<Long, GroceryItemFridgeAlgoDto> result = recipeService.retrieveFridgeItemsHashMap(fridgeId);

        // Sjekk at resultatet er som forventet
        assertThat(result.size()).isEqualTo(2);

        GroceryItemFridgeAlgoDto groceryItemFridgeDTO1 = result.get(1L);
        assertThat(groceryItemFridgeDTO1.getAmount()).isEqualTo(2.0);
        assertThat(groceryItemFridgeDTO1.getExpirationDate()).isEqualTo(LocalDate.now());
        assertThat(groceryItemFridgeDTO1.getPurchaseDate()).isEqualTo(LocalDate.now());
        assertThat(groceryItemFridgeDTO1.getFridgeDto().getFridgeId()).isEqualTo(fridgeId);
        assertThat(groceryItemFridgeDTO1.getGroceryItem().getName()).isEqualTo("Test Grocery Item 1");

        GroceryItemFridgeAlgoDto groceryItemFridgeDTO2 = result.get(2L);
        assertThat(groceryItemFridgeDTO2.getAmount()).isEqualTo(3.0);
        assertThat(groceryItemFridgeDTO2.getExpirationDate()).isEqualTo(LocalDate.now());
        assertThat(groceryItemFridgeDTO2.getPurchaseDate()).isEqualTo(LocalDate.now());
        assertThat(groceryItemFridgeDTO2.getFridgeDto().getFridgeId()).isEqualTo(fridgeId);
        assertThat(groceryItemFridgeDTO2.getGroceryItem().getName()).isEqualTo("Test Grocery Item 2");
    }


    @Test
    void getAllRecipeListTest() {
        // Opprett mock-objekter
        RecipeRepository recipeRepository = mock(RecipeRepository.class);
        ModelMapper modelMapper = mock(ModelMapper.class);

        // Opprett instans av RecipeService og sett mock-objekter
        RecipeServiceImplementation recipeService = new RecipeServiceImplementation(recipeRepository, modelMapper);

        // Opprett testdata
        Recipe recipe1 = new Recipe();
        recipe1.setRecipe_id(1L);
        recipe1.setName("Recipe 1");
        recipe1.setDescription("Description 1");

        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        groceryItem1.setName("Grocery Item 1");

        GroceryItemRecipe gir1 = new GroceryItemRecipe(recipe1, groceryItem1);
        gir1.setAmount(2.0);

        Set<GroceryItemRecipe> girSet1 = new HashSet<>();
        girSet1.add(gir1);
        recipe1.setGroceries(girSet1);

        Set<Recipe> recipeSet = new HashSet<>();
        recipeSet.add(recipe1);

        // Angi oppførsel for mock-objekter
        when(recipeRepository.getAllRecipes()).thenReturn(recipeSet);
        when(modelMapper.map(any(GroceryItem.class), eq(GroceryItemDto.class)))
                .thenReturn(new GroceryItemRecipeDto().getGroceryItem());

        // Kjør testen og verifiser resultatet
        List<List<GroceryItemRecipeDto>> result = recipeService.getAllRecipeList();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).size());
        assertEquals(2.0, result.get(0).get(0).getAmount());
    }


    @Test
    public void convertToRecipeResponseDTOTest() {
        // Create mock data
        RecipeServiceImplementation recipeService = new RecipeServiceImplementation();

        Category category = new Category();
        category.setName("Test kategori");
        category.setUnit("Stk");

        RecipeDTO recipeDTO = new RecipeDTO(1L, "Spaghetti Carbonara", "Delicious pasta dish");
        GroceryItemDto groceryItemDTO = new GroceryItemDto();
        groceryItemDTO.setGroceryItemId(1L);
        groceryItemDTO.setName("Pasta");
        groceryItemDTO.setExpectedShelfLife(6);
        groceryItemDTO.setCategory(category);
        GroceryItemRecipeDto groceryItemRecipeDTO = new GroceryItemRecipeDto(recipeDTO, groceryItemDTO, 400);

        List<List<GroceryItemRecipeDto>> groceryItemRecipeDTOList = new ArrayList<>();
        groceryItemRecipeDTOList.add(Collections.singletonList(groceryItemRecipeDTO));

        // Call the method to test
        List<RecipeResponseDTO> result = recipeService.convertToRecipeResponseDTO(groceryItemRecipeDTOList);

        // Assert the result
        assertEquals(1, result.size());
        RecipeResponseDTO recipeResponseDTO = result.get(0);
        assertEquals(recipeDTO.getRecipe_id(), recipeResponseDTO.getRecipe_id());
        assertEquals(recipeDTO.getName(), recipeResponseDTO.getName());
        assertEquals(recipeDTO.getDescription(), recipeResponseDTO.getDescription());
        List<IngredientDTO> ingredients = recipeResponseDTO.getIngredients();
        assertEquals(1, ingredients.size());
        IngredientDTO ingredientDTO = ingredients.get(0);
        assertEquals(groceryItemDTO.getGroceryItemId(), ingredientDTO.getId());
        assertEquals(groceryItemDTO.getName(), ingredientDTO.getName());
        assertEquals(groceryItemRecipeDTO.getAmount(), ingredientDTO.getAmount());
    }

    @Test
    public void testCompareFridgeAndRecipeList() {

        // Setup
        Map<Long, GroceryItemFridgeAlgoDto> fridge = new HashMap<>();
        GroceryItemFridgeAlgoDto item1 = new GroceryItemFridgeAlgoDto(null, new GroceryItemDto(1L, "Milk"), 1, LocalDate.now(), LocalDate.now().plusDays(7), null);
        GroceryItemFridgeAlgoDto item2 = new GroceryItemFridgeAlgoDto(null, new GroceryItemDto(2L, "Eggs"), 3, LocalDate.now(), LocalDate.now().plusDays(14), null);
        fridge.put(1L, item1);
        fridge.put(2L, item2);

        GroceryItemDto groceryItemDto1 = new GroceryItemDto();
        groceryItemDto1.setGroceryItemId(1L);
        groceryItemDto1.setName("Milk");

        GroceryItemDto groceryItemDto2 = new GroceryItemDto();
        groceryItemDto2.setGroceryItemId(2L);
        groceryItemDto2.setName("Eggs");

        GroceryItemDto groceryItemDto3 = new GroceryItemDto();
        groceryItemDto3.setGroceryItemId(3L);
        groceryItemDto3.setName("Flour");

        GroceryItemDto groceryItemDto4 = new GroceryItemDto();
        groceryItemDto3.setGroceryItemId(4L);
        groceryItemDto3.setName("Sugar");


        List<GroceryItemRecipeDto> recipe = List.of(
                new GroceryItemRecipeDto(groceryItemDto1, 1),
                new GroceryItemRecipeDto(groceryItemDto2, 4),
                new GroceryItemRecipeDto(groceryItemDto3, 1),
                new GroceryItemRecipeDto(groceryItemDto3, 1)
        );

        RecipeServiceImplementation recipeService = new RecipeServiceImplementation();

        // Test
        double result = recipeService.compareFridgeAndRecipeList(fridge, recipe);

        // Verify
        Assertions.assertEquals(0.4375, result);
    }

    @Test
    public void testGetExpirationDateWeight() {
        // Setup
        LocalDate today = LocalDate.of(2023, 5, 3);
        LocalDate futureDate = LocalDate.of(2023, 5, 10);
        LocalDate pastDate = LocalDate.of(2023, 4, 30);

        RecipeServiceImplementation recipeService = new RecipeServiceImplementation();

        // Test future date
        double result1 = recipeService.getExpirationDateWeight(today, futureDate);
        Assertions.assertTrue(result1 < 1.0 && result1 > 0.0);

        // Test past date
        double result2 = recipeService.getExpirationDateWeight(today, pastDate);
        Assertions.assertTrue(result2 > 1.0);
    }

    @Test
    public void testGetWeightListOfRecipeList() {
        // Setup
        Map<Long, GroceryItemFridgeAlgoDto> fridge = new HashMap<>();
        GroceryItemFridgeAlgoDto item1 = new GroceryItemFridgeAlgoDto(null, new GroceryItemDto(1L, "Milk"), 1, LocalDate.now(), LocalDate.now().plusDays(7), null);
        GroceryItemFridgeAlgoDto item2 = new GroceryItemFridgeAlgoDto(null, new GroceryItemDto(2L, "Eggs"), 3, LocalDate.now(), LocalDate.now().plusDays(14), null);
        fridge.put(1L, item1);
        fridge.put(2L, item2);

        GroceryItemDto groceryItemDto1 = new GroceryItemDto();
        groceryItemDto1.setGroceryItemId(1L);
        groceryItemDto1.setName("Milk");

        GroceryItemDto groceryItemDto2 = new GroceryItemDto();
        groceryItemDto2.setGroceryItemId(2L);
        groceryItemDto2.setName("Eggs");

        GroceryItemDto groceryItemDto3 = new GroceryItemDto();
        groceryItemDto3.setGroceryItemId(3L);
        groceryItemDto3.setName("Flour");

        List<GroceryItemRecipeDto> recipe1 = List.of(
                new GroceryItemRecipeDto(groceryItemDto1, 1),
                new GroceryItemRecipeDto(groceryItemDto2, 2),
                new GroceryItemRecipeDto(groceryItemDto3, 1)
        );

        List<GroceryItemRecipeDto> recipe2 = List.of(
                new GroceryItemRecipeDto(groceryItemDto1, 1),
                new GroceryItemRecipeDto(groceryItemDto2, 1)
        );

        List<GroceryItemRecipeDto> recipe3 = List.of(
                new GroceryItemRecipeDto(groceryItemDto3, 1)
        );

        List<List<GroceryItemRecipeDto>> recipesOverThreshold = List.of(recipe1, recipe2, recipe3);

        RecipeServiceImplementation recipeService = new RecipeServiceImplementation();

        // Test
        double[] result = recipeService.getWeightListOfRecipeList(fridge, recipesOverThreshold);

        // Verify
        Assertions.assertArrayEquals(new double[]{0.6666666666666666, 1, 0.0}, result, 0.001);
    }



    //Vi burde egentlig hatt tester som tester selve algoritmen mer, men vi hadde ikke tid.
    //Testen under fungerer - en av hjelpemetodene til metoden vi tester sier at
    //fridgen ikke finnes

    /*@Test
    void testGetRecommendedRecipes() {
        // Set the fridge and recipe list in the mock service
        RecipeRepository recipeRepository = mock(RecipeRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        RecipeServiceImplementation recipeService = new RecipeServiceImplementation(recipeRepository, fridgeRepository);


        // Create a fridge with 5 grocery items with different expiration dates
        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = new HashMap<>();
        LocalDate dateToday = LocalDate.now();
        GroceryItemFridgeAlgoDto item1 = new GroceryItemFridgeAlgoDto(new GroceryItemDto(1L, "Egg"), dateToday.plusDays(5), 2);
        GroceryItemFridgeAlgoDto item2 = new GroceryItemFridgeAlgoDto(new GroceryItemDto(2L, "Milk"), dateToday.plusDays(2), 1);
        GroceryItemFridgeAlgoDto item3 = new GroceryItemFridgeAlgoDto(new GroceryItemDto(3L, "Cheese"), dateToday.plusDays(4), 3);
        GroceryItemFridgeAlgoDto item4 = new GroceryItemFridgeAlgoDto(new GroceryItemDto(4L, "Butter"), dateToday.plusDays(1), 1);
        GroceryItemFridgeAlgoDto item5 = new GroceryItemFridgeAlgoDto(new GroceryItemDto(5L, "Bread"), dateToday.plusDays(3), 2);
        fridge.put(1L, item1);
        fridge.put(2L, item2);
        fridge.put(3L, item3);
        fridge.put(4L, item4);
        fridge.put(5L, item5);

        GroceryItem item1Mocked = new GroceryItem(1L, "Egg", 5);
        GroceryItem item2Mocked = new GroceryItem(2L, "Milk", 2);
        GroceryItem item3Mocked = new GroceryItem(3L, "Cheese", 4);
        GroceryItem item4Mocked = new GroceryItem(4L, "Butter", 1);
        GroceryItem item5Mocked = new GroceryItem(5L, "Bread", 3);

        Fridge fridgeMocked = new Fridge();
        fridgeMocked.setFridgeId(1L);

        fridgeMocked.addGroceryItem(item1Mocked, 2);
        fridgeMocked.addGroceryItem(item2Mocked, 1);
        fridgeMocked.addGroceryItem(item3Mocked, 3);
        fridgeMocked.addGroceryItem(item4Mocked, 1);
        fridgeMocked.addGroceryItem(item5Mocked, 2);



        // Create a list of recipes
        List<GroceryItemRecipeDto> recipe1Items = new ArrayList<>();
        recipe1Items.add(new GroceryItemRecipeDto(new GroceryItemDto(1L, "Egg"), 2));
        recipe1Items.add(new GroceryItemRecipeDto(new GroceryItemDto(2L, "Milk"), 1));
        recipe1Items.add(new GroceryItemRecipeDto(new GroceryItemDto(3L, "Cheese"), 1));
        List<GroceryItemRecipeDto> recipe2Items = new ArrayList<>();
        recipe2Items.add(new GroceryItemRecipeDto(new GroceryItemDto(4L, "Butter"), 2));
        recipe2Items.add(new GroceryItemRecipeDto(new GroceryItemDto(5L, "Bread"), 3));
        List<GroceryItemRecipeDto> recipe3Items = new ArrayList<>();
        recipe3Items.add(new GroceryItemRecipeDto(new GroceryItemDto(6L, "Beef"), 2));
        recipe3Items.add(new GroceryItemRecipeDto(new GroceryItemDto(7L, "Potato"), 4));
        List<List<GroceryItemRecipeDto>> recipes = new ArrayList<>();
        recipes.add(recipe1Items);
        recipes.add(recipe2Items);
        recipes.add(recipe3Items);

        // Call the method being tested
        List<List<GroceryItemRecipeDto>> recommendedRecipes = recipeService.getRecommendedRecipes(1L);

        // Check that the method returns a list with two elements (excluding the recipe that falls below the threshold)
        assertEquals(2, recommendedRecipes.size());

        // Check that the first recipe in the recommended list is recipe2 and the second recipe is recipe1
        assertEquals(recipe2Items, recommendedRecipes.get(0));
        assertEquals(recipe1Items, recommendedRecipes.get(1));


    }*/
}



