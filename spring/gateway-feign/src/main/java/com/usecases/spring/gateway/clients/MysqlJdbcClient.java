package com.usecases.spring.gateway.clients;

import com.usecases.spring.gateway.domain.Person;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

@FeignClient("mysqlJdbc")
public interface MysqlJdbcClient {

    @PostMapping(value = "/people")
    Link createPerson(@RequestBody Person person);

    @PutMapping(value = "/people/{id}")
    Link updatePerson(@PathVariable Long id, @RequestBody Person person);

    @GetMapping(value = "/people/{id}")
    Person getById(@PathVariable Long id);

    @DeleteMapping(value = "/people/{id}")
    void deletePerson(@PathVariable Long id);
}
