package edu.ntnu.idatt2106_09.backend.repository;

        import edu.ntnu.idatt2106_09.backend.model.Fridge;
        import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
        import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.Modifying;
        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.query.Param;
        import org.springframework.stereotype.Repository;

        import java.util.Optional;
        import java.util.Set;
/**
 * Repository interface for managing shopping lists.
 */
@Repository
public interface ShoppinglistRepository extends JpaRepository<Shoppinglist, Long> {
    /**
     * Returns a set of all shopping lists.
     *
     * @return a set of all shopping lists.
     */
    @Query("SELECT s FROM Shoppinglist s")
    Set<Shoppinglist> getAllShoppinglists();

    /**
     * Returns a shopping list with a given name and household id.
     *
     * @param name        the name of the shopping list.
     * @param householdId the id of the household the shopping list belongs to.
     * @return a shopping list with the given name and household id.
     */
    @Query("SELECT f FROM Shoppinglist f WHERE f.name = :name AND f.household.householdId = :householdId")
    Shoppinglist findByNameAndHouseholdId(@Param("name") String name, @Param("householdId") Long householdId);


    /**

     Finds a shopping list with the given name and household id, excluding the shopping list with the given id.
     @param name the name of the shopping list
     @param householdId the id of the household that the shopping list belongs to
     @param shoppinglistId the id of the shopping list to exclude from the search
     @return the shopping list with the given name and household id, excluding the shopping list with the given id
     */
    @Query("SELECT f FROM Shoppinglist f WHERE f.name = :name AND f.household.householdId = :householdId AND f.shoppinglistId != :shoppinglistId")
    Shoppinglist findByNameAndHouseholdIdExcludeShoppinglistId(String name, Long householdId, Long shoppinglistId);

    /**
     * Returns a shopping list with a given household id.
     *
     * @param householdId the id of the household.
     * @return a shopping list with the given household id.
     */
    @Query("SELECT f FROM Shoppinglist f WHERE f.household.householdId = :householdId")
    Optional<Shoppinglist> findByHouseholdId(@Param("householdId") Long householdId);

    /**
     * Returns a shopping list with a given household id as a Shoppinglist object.
     *
     * @param householdId the id of the household.
     * @return a shopping list with the given household id as a Shoppinglist object.
     */
    @Query("SELECT s FROM Shoppinglist s WHERE s.household.householdId = :householdId")
    Shoppinglist findByHouseholdIdAsShoppinglist(@Param("householdId") Long householdId);

}
