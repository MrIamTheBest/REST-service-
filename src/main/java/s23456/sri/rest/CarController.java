package s23456.sri.rest;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import s23456.sri.dto.CarDto;
import s23456.sri.model.Car;
import s23456.sri.repo.CarRepository;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/cars")
public class CarController {
    private CarRepository carRepository;
    private ModelMapper modelMapper;

    public CarController(CarRepository carRepository, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
    }

    private CarDto convertToDto(Car e) {
        return modelMapper.map(e, (Type) CarDto.class);
    }

    private Car convertToEntity(CarDto dto) {
        return modelMapper.map(dto, Car.class);
    }

    @GetMapping
    public ResponseEntity<Collection<CarDto>> getCars() {
        List<Car> allCars = carRepository.findAll();

        List<CarDto> result = allCars.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        for(CarDto carDto : result){
            carDto.add(linkTo(methodOn(CarController.class).getCarById(carDto.getId())).withSelfRel());
            carDto.add(linkTo(methodOn(CarController.class).updateCar(carDto.getId(),carDto)).withRel("update"));
            carDto.add(linkTo(methodOn(CarController.class).deleteCar(carDto.getId())).withRel("delete"));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{carId}")
    public ResponseEntity<CarDto>
    getCarById(@PathVariable Long carId) {
        Optional<Car> c =
                carRepository.findById(carId);
        if (c.isPresent()) {
            CarDto carDto = convertToDto(c.get());
            carDto.add(linkTo(methodOn(CarController.class).getCarById(carId)).withSelfRel());
            carDto.add(linkTo(methodOn(CarController.class).updateCar(carId,carDto)).withRel("update"));
            carDto.add(linkTo(methodOn(CarController.class).deleteCar(carId)).withRel("delete"));
            return new ResponseEntity<>(carDto,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null,
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity saveNewCar(@RequestBody CarDto c) {
        Car entity = convertToEntity(c);
        carRepository.save(entity);
        HttpHeaders headers = new HttpHeaders();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getId())
                .toUri();
        headers.add("Location", location.toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{carId}")
    public ResponseEntity updateCar(@PathVariable Long
                                            carId, @RequestBody CarDto carDto) {
        Optional<Car> currentEmp =
                carRepository.findById(carId);
        if (currentEmp.isPresent()) {
            carDto.setId(carId);
            Car entity = convertToEntity(carDto);
            carRepository.save(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity deleteCar(@PathVariable Long carId) {
        if (carRepository.existsById(carId)) {
            carRepository.deleteById(carId);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
