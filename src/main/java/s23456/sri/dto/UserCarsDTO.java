package s23456.sri.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import s23456.sri.model.User;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCarsDTO extends RepresentationModel<UserCarsDTO> {
    private UserDTO userDTO;
    private Set<CarDto> carDtoSet;

}
