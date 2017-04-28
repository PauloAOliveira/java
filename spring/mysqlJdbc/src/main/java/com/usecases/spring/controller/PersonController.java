package com.usecases.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usecases.spring.domain.Person;
import com.usecases.spring.domain.groups.Create;
import com.usecases.spring.domain.groups.Update;
import com.usecases.spring.response.Link;
import com.usecases.spring.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequestMapping(path = "/people")
@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping()
    public ResponseEntity<Link> createPerson(@RequestBody @Validated(Create.class) Person person){
        Long id = personService.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Link(String.format("/people/%d", id)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Link> updatePerson(@PathVariable Long id, @RequestBody @Validated(Update.class) Person person) {
        personService.update(id, person);
        return ResponseEntity.ok(new Link(String.format("/people/%d", id)));
    }

    @GetMapping(value = "/{id}")
    public Person getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
