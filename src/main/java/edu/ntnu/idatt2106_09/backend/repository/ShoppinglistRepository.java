package edu.ntnu.idatt2106_09.backend.repository;

        import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
        import org.springframework.data.jpa.repository.JpaRepository;

        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.query.Param;
        import org.springframework.stereotype.Repository;

        import java.util.Optional;
        import java.util.Set;

@Repository
public interface ShoppinglistRepository extends JpaRepository<Shoppinglist, Long> {

    @Query("SELECT s FROM Shoppinglist s")
    Set<Shoppinglist> getAllShoppinglists();

    @Query("SELECT f FROM Shoppinglist f WHERE f.name = :name AND f.household.householdId = :householdId")
    Shoppinglist findByNameAndHouseholdId(@Param("name") String name, @Param("householdId") Long householdId);

    @Query("SELECT f FROM Shoppinglist f WHERE f.name = :name AND f.household.householdId = :householdId AND f.shoppinglistId != :shoppinglistId")
    Shoppinglist findByNameAndHouseholdIdExcludeShoppinglistId(String name, Long householdId, Long shoppinglistId);

    @Query("SELECT f FROM Shoppinglist f WHERE f.household.householdId = :householdId")
    Optional<Shoppinglist> findByHouseholdId(@Param("householdId") Long householdId);
}
