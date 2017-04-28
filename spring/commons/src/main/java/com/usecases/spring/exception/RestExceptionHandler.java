package com.usecases.spring.exception;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequestMapping(produces = "application/vnd.error")
@ControllerAdvice(annotations = RestController.class)
@ResponseBody
public class RestExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Errors> handle(Exception e) {
        Errors errors = new Errors(Arrays.asList(new Error(e.getMessage())));
        return new ResponseEntity(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Errors> handle(MethodArgumentNotValidException e) {
        List<FieldError> requestErrors = e.getBindingResult().getFieldErrors();

        List<Error> errors = requestErrors.stream().map(
                er -> new Error(String.format("%s:%s", er.getField(), er.getDefaultMessage())))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new Errors(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<Errors> handle(BaseException e) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus status = responseStatus.value();

        return new ResponseEntity<>(new Errors(Arrays.asList(new Error(e.getMessage()))), status);
    }
}
