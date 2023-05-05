package edu.ntnu.idatt2106_09.backend.service.category;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Category;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Interface defining methods for interacting with Category objects.
 * It provides methods for retrieving, updating, and deleting categories, as well as operations involving the conversion
 * between Category and CategoryDto objects.
 *
 * Implementations of this interface should handle the necessary logic for fetching data from repositories and mapping
 * between different object representations.
 */
@Service
public interface CategoryService {

    /**
     * Retrieves all the categories available and returns them as a set of CategoryDto objects wrapped in a
     * ResponseEntity.
     *
     * @return A ResponseEntity containing a set of CategoryDto objects representing all the categories available.
     *         If the set is empty, the HTTP status is set to NO_CONTENT, otherwise, the HTTP status is set to OK.
     */
    public ResponseEntity<Set<CategoryDto>> getAllCategories();

    /**
     * Retrieves a category by its ID and returns the category as a CategoryDto object wrapped in a ResponseEntity.
     *
     * @param categoryId The ID of the category to be fetched.
     * @return A ResponseEntity containing a CategoryDto object representing the requested category.
     *         If the category is found, the HTTP status is set to OK.
     * @throws NotFoundException if the category with the given ID is not found.
     */
    public ResponseEntity<CategoryDto> getCategoryById(Long categoryId);

    /**
     * Updates a category with the provided categoryId using the information from the updatedCategoryDto object.
     * The method only updates the fields that are not null in the updatedCategoryDto.
     *
     * @param categoryId The ID of the category to be updated.
     * @param updatedCategoryDto A CategoryDto object containing the updated information for the category.
     * @return A ResponseEntity with the appropriate HTTP status.
     *         If the category is updated successfully, the HTTP status is set to OK.
     *         If the category with the given ID is not found, the HTTP status is set to NOT_FOUND.
     */
    public ResponseEntity<CategoryDto> updateCategory(Long categoryId, CategoryDto updatedCategoryDto);

    /**
     * Deletes a category with the provided categoryId.
     *
     * @param categoryId The ID of the category to be deleted.
     * @return A ResponseEntity with the appropriate HTTP status.
     *         If the category is deleted successfully, the HTTP status is set to OK.
     *         If the category with the given ID is not found, the HTTP status is set to NOT_FOUND.
     */
    public ResponseEntity<CategoryDto> deleteCategory(Long categoryId);
}
