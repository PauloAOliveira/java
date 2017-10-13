package com.usecases.spring.brand;

import org.springframework.hateoas.Link;
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

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public static BrandRepresentation of(Brand brand) {
        BrandRepresentation rep = new BrandRepresentation();
        rep.setName(brand.getName());
        rep.setDescription(brand.getDescription());
        return rep;
    }

    protected static BrandRepresentation of(String name, String description) {
        BrandRepresentation rep = new BrandRepresentation();
        rep.setName(name);
        rep.setDescription(description);
        return rep;
    }
}
