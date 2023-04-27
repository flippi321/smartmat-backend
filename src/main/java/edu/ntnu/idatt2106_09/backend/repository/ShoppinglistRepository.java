package edu.ntnu.idatt2106_09.backend.repository;

        import edu.ntnu.idatt2106_09.backend.model.Fridge;
        import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
        import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.Modifying;
        import org.springframework.data.jpa.repository.Query;
        import org.springframework.data.repository.query.Param;
        import org.springframework.stereotype.Repository;

        import java.util.Set;

@Repository
public interface ShoppinglistRepository extends JpaRepository<Shoppinglist, Long> {

    @Query("SELECT s FROM Shoppinglist s")
    Set<Shoppinglist> getAllShoppinglists();
}
