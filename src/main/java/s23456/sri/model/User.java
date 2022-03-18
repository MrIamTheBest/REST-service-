package s23456.sri.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "renter")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Car> cars;
}
