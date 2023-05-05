package edu.ntnu.idatt2106_09.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Represents a category of grocery items in the application.
 * Each grocery item belongs to a category, which provides useful information such as the type of item and the unit of
 * measurement.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long category;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;
}
