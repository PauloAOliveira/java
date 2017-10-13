package com.usecases.spring.gateway.controller;

import com.usecases.spring.gateway.clients.MysqlJdbcClient;
import com.usecases.spring.gateway.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(value = "/gateway")
public class PersonController {

    @Autowired
    private MysqlJdbcClient mysqlJdbcClient;

    @PostMapping(value = "/people")
    public Link createPerson(@RequestBody Person person) {
        return mysqlJdbcClient.createPerson(person);
    }

    @PutMapping(value = "/people/{id}")
    public Link updatePerson(@PathVariable Long id, @RequestBody Person person) {
        return mysqlJdbcClient.updatePerson(id, person);
    }

    @GetMapping(value = "/people/{id}")
    public Person getById(@PathVariable Long id) {
        return mysqlJdbcClient.getById(id);
    }

    @DeleteMapping(value = "/people/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        mysqlJdbcClient.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}
