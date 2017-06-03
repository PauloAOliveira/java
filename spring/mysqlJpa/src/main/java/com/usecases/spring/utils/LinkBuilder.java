package com.usecases.spring.utils;

import com.usecases.spring.brand.BrandController;
import com.usecases.spring.car.CarController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class LinkBuilder {

    private LinkBuilder(){}

    public static Link brandLink(Long id) {
        return idLink(BrandController.class, id);
    }

    public static Link carLink(Long id) {
        return idLink(CarController.class, id);
    }

    private static <T> Link idLink(Class<?> clazz, T id) {
        return ControllerLinkBuilder.linkTo(clazz).slash(id).withSelfRel();
    }
}
