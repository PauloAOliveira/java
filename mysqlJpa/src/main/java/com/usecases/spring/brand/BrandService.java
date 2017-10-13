package com.usecases.spring.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void update(Long id, BrandRepresentation representation) {
        Brand brand = getById(id);
        brand.setFrom(representation);
        brandRepository.save(brand);
    }
}
