package com.usecases.spring.car;

import com.usecases.spring.utils.LinkBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class CarRepresentation extends ResourceSupport {

    private Link brand;

    @NotNull
    @Size(min = 5, max = 15)
    private String name;

    @NotNull
    @Max(value = 5)
    @Min(value = 2)
    private Integer numberDoors;

    @NotNull
    @Size(min = 3, max = 15)
    private String color;

    @NotNull
    @Min(value = 1970)
    private Integer manufactureYear;

    @NotNull
    private Boolean airbags;

    @NotNull
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal engine;

    public Link getBrand() {
        return brand;
    }

    public void setBrand(Link brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberDoors() {
        return numberDoors;
    }

    public void setNumberDoors(Integer numberDoors) {
        this.numberDoors = numberDoors;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public Boolean getAirbags() {
        return airbags;
    }

    public void setAirbags(Boolean airbags) {
        this.airbags = airbags;
    }

    public BigDecimal getEngine() {
        return engine;
    }

    public void setEngine(BigDecimal engine) {
        this.engine = engine;
    }

    public static CarRepresentation of(Car car) {
        CarRepresentation representation = new CarRepresentation();
        representation.setAirbags(car.getAirbags());
        representation.setBrand(LinkBuilder.brandLink(car.getBrand().getId()));
        representation.setColor(car.getColor());
        representation.setEngine(car.getEngine());
        representation.setManufactureYear(car.getManufactureYear());
        representation.setName(car.getName());
        representation.setNumberDoors(car.getNumberDoors());

        return representation;
    }
}
