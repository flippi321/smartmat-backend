package edu.ntnu.idatt2106_09.backend.service.category;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.model.Category;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface CategoryService {
    public ResponseEntity<Set<CategoryDto>> getAllCategories();

    public ResponseEntity<Object> getCategoryById(Long categoryId);

    public ResponseEntity<CategoryDto> updateCategory(Long categoryId, CategoryDto updatedCategoryDto);

    public ResponseEntity<CategoryDto> deleteCategory(Long categoryId);
}
