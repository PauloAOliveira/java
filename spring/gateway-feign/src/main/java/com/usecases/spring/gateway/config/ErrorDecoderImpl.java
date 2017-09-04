package com.usecases.spring.gateway.config;

import feign.Response;
import feign.codec.ErrorDecoder;

public class ErrorDecoderImpl implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        System.out.println(s);
        System.out.println(response.body());
        System.out.println(response.reason());
        return null;
    }
}
