package edu.ntnu.idatt2106_09.backend.service.fridge;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface FridgeService {

    public Fridge addFridge(Fridge fridge);

    public FridgeDto addFridge(FridgeDto fridgeDTO);

    public Optional<Fridge> getFridgeById(Long fridgeId);

    public Set<Fridge> getAllFridges();

    public Fridge updateFridge(Fridge fridge);

    public void deleteFridge(Long fridgeId);

    public Fridge addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem);

    public Fridge removeGroceryItemFromFridge(Long fridgeId, GroceryItem groceryItem);
}
