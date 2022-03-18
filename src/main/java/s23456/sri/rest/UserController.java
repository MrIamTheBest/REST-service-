package s23456.sri.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s23456.sri.dto.CarDto;
import s23456.sri.dto.RentDTO;
import s23456.sri.dto.UserCarsDTO;
import s23456.sri.dto.UserDTO;

import s23456.sri.service.UserService;


import java.util.Collection;
import java.util.Set;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserCarsDTO> getUser(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/cars/{userId}")
    public ResponseEntity<Set<CarDto>> getCarsBysUser(@PathVariable Long userId) {
        return userService.findCarsByUserId(userId);
    }

    @PutMapping("/cars/rent")
    public ResponseEntity<String> rentCar(@RequestBody RentDTO rentDTO) {
        return userService.rentCarId(rentDTO);
    }

    @PutMapping("/cars/return")
    public ResponseEntity<String> returnCar(@RequestBody RentDTO rentDTO) {
        return userService.returnCar(rentDTO);
    }

    @PostMapping
    public ResponseEntity saveNewUser(@RequestBody UserDTO c) {
        return userService.saveNewUser(c);
    }

    @PutMapping("/{userId}")
    public ResponseEntity updateUser(@PathVariable Long
                                            userId, @RequestBody UserDTO userDto) {
        return userService.updateUser(userId,userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
