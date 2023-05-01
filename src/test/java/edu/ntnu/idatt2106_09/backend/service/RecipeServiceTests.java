//Det jeg gjør nå er å skrive tester for ulike deler av programmet. Jeg og Hadar deler på oppgaven. Når man velger en del,
//så skriver man tester for modellen, servicen, repositoryet. Jeg var nesten ferdig med RecipeServiceTests
//(utenom tester for selve algoritmen - siden den gjør så mye viktig logikk) men så var det gjort oppdateringer på
//development-branchen, og nå må jeg endre testene slik at de matcher. Surya sa btw at det er methods man skal se på
//når det gjelder coverage.

package edu.ntnu.idatt2106_09.backend.service;


import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemFridgeAlgoDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.IngredientDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemFridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRecipeRepository;
import edu.ntnu.idatt2106_09.backend.repository.RecipeRepository;

import edu.ntnu.idatt2106_09.backend.service.RecipeService;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeService;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemServiceImplementation;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;



// @ExtendWith(MockitoExtension.class) enables us to remove everything that has to do with 'autoClosable'.
@ExtendWith(MockitoExtension.class)
class RecipeServiceTests {


    //jeg hopper over å lage test for addRecipe. det ser ut som den fungerer i postman og jeg får ikke testen til å gå.
    //begynn å rette ioo i RecipeTests før RecipeServiceTests





    @Test
    void getRecipeAndAllIngredientsTest() {
        Long recipeId = 1L;
        GroceryItemRecipeRepository groceryItemRecipeRepository = mock(GroceryItemRecipeRepository.class);
        RecipeRepository recipeRepository = mock(RecipeRepository.class);
        RecipeService recipeService = new RecipeService(recipeRepository, groceryItemRecipeRepository);

        // Oppsett av mock-objekter
        Recipe recipe = new Recipe();
        recipe.setRecipe_id(recipeId);
        recipe.setName("Test Recipe");
        recipe.setDescription("This is a test recipe");
        given(recipeRepository.findById(recipeId)).willReturn(Optional.of(recipe));

        Set<GroceryItemRecipe> ingredients = new HashSet<>();
        GroceryItemRecipe gir1 = new GroceryItemRecipe();
        gir1.setAmount(2);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        groceryItem1.setName("Test Ingredient 1");
        gir1.setGroceryItem(groceryItem1);
        ingredients.add(gir1);

        GroceryItemRecipe gir2 = new GroceryItemRecipe();
        gir2.setAmount(3);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        groceryItem2.setName("Test Ingredient 2");
        gir2.setGroceryItem(groceryItem2);
        ingredients.add(gir2);

        given(groceryItemRecipeRepository.findGroceryItemRecipeByRecipeId(recipeId)).willReturn(ingredients);

        // Kjør metoden som testes
        ResponseEntity<Object> response = recipeService.getRecipeAndAllIngredients(recipeId);

        // Sjekk at responsen er som forventet
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        RecipeResponseDTO result = (RecipeResponseDTO) response.getBody();
        assertThat(result.getId()).isEqualTo(recipeId);
        assertThat(result.getName()).isEqualTo("Test Recipe");
        assertThat(result.getDescription()).isEqualTo("This is a test recipe");
        assertThat(result.getIngredients().size()).isEqualTo(2);

        //siden lista med groceryItems som returneres er uordnet
        List<IngredientDTO> sortedIngredients = result.getIngredients().stream()
                .sorted(Comparator.comparingLong(IngredientDTO::getId))
                .collect(Collectors.toList());

        IngredientDTO ingredient1 = sortedIngredients.get(0);
        assertThat(ingredient1.getName()).isEqualTo("Test Ingredient 1");
        assertThat(ingredient1.getAmount()).isEqualTo(2);
        assertThat(ingredient1.getId()).isEqualTo(1L);
        assertThat(ingredient1.getUnit()).isEqualTo("unit");

        IngredientDTO ingredient2 = sortedIngredients.get(1);
        assertThat(ingredient2.getName()).isEqualTo("Test Ingredient 2");
        assertThat(ingredient2.getAmount()).isEqualTo(3);
        assertThat(ingredient2.getId()).isEqualTo(2L);
        assertThat(ingredient2.getUnit()).isEqualTo("unit");

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

        RecipeService recipeService = new RecipeService(groceryItemFridgeRepository, fridgeRepository,
                fridgeService, groceryItemService, modelMapper);

        // Oppsett av mock-objekter
        Fridge fridge = new Fridge();
        Long fridgeId = 1L;
        fridge.setFridgeId(fridgeId);
        given(fridgeRepository.findById(fridgeId)).willReturn(Optional.of(fridge));

        Set<GroceryItemFridge> groceryItemFridge = new HashSet<>();
        GroceryItemFridge gif1 = new GroceryItemFridge();
        gif1.setAmount(2);
        gif1.setExpirationDate(LocalDate.now());
        gif1.setPurchaseDate(LocalDate.now());
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        groceryItem1.setName("Test Grocery Item 1");
        gif1.setGroceryItem(groceryItem1);
        groceryItemFridge.add(gif1);

        GroceryItemFridge gif2 = new GroceryItemFridge();
        gif2.setAmount(3);
        gif2.setExpirationDate(LocalDate.now());
        gif2.setPurchaseDate(LocalDate.now());
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        groceryItem2.setName("Test Grocery Item 2");
        gif2.setGroceryItem(groceryItem2);
        groceryItemFridge.add(gif2);

        given(groceryItemFridgeRepository.findAllByFridgeId(fridgeId)).willReturn(groceryItemFridge);

        // Kjør metoden som testes
        HashMap<Long, GroceryItemFridgeAlgoDto> result = recipeService.retrieveFridgeItemsHashMap(fridgeId);

        // Sjekk at resultatet er som forventet
        assertThat(result.size()).isEqualTo(2);

        GroceryItemFridgeAlgoDto groceryItemFridgeDTO1 = result.get(1L);
        assertThat(groceryItemFridgeDTO1.getAmount()).isEqualTo(2);
        assertThat(groceryItemFridgeDTO1.getExpirationDate()).isEqualTo(LocalDate.now());
        assertThat(groceryItemFridgeDTO1.getPurchaseDate()).isEqualTo(LocalDate.now());
        assertThat(groceryItemFridgeDTO1.getFridgeDto().getFridgeId()).isEqualTo(fridgeId);
        assertThat(groceryItemFridgeDTO1.getGroceryItem().getName()).isEqualTo("Test Grocery Item 1");

        GroceryItemFridgeAlgoDto groceryItemFridgeDTO2 = result.get(2L);
        assertThat(groceryItemFridgeDTO2.getAmount()).isEqualTo(3);
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
        RecipeService recipeService = new RecipeService(recipeRepository, modelMapper);

        // Opprett testdata
        Recipe recipe1 = new Recipe();
        recipe1.setRecipe_id(1L);
        recipe1.setName("Recipe 1");
        recipe1.setDescription("Description 1");

        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        groceryItem1.setName("Grocery Item 1");

        GroceryItemRecipe gir1 = new GroceryItemRecipe(recipe1, groceryItem1);
        gir1.setAmount(2);

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
        assertEquals(2, result.get(0).get(0).getAmount());
    }



