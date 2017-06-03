package com.usecases.spring.brand;

import com.usecases.spring.utils.LinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.usecases.spring.utils.LinkBuilder.*;

@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping
    public ResponseEntity<Link> save(@RequestBody @Validated BrandRepresentation brand) {
        Long id = brandService.save(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(brandLink(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandRepresentation> getById(@PathVariable Long id) {
        BrandRepresentation rep = BrandRepresentation.of(brandService.getById(id));
        rep.add(brandLink(id));
        return ResponseEntity.ok(rep);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Link> update(@PathVariable Long id, @RequestBody @Validated BrandRepresentation brand) {
        brandService.update(id, brand);
        return ResponseEntity.status(HttpStatus.OK).body(brandLink(id));
    }
}
