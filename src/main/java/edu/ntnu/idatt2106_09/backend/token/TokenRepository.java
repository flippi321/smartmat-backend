package edu.ntnu.idatt2106_09.backend.token;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for a Token entity.
 */
public interface TokenRepository extends JpaRepository<Token, Integer> {

    /**
     * Find all valid tokens for a given user.
     *
     * @param id the id of the user to find tokens for.
     * @return a list of all valid tokens for the given user.
     */
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Integer id);

    /**
     * Find a token by its token value.
     *
     * @param token the value of the token to find.
     * @return an optional containing the token with the given value, or empty if no token is found.
     */
    Optional<Token> findByToken(String token);
}
