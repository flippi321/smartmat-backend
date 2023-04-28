package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.ntnu.idatt2106_09.backend.model.Category;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/categories")
@Tag(name = "Category Controller", description = "Category management operations")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all categories", description = "Fetch all categories from the database")
    public ResponseEntity<Set<CategoryDto>> getAllCategories() {
        log.debug("[X] Call to return all grocery items");
        return categoryService.getAllCategories();
    }

    @GetMapping("/{groceryItemId}")
    @Operation(summary = "Get category by ID", description = "Fetch a specific category by its ID")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryId) {
        log.debug("[X] Call to return a grocery item by id");
        return categoryService.getCategoryById(categoryId);
    }

    @PutMapping("/update/{groceryItemId}")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto updatedCategoryDto) {
        log.debug("[X] Call to update a category item with id = {}", categoryId);
        return categoryService.updateCategory(categoryId, updatedCategoryDto);
    }

    @Operation(summary = "Delete category", description = "Delete a category by its ID")
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
        log.debug("[X] Call to delete a category item with id = {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }
}
