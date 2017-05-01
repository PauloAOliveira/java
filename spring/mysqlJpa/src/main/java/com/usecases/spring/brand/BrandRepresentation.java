package com.usecases.spring.brand;

import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BrandRepresentation extends ResourceSupport{

    @NotNull
    @Size(min = 2, max = 20)
    private String name;

    @Size(max = 300)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static BrandRepresentation of(Brand brand) {
        BrandRepresentation rep = new BrandRepresentation();
        rep.setName(brand.getName());
        rep.setDescription(brand.getDescription());
        return rep;
    }
}
