package com.usecases.spring.car;

import com.usecases.spring.utils.LinkBuilder;
import com.usecases.spring.validator.groups.Create;
import com.usecases.spring.validator.groups.Update;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class CarRepresentation extends ResourceSupport {

    private Link brand;

    @NotNull(groups = {Create.class, Update.class})
    @Size(min = 5, max = 15, groups = {Create.class, Update.class})
    private String name;

    @NotNull(groups = {Create.class, Update.class})
    @Max(value = 5, groups = {Create.class, Update.class})
    @Min(value = 2, groups = {Create.class, Update.class})
    private Integer numberDoors;

    @NotNull(groups = {Create.class, Update.class})
    @Size(min = 3, max = 15, groups = {Create.class, Update.class})
    private String color;

    @NotNull(groups = {Create.class, Update.class})
    @Min(value = 1970, groups = {Create.class, Update.class})
    private Integer manufactureYear;

    @NotNull(groups = {Create.class, Update.class})
    private Boolean airbags;

    @NotNull(groups = {Create.class, Update.class})
    @DecimalMin(value = "1.0", groups = {Create.class, Update.class})
    @DecimalMax(value = "5.0", groups = {Create.class, Update.class})
    private BigDecimal engine;

    @NotNull(groups = {Update.class})
    private Long version;

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
        representation.setVersion(car.getVersion());

        return representation;
    }
}
