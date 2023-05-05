package edu.ntnu.idatt2106_09.backend.service.fridge;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The FridgeService interface provides methods for interacting with the FridgeRepository and Fridge objects in the
 * database. The interface specifies methods for adding, retrieving, updating and deleting Fridge objects, as well as
 * mapping Fridge objects to and from FridgeDto objects using the ModelMapper library.
 */
@Service
public interface FridgeService {

    /**
     * Adds a new Fridge to the database. This method validates the input FridgeDto, converts it to a Fridge object,
     * associates it with the specified household, and persists it to the database.
     *
     * @param fridgeDTO A FridgeDto object representing the new Fridge to be added.
     * @return A FridgeDto object representing the new Fridge that was added to the database.
     * @throws BadRequestException if the FridgeDto name is empty or null, if the household ID does not exist in the database,
     *         if a Fridge with the same name already exists for the household, or if the household already has a fridge.
     */
    public FridgeDto addFridge(FridgeDto fridgeDTO);

    /**
     * Updates an existing Fridge in the database. This method validates the input FridgeDto, retrieves the existing
     * Fridge from the database, updates its properties, and saves the changes back to the database.
     *
     * @param fridgeDto A FridgeDto object representing the Fridge to be updated.
     * @return A FridgeDto object representing the updated Fridge in the database.
     * @throws BadRequestException if the FridgeDto ID or name is empty or null, if the Fridge does not exist in the
     *         database, if the household ID does not exist in the database, or if a Fridge with the same name already
     *         exists for the household.
     */
    public FridgeDto updateFridge(FridgeDto fridgeDto);

    /**
     * Retrieves a Fridge from the database by its ID.
     *
     * @param fridgeId A Long representing the ID of the Fridge to retrieve.
     * @return A FridgeDto object representing the retrieved Fridge.
     * @throws NotFoundException if the Fridge with the given ID does not exist in the database.
     */
    public FridgeDto getFridgeById(Long fridgeId);

    /**
     * Deletes a Fridge from the database by its ID.
     *
     * @param fridgeId A Long representing the ID of the Fridge to delete.
     * @throws NotFoundException if the Fridge with the given ID does not exist in the database.
     */
    public void deleteFridge(Long fridgeId);
}
