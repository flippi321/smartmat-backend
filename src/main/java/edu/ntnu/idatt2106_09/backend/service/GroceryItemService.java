package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDTO;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroceryItemService {

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Get a grocery item by ID
    public Optional<GroceryItem> getGroceryItemById(Long groceryItemId) {
        return groceryItemRepository.findById(groceryItemId);
    }

    public GroceryItemDTO castGroceryItemDto(GroceryItem groceryItem) {
        return modelMapper.map(groceryItem, GroceryItemDTO.class);
    }


    // Other necessary methods for GroceryItemService can be added here as needed
}
