package com.usecases.spring.car;

import com.usecases.spring.validator.groups.Create;
import com.usecases.spring.validator.groups.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.usecases.spring.utils.LinkBuilder.carLink;

@RestController
@RequestMapping("/brands/{brandId}/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping()
    public ResponseEntity<Link> save(@PathVariable Long brandId, @Validated(Create.class) @RequestBody CarRepresentation car) {
        Long id = carService.save(brandId, car);
        return ResponseEntity.status(HttpStatus.CREATED).body(carLink(id, brandId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarRepresentation> getById(@PathVariable Long brandId, @PathVariable("id") Long id) {
        CarRepresentation rep = CarRepresentation.of(carService.getById(id));
        rep.add(carLink(id, brandId));
        return ResponseEntity.ok(rep);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Link> update(@PathVariable Long brandId, @PathVariable Long id, @Validated(Update.class) @RequestBody CarRepresentation car) {
        carService.update(id, car);
        return ResponseEntity.ok(carLink(id, brandId));
    }
}
