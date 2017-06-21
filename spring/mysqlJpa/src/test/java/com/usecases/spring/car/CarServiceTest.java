package com.usecases.spring.car;

import com.usecases.spring.Commons;
import com.usecases.spring.MysqlJpaApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MysqlJpaApplication.class, Commons.class})
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {MysqlJpaApplication.class, Commons.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarServiceTest {

    /*@Autowired
    private CarService carService;

    @Test
    public void test1() throws Exception {

        try {
            carService.test(null);
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(cv -> {
                System.out.print(cv.getMessage());
                System.out.println(cv.);
            });
        }
    }*/

}