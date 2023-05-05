package edu.ntnu.idatt2106_09.backend.service.shoppinglist;

        import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
        import edu.ntnu.idatt2106_09.backend.dto.ShoppinglistDto;
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

@Service
public interface ShoppinglistService {

    /**
     * Adds a new shopping list to a given household.
     *
     * @param shoppinglistDto a ShoppinglistDto object containing the shopping list information to be added.
     * @return a ShoppinglistDto object containing the information of the newly created shopping list.
     * @throws BadRequestException if the name of the shopping list is empty, the household does not exist, the shopping
     *         list already exists for the household, or if the household already has a shopping list.
     */
    public ShoppinglistDto addShoppinglist(ShoppinglistDto shoppinglistDto);

    /**
     * Update a shopping list with the given ShoppinglistDto object.
     *
     * @param shoppinglistDto the ShoppinglistDto object to update the shopping list with
     * @return the updated ShoppinglistDto object
     * @throws BadRequestException if the ID or name of the shopping list is null or empty, or if the shopping list is
     *         not found
     */
    public ShoppinglistDto updateShoppinglist(ShoppinglistDto shoppinglistDto);

    /**
     * Retrieves a ShoppinglistDto by its unique ID.
     *
     * @param shoppinglistId the ID of the shoppinglist to retrieve
     * @return a ShoppinglistDto representing the shoppinglist with the given ID
     * @throws NotFoundException if no shoppinglist is found with the given ID
     */
    public ShoppinglistDto getShoppinglistById(Long shoppinglistId);

    /**
     * Deletes the Shoppinglist with the given ID. If the Shoppinglist is associated with a Household, the Household's
     * shoppinglist will be set to null and updated in the database after the Shoppinglist is deleted.
     *
     * @param shoppinglistId the ID of the Shoppinglist to be deleted
     * @throws NotFoundException if no Shoppinglist with the given ID is found in the database
     */
    public void deleteShoppinglist(Long shoppinglistId);
}
