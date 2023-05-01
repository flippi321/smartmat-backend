package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.service.category.CategoryService;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<Set<CategoryDto>> getAllCategories() {
        log.debug("[X] Call to return all grocery items");
        return categoryService.getAllCategories();
    }

    @GetMapping("/{groceryItemId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryId) {
        log.debug("[X] Call to return a grocery item by id");
        return categoryService.getCategoryById(categoryId);
    }

    @PutMapping("/update/{groceryItemId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto updatedCategoryDto) {
        log.debug("[X] Call to update a category item with id = {}", categoryId);
        return categoryService.updateCategory(categoryId, updatedCategoryDto);
    }

    @DeleteMapping("/delete/{groceryItemId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
        log.debug("[X] Call to delete a category item with id = {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }
}
