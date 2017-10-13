package com.usecases.spring.car;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CarRepository extends Repository<Car, Long> {
    Optional<Car> findOne(Long id);

    <S extends Car> S save(S car);
}
