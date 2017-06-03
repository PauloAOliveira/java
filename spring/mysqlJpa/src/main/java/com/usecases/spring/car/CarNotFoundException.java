package com.usecases.spring.car;

import com.usecases.spring.exception.NotFoundException;

public class CarNotFoundException extends NotFoundException{
    public CarNotFoundException() {
        super("Car does not exist");
    }
}
