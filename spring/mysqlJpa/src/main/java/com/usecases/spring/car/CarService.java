package com.usecases.spring.car;

import com.usecases.spring.brand.Brand;
import com.usecases.spring.brand.BrandService;
import com.usecases.spring.exception.ConflictException;
import com.usecases.spring.exception.VersionConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BrandService brandService;

    @Transactional
    public Long save(Long brandId, CarRepresentation carRepresentation) {
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
}
