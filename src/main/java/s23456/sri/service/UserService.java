package s23456.sri.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import s23456.sri.dto.CarDto;
import s23456.sri.dto.RentDTO;
import s23456.sri.dto.UserCarsDTO;
import s23456.sri.dto.UserDTO;
import s23456.sri.model.Car;
import s23456.sri.model.User;
import s23456.sri.repo.CarRepository;
import s23456.sri.repo.UserRepository;
import s23456.sri.rest.CarController;
import s23456.sri.rest.UserController;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CarRepository carRepository;

    private User convertToEntity(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }
    @Transactional
    public ResponseEntity<UserCarsDTO> findUserById(Long id) {
        UserCarsDTO userCarsDTO = new UserCarsDTO();
        UserDTO userDto = new UserDTO();
        RentDTO rentDto = new RentDTO();
        var user = userRepository.findUserById(id);
        if (user.isPresent()) {
            //userCarsDTO.add(Link.of("http://localhost:8080/api/users/"+id));
            userCarsDTO.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
            userCarsDTO.add(linkTo(methodOn(UserController.class).rentCar(rentDto)).withRel("rent"));
            userCarsDTO.add(linkTo(methodOn(UserController.class).returnCar(rentDto)).withRel("return"));
            userCarsDTO.add(linkTo(methodOn(UserController.class).getCarsBysUser(id)).withRel("getCarsByUserId"));
            userCarsDTO.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("deleteUser"));
            userCarsDTO.add(linkTo(methodOn(UserController.class).updateUser(id,userDto)).withRel("update"));
            userCarsDTO.setUserDTO(convertToDto(user.get()));
            userCarsDTO.setCarDtoSet(user.get().getCars().stream().map(this::convertToDto).collect(Collectors.toSet()));

            return ResponseEntity.ok(userCarsDTO);
        }
        return new ResponseEntity<>(null,
                HttpStatus.NOT_FOUND);
    }
    @Transactional
    public ResponseEntity<Collection<UserDTO>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<UserDTO> result = allUsers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        for(UserDTO userDto : result){
            userDto.add(linkTo(methodOn(UserController.class).getUser(userDto.getId())).withSelfRel());
            userDto.add(linkTo(methodOn(UserController.class).updateUser(userDto.getId(),userDto)).withRel("update"));
            userDto.add(linkTo(methodOn(UserController.class).deleteUser(userDto.getId())).withRel("delete"));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<Set<CarDto>> findCarsByUserId(Long id) {
        var user = userRepository.findUserById(id);
        if (user.isPresent()) {
            var cars = user.get().getCars().stream().map(this::convertToDto).collect(Collectors.toSet());
            if(!cars.isEmpty()){
                return ResponseEntity.ok(cars);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null,
                HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<String>  saveNewUser(UserDTO userDTO){
            User entity = convertToEntity(userDTO);
            userRepository.save(entity);
            HttpHeaders headers = new HttpHeaders();
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(entity.getId())
                    .toUri();
            headers.add("Location", location.toString());
            return new ResponseEntity(headers, HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity updateUser(@PathVariable Long
                                             userId, @RequestBody UserDTO userDto) {
        Optional<User> currentEmp =
                userRepository.findById(userId);
        if (currentEmp.isPresent()) {
            userDto.setId(userId);
            User entity = convertToEntity(userDto);
            userRepository.save(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity deleteUser(@PathVariable Long userId) {
        if (userRepository.existsById(userId)) {
            var user = userRepository.findUserById(userId);
                var cars = user.get().getCars().stream().map(this::convertToDto).collect(Collectors.toSet());
                if (cars.isEmpty()) {
                    userRepository.deleteById(userId);
                    return new ResponseEntity(HttpStatus.OK);
                }
                return new ResponseEntity<>("Oddaj auto!!!!", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<String> rentCarId(RentDTO rentDTO) {
        var car = carRepository.findById(rentDTO.getCarId());
        if (car.isPresent()) {
            if (car.get().getRenter() == null) {
                var user = userRepository.findUserById(rentDTO.getUserId());
                if (user.isPresent()) {
                    car.get().setRenter(user.get());
                    return ResponseEntity.ok("Wypozyczono samochod");
                }
                return new ResponseEntity<>("Nie ma takiego uzytkownika",
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Samochod juz wypozyczony",
                    HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Nie ma takiego samochodu",
                HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<String> returnCar(RentDTO rentDTO) {
        var car = carRepository.findById(rentDTO.getCarId());
        if (car.isPresent()) {
            var user = userRepository.findUserById(rentDTO.getUserId());
            if(user.isPresent()) {
                if(car.get().getRenter().equals(user.get())) {
                    car.get().setRenter(null);
                    return ResponseEntity.ok("Zwrocono samochod");
                }
                return new ResponseEntity<>("to nie ten samochod",
                        HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>("Nie ma takiego uzytkownika",
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Nie ma takiego samochodu",
                HttpStatus.NOT_FOUND);
    }




    private CarDto convertToDto(Car e) {
        return modelMapper.map(e, (Type) CarDto.class);
    }

    private UserDTO convertToDto(User e) {
        return modelMapper.map(e, (Type) UserDTO.class);
    }
}
