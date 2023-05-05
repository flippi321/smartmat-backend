package edu.ntnu.idatt2106_09.backend.config;

import lombok.NoArgsConstructor;

/**
 * A class containing constants used by the RecipeRecommender class.
 */
@NoArgsConstructor
public class RecipeRecommenderConstants {
    // Recommends recipes with a threshold of the value below
    // Example 0.4 = 40% ingredient and fridge match
    public static final double RECOMMENDATION_THRESHOLD = 0.4;
    public static final double EXPIRATION_WEIGHT_CONSTANT = 0.1;
    public static final double EXPIRED_WEIGHT_CONSTANT = 0.1;
}
