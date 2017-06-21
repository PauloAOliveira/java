package com.usecases.spring.car;

import com.usecases.spring.brand.Brand;
import com.usecases.spring.brand.BrandService;
import com.usecases.spring.exception.ConflictException;
import com.usecases.spring.exception.VersionConflictException;
import com.usecases.spring.validator.groups.Create;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.OptimisticLockException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BrandService brandService;

    @Validated(Create.class)
    @Transactional
    public Long save(Long brandId, @Valid CarRepresentation carRepresentation) {
        Brand brand = brandService.getById(brandId);
        Car car = Car.of(carRepresentation, brand);
        car = carRepository.save(car);
        return car.getId();
    }

    public void update(Long id, CarRepresentation carRepresentation) {
        Car car = getById(id);
        car = Car.of(car, carRepresentation);
        try {
            carRepository.save(car);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new VersionConflictException();
        }
    }

    public Car getById(Long id) {
        return carRepository.findOne(id).orElseThrow(CarNotFoundException::new);
    }

    @Validated
    public void test(@Valid @NotNull TestE t) {
        System.out.println("foi");
    }
}
