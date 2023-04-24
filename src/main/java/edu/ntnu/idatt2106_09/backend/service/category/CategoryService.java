package edu.ntnu.idatt2106_09.backend.service.category;

import edu.ntnu.idatt2106_09.backend.model.Category;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface CategoryService {

    public Set<Category> getAllCategories();

    public Category addCategory(Category category);

    public Category updateCategory(Long id, Category updatedCategory);

    public void deleteCategory(Long id);
}
