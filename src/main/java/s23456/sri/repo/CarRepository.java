package s23456.sri.repo;

import org.springframework.data.repository.CrudRepository;
import s23456.sri.model.Car;

import java.util.List;

public interface CarRepository extends CrudRepository<Car, Long> {
    List<Car> findAll();
}
