package edu.ntnu.idatt2106_09.backend.dto;

        import edu.ntnu.idatt2106_09.backend.model.Category;
        import jakarta.persistence.Column;
        import jakarta.persistence.GeneratedValue;
        import jakarta.persistence.GenerationType;
        import jakarta.persistence.Id;
        import lombok.Getter;
        import lombok.Setter;
        import lombok.NoArgsConstructor;
        import lombok.AllArgsConstructor;
        import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long category;
    private String name;
    private String unit;
}
