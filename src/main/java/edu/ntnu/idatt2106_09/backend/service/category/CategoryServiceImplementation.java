package edu.ntnu.idatt2106_09.backend.service.category;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Category;
import edu.ntnu.idatt2106_09.backend.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    private CategoryDto castCategoryToDto(Category category){
        modelMapper = new ModelMapper();
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    /**
     * Retrieves all the categories available and returns them as a set of CategoryDto objects wrapped in a ResponseEntity.
     *
     * @return A ResponseEntity containing a set of CategoryDto objects representing all the categories available.
     *         If the set is empty, the HTTP status is set to NO_CONTENT, otherwise, the HTTP status is set to OK.
     */
    public ResponseEntity<Set<CategoryDto>> getAllCategories() {
        log.debug("Fetching all categories");
        Set<Category> allCategories = categoryRepository.getAllCategories();
        Set<CategoryDto> categoriesToBeReturned = new HashSet<>();
        for (Category category : allCategories) {
            CategoryDto categoryDto = castCategoryToDto(category);
            categoriesToBeReturned.add(categoryDto);
        }
        if (categoriesToBeReturned.isEmpty()) {
            return new ResponseEntity<>(categoriesToBeReturned, HttpStatus.NO_CONTENT);
        }
        log.info("[x] Categories found");
        return new ResponseEntity<>(categoriesToBeReturned, HttpStatus.OK);
    }

    @Override
    /**
     * Retrieves a category by its ID and returns the category as a CategoryDto object wrapped in a ResponseEntity.
     *
     * @param categoryId The ID of the category to be fetched.
     * @return A ResponseEntity containing a CategoryDto object representing the requested category.
     *         If the category is found, the HTTP status is set to OK.
     * @throws NotFoundException if the category with the given ID is not found.
     */
    public ResponseEntity<CategoryDto> getCategoryById(Long categoryId) {
        log.debug("Fetching Category with id: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id " + categoryId + " not found"));

        log.info("[x] Category with id {} found", categoryId);
        CategoryDto categoryDto = castCategoryToDto(category);
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @Override
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
    public ResponseEntity<CategoryDto> updateCategory(Long categoryId, CategoryDto updatedCategoryDto) {
        log.debug("Updating Category with id: {}", categoryId);
        Optional<Category> categoryToUpdate = categoryRepository.findById(categoryId);

        if (categoryToUpdate.isPresent()) {
            Category category = categoryToUpdate.get();
            if (updatedCategoryDto.getName() != null) {
                category.setName(updatedCategoryDto.getName());
            }
            if (updatedCategoryDto.getUnit() != null) {
                category.setUnit(updatedCategoryDto.getUnit());
            }
            categoryRepository.save(category);
        } else {
            log.warn("[x] Category with id {} not found for update request", categoryId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("[x] Category with id {} updated", categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    /**
     * Deletes a category with the provided categoryId.
     *
     * @param categoryId The ID of the category to be deleted.
     * @return A ResponseEntity with the appropriate HTTP status.
     *         If the category is deleted successfully, the HTTP status is set to OK.
     *         If the category with the given ID is not found, the HTTP status is set to NOT_FOUND.
     */
    public ResponseEntity<CategoryDto> deleteCategory(Long categoryId) {
        log.debug("Deleting Category with id: {}", categoryId);
        Optional<Category> categoryToDelete = categoryRepository.findById(categoryId);

        if (categoryToDelete.isPresent()) {
            categoryRepository.delete(categoryToDelete.get());
            categoryRepository.deleteById(categoryId);
            log.info("[x] Category with id {} deleted", categoryId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("[x] Category with id {} not found for delete request", categoryId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
