package s23456.sri.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentDTO extends RepresentationModel<RentDTO> {
    private Long carId;
    private Long userId;
}
