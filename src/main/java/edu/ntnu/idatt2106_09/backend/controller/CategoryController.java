package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.ntnu.idatt2106_09.backend.model.Category;
;

import java.util.Set;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "Category management operations")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all categories", description = "Fetch all categories from the database")
    @ApiResponse(responseCode = "204", description = "Categories table empty")
    @ApiResponse(responseCode = "200", description = "Ok")
    public ResponseEntity<Set<CategoryDto>> getAllCategories() {
        log.debug("[X] Call to return all categories");
        return categoryService.getAllCategories();
    }

    @GetMapping("/{groceryItemId}")
    @Operation(summary = "Get category by ID", description = "Fetch a specific category by its ID")
    @ApiResponse(responseCode = "404", description = "Categories table empty")
    @ApiResponse(responseCode = "200", description = "Ok")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long categoryId) {
        log.debug("[X] Call to return a category item by id");
        return categoryService.getCategoryById(categoryId);
    }

    @PutMapping("/update/{groceryItemId}")
    @Operation(summary = "Update category", description = "Update an existing category")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "200", description = "Ok")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto updatedCategoryDto) {
        log.debug("[X] Call to update a category item with id = {}", categoryId);
        return categoryService.updateCategory(categoryId, updatedCategoryDto);
    }

    @DeleteMapping("/delete/{groceryItemId}")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "204", description = "No content")
    @Operation(summary = "Delete category", description = "Delete an existing category by using categoryId")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
        log.debug("[X] Call to delete a category item with id = {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }
}
