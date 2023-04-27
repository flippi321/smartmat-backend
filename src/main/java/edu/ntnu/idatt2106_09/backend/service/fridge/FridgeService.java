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

    public FridgeDto addFridge(FridgeDto fridgeDTO);

    public FridgeDto getFridgeById(Long fridgeId);

    public FridgeDto updateFridge(FridgeDto fridgeDto);

    public void deleteFridge(Long fridgeId);
}
