package com.usecases.spring.brand;

import com.usecases.spring.car.Car;
import com.usecases.spring.domain.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "brand")
public class Brand extends AbstractEntity {

    @Column(nullable = false, length = 20)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "brand")
    private List<Car> cars;

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

    public static Brand of(BrandRepresentation representation) {
        Brand brand = new Brand();
        brand.setName(representation.getName());
        brand.setDescription(representation.getDescription());
        return brand;
    }

    public void setFrom(BrandRepresentation from) {
        setName(from.getName());
        setDescription(from.getDescription());
    }
}
