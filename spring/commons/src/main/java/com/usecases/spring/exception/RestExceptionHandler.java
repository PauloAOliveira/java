package com.usecases.spring.exception;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequestMapping(produces = "application/vnd.error")
@ControllerAdvice(annotations = RestController.class)
@ResponseBody
public class RestExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Errors> handle(Exception e) {
        Errors errors = new Errors(Collections.singletonList(new Error(e.getMessage())));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Errors> handle(MethodArgumentNotValidException e) {
        List<FieldError> requestErrors = e.getBindingResult().getFieldErrors();

        List<Error> errors = requestErrors.stream().map(
                er -> new Error(String.format("%s:%s", er.getField(), er.getDefaultMessage())))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new Errors(errors));
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<Errors> handle(BaseException e) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus status = responseStatus.value();
        return ResponseEntity.status(status).body(new Errors(Collections.singletonList(new Error(e.getMessage()))));
    }
}
