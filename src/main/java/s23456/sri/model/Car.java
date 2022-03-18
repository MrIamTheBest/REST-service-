package s23456.sri.model;

import lombok.*;

import javax.persistence.*;
import java.time.Year;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String brand;
    private String color;
    private Year year;
    @ManyToOne
    @JoinColumn(name="renter_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User renter;
}
