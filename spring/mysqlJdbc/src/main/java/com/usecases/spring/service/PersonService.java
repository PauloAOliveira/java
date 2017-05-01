package com.usecases.spring.service;

import com.usecases.spring.domain.PersonRepresentation;
import com.usecases.spring.domain.exception.InvalidDocumentException;
import com.usecases.spring.domain.exception.PersonNotFoundException;
import com.usecases.spring.exception.BadRequestException;
import com.usecases.spring.exception.NotFoundException;
import com.usecases.spring.validator.DocumentValidator;
import com.usecases.spring.domain.Person;
import com.usecases.spring.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public PersonRepresentation getById(Long id) {
        Person p = personRepository.getById(id)
                .orElseThrow(PersonNotFoundException::new);
        return PersonRepresentation.of(p);
    }

    public Long save(Person person){
        if(!DocumentValidator.isValid(person.getDocumentType().toString(), person.getDocumentNumber())){
            throw new InvalidDocumentException();
        }
        person.setDocumentNumber(person.getDocumentNumber().replaceAll("\\D+", ""));
        return personRepository.create(person);
    }

    public void update(Long id, Person person) {
        validateIfExist(id);
        personRepository.update(id, person);
    }

    public void delete(Long id) {
        validateIfExist(id);
        personRepository.delete(id);
    }

    private void validateIfExist(Long id) {
        if(!personRepository.exist(id)) {
            throw new PersonNotFoundException();
        }
    }
}
