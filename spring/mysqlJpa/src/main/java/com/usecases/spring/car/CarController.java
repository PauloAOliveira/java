package com.usecases.spring.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.usecases.spring.utils.LinkBuilder.carLink;

@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("/brands/{brandId}/cars")
    public ResponseEntity<Link> save(Long brandId, @Validated @RequestBody CarRepresentation car) {
        Long id = carService.save(brandId, car);
        return ResponseEntity.status(HttpStatus.CREATED).body(carLink(id));
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<CarRepresentation> getById(@PathVariable Long id) {
        CarRepresentation rep = CarRepresentation.of(carService.getById(id));
        rep.add(carLink(id));
        return ResponseEntity.ok(rep);
    }
}
