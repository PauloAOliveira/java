package com.usecases.spring.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public Long save(BrandRepresentation representation) {
        Brand brand = Brand.of(representation);
        brand = brandRepository.save(brand);
        return brand.getId();
    }

    public Brand getById(Long id) {
        return brandRepository.findOne(id).orElseThrow(BrandNotFoundException::new);
    }
}
