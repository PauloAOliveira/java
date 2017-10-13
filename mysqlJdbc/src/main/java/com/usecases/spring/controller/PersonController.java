package com.usecases.spring.controller;

import com.usecases.spring.domain.Person;
import com.usecases.spring.domain.PersonRepresentation;
import com.usecases.spring.service.PersonService;
import com.usecases.spring.validator.groups.Create;
import com.usecases.spring.validator.groups.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequestMapping(path = "/people")
@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping()
    public ResponseEntity<Link> createPerson(@RequestBody @Validated(Create.class) Person person){
        Long id = personService.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(getLinkById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Link> updatePerson(@PathVariable Long id, @RequestBody @Validated(Update.class) Person person) {
        personService.update(id, person);
        return ResponseEntity.ok(getLinkById(id));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonRepresentation> getById(@PathVariable Long id) {
        PersonRepresentation person = personService.getById(id);
        person.add(ControllerLinkBuilder.linkTo(PersonController.class).slash(id).withSelfRel());
        return ResponseEntity.ok(person);
    }

    private Link getLinkById(Long id) {
        return ControllerLinkBuilder.linkTo(PersonController.class).slash(id).withSelfRel();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
