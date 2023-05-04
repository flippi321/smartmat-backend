package edu.ntnu.idatt2106_09.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.CategoryRepository;
import edu.ntnu.idatt2106_09.backend.service.category.CategoryServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImplementation categoryService;


    @Test
    public void CategoryService_GetAllCategories_ReturnCategoryDtoSet() {
        Set<Category> categories = new HashSet<>();
        categories.add(new Category(1L, "Fruit", "kg"));
        categories.add(new Category(2L, "Vegetable", "kg"));
        when(categoryRepository.getAllCategories()).thenReturn(categories);

        ResponseEntity<Set<CategoryDto>> categoriesReturn = categoryService.getAllCategories();

        assertThat(categoriesReturn).isNotNull();
        assertThat(categoriesReturn.getBody().size()).isEqualTo(2);
    }

    @Test
    public void CategoryService_GetCategoryById_ReturnCategoryDto() {
        Category category = new Category(1L, "Fruits", "kg");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<CategoryDto> categoryReturn = categoryService.getCategoryById(1L);

        assertThat(categoryReturn).isNotNull();
        assertThat(categoryReturn.getBody().getCategory()).isEqualTo(1L);
        assertThat(categoryReturn.getBody().getName()).isEqualTo("Fruits");
        assertThat(categoryReturn.getBody().getUnit()).isEqualTo("kg");
    }

    @Test
    public void CategoryService_UpdateCategory_ReturnHttpStatus() {
        Category category = new Category(1L, "Fruits", "kg");
        CategoryDto updatedCategoryDto = new CategoryDto(1L, "Fresh Fruits", "kg");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<CategoryDto> response = categoryService.updateCategory(1L, updatedCategoryDto);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(categoryRepository).save(category);
    }

    @Test
    public void CategoryService_DeleteCategory_ReturnHttpStatus() {
        Category category = new Category(1L, "Fruits", "kg");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<CategoryDto> response = categoryService.deleteCategory(1L);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(categoryRepository).deleteById(1L);
    }


}
