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
public class FridgeServiceImplementation implements FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Fridge addFridge(Fridge fridge) {
        return fridgeRepository.save(fridge);
    }

    @Override
    public FridgeDto addFridge(FridgeDto fridgeDTO) {
        Fridge newFridge = modelMapper.map(fridgeDTO, Fridge.class);
        return modelMapper.map(fridgeRepository.save(newFridge), FridgeDto.class);
    }

    @Override
    public Optional<Fridge> getFridgeById(Long fridgeId) {
        return fridgeRepository.findById(fridgeId);
    }

    @Override
    public Set<Fridge> getAllFridges() {
        return fridgeRepository.getAllFridges();
    }

    @Override
    public Fridge updateFridge(Fridge fridge) {
        return fridgeRepository.save(fridge);
    }

    @Override
    public void deleteFridge(Long fridgeId) {
        fridgeRepository.deleteById(fridgeId);
    }

    @Override
    public Fridge addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.addGroceryItem(groceryItem);
            return fridgeRepository.save(fridge);
        }
        return null;
    }

    @Override
    public Fridge removeGroceryItemFromFridge(Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.removeGroceryItem(groceryItem);
            return fridgeRepository.save(fridge);
        }
        return null;
    }
}