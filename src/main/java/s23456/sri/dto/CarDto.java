package s23456.sri.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto extends RepresentationModel<CarDto> {
    private Long id;
    private String brand;
    private String color;
    private Year year;
}
