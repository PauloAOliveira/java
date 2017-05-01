package com.usecases.spring.brand;

import com.usecases.spring.exception.NotFoundException;

public class BrandNotFoundException extends NotFoundException{
    public BrandNotFoundException() {
        super("Brand does not exist");
    }
}
