package com.usecases.spring.car;

import com.usecases.spring.brand.Brand;
import com.usecases.spring.domain.AbstractEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.TreeSet;

@Entity
@Table(name = "car")
public class Car extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Integer numberDoors;

    @Column(nullable = false, length = 15)
    private String color;

    @Column(nullable = false)
    private Integer manufactureYear;

    @Column(nullable = false)
    private Boolean airbags;

    @Column(precision = 1, scale = 1)
    private BigDecimal engine;

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
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

    public static Car of(CarRepresentation carRepresentation, Brand brand) {
        Car car = new Car();
        car.setBrand(brand);
        car.setName(carRepresentation.getName());
        car.setNumberDoors(carRepresentation.getNumberDoors());
        car.setColor(carRepresentation.getColor());
        car.setManufactureYear(carRepresentation.getManufactureYear());
        car.setAirbags(carRepresentation.getAirbags());
        car.setEngine(carRepresentation.getEngine());
        return car;
    }
}
