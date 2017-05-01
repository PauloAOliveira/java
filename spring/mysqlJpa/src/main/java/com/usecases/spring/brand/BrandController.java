package com.usecases.spring.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping
    public ResponseEntity<Link> save(@RequestBody @Validated BrandRepresentation brand) {
        Long id = brandService.save(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(getLinkById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandRepresentation> getById(@PathVariable Long id) {
        BrandRepresentation rep = BrandRepresentation.of(brandService.getById(id));
        rep.add(getLinkById(id));
        return ResponseEntity.ok(rep);
    }

    private Link getLinkById(Long id) {
        return ControllerLinkBuilder.linkTo(BrandController.class).slash(id).withSelfRel();
    }
}
