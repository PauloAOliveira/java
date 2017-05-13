package com.usecases.spring.brand;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BrandRepository extends Repository<Brand, Long> {

    Optional<Brand> findOne(Long id);

    <S extends Brand> S save(S customer);

    boolean exists(Long id);
}
