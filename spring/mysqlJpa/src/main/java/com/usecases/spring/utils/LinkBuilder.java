package com.usecases.spring.utils;

import com.usecases.spring.brand.BrandController;
import com.usecases.spring.car.CarController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class LinkBuilder {

    private LinkBuilder(){}

    public static Link brandLink(Long id) {
        return idLink(BrandController.class, id);
    }

    public static Link carLink(Long id) {
        return linkTo(methodOn(CarController.class).getById(id)).withSelfRel();
    }

    private static <T> Link idLink(Class<?> clazz, T id) {
        return linkTo(clazz).slash(id).withSelfRel();
    }
}
