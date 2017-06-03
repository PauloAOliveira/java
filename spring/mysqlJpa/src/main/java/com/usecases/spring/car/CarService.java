package com.usecases.spring.car;

import com.usecases.spring.brand.Brand;
import com.usecases.spring.brand.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Car getById(Long id) {
        return carRepository.findOne(id).orElseThrow(CarNotFoundException::new);
    }
}