    @Test
    public void convertToRecipeResponseDTOTest() {
        // Create mock data
        RecipeService recipeService = new RecipeService();
        RecipeDTO recipeDTO = new RecipeDTO(1L, "Spaghetti Carbonara", "Delicious pasta dish");
        GroceryItemDto groceryItemDTO = new GroceryItemDto();
        groceryItemDTO.setGroceryItemId(1L);
        groceryItemDTO.setName("Pasta");
        groceryItemDTO.setExpected_shelf_life(6);
        GroceryItemRecipeDto groceryItemRecipeDTO = new GroceryItemRecipeDto(recipeDTO, groceryItemDTO, 400);

        List<List<GroceryItemRecipeDto>> groceryItemRecipeDTOList = new ArrayList<>();
        groceryItemRecipeDTOList.add(Collections.singletonList(groceryItemRecipeDTO));

        // Call the method to test
        List<RecipeResponseDTO> result = recipeService.convertToRecipeResponseDTO(groceryItemRecipeDTOList);

        // Assert the result
        assertEquals(1, result.size());
        RecipeResponseDTO recipeResponseDTO = result.get(0);
        assertEquals(recipeDTO.getRecipe_id(), recipeResponseDTO.getId());
        assertEquals(recipeDTO.getName(), recipeResponseDTO.getName());
        assertEquals(recipeDTO.getDescription(), recipeResponseDTO.getDescription());
        List<IngredientDTO> ingredients = recipeResponseDTO.getIngredients();
        assertEquals(1, ingredients.size());
        IngredientDTO ingredientDTO = ingredients.get(0);
        assertEquals(groceryItemDTO.getGroceryItemId(), ingredientDTO.getId());
        assertEquals(groceryItemDTO.getName(), ingredientDTO.getName());
        assertEquals(groceryItemRecipeDTO.getAmount(), ingredientDTO.getAmount());
    }

    //Arnold hadde glemt å legge til utløpsdato i algoritmen, og de metodene som har med oppskriftsgenerering er er veldig
    //avhengig av hverandre, så jeg begynner



}



