package com.usecases.spring.car;

import com.github.javafaker.Faker;
import com.usecases.spring.UtilsTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private MockMvc mockMvc;

    private Faker faker;

    @Before
    public void setup() {
        faker = new Faker();
        MockitoAnnotations.initMocks(this);
        mockMvc = UtilsTest.getControllerMockMvc(carController);
    }

    @Test
    public void createCarAllParamsNull() throws Exception{
        mockMvc.perform(
                post("/brands/{brandId}/cars", 1L).contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(6)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:may not be null",
                        "numberDoors:may not be null",
                        "color:may not be null",
                        "manufactureYear:may not be null",
                        "airbags:may not be null",
                        "engine:may not be null"
                )));
    }

    @Test
    public void createCarMinValues() throws Exception {
        String json = "" +
                "{" +
                "\"airbags\":false," +
                "\"name\":\"a\"," +
                "\"numberDoors\":1," +
                "\"color\":\"c\"," +
                "\"manufactureYear\":1969," +
                "\"engine\":0.9"+
                "}";

        mockMvc.perform(
                post("/brands/{brandId}/cars", 1L).contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:size must be between 5 and 15",
                        "color:size must be between 3 and 15",
                        "engine:must be greater than or equal to 1.0",
                        "manufactureYear:must be greater than or equal to 1970",
                        "numberDoors:must be greater than or equal to 2"
                )));
    }

    @Test
    public void createCarMaxValues() throws Exception {
        String json = String.format("{" +
                "\"airbags\":false," +
                "\"name\":\"%s\"," +
                "\"numberDoors\":6," +
                "\"color\":\"%s\"," +
                "\"manufactureYear\":9999," +
                "\"engine\":5.1"+
                "}", faker.lorem().characters(16),
                faker.lorem().characters(16));

        mockMvc.perform(
                post("/brands/{brandId}/cars", 1L).contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:size must be between 5 and 15",
                        "color:size must be between 3 and 15",
                        "engine:must be less than or equal to 5.0",
                        "numberDoors:must be less than or equal to 5"
                )));
    }

    @Test
    public void createCar() throws Exception {
        String name = faker.lorem().characters(5, 15);
        Long carId = faker.number().randomNumber();
        Integer numberDoors = faker.number().numberBetween(2, 5);
        String color = faker.color().name();
        Integer manufactureYear = faker.number().numberBetween(1970, LocalDate.now().getYear());
        Boolean airbags = faker.bool().bool();
        String engine = faker.commerce().price(1d, 5d).replace(",", ".");

        String json = String.format("{" +
                        "\"airbags\":%s," +
                        "\"name\":\"%s\"," +
                        "\"numberDoors\":%d," +
                        "\"color\":\"%s\"," +
                        "\"manufactureYear\":%d," +
                        "\"engine\":\"%s\""+
                        "}", airbags, name, numberDoors, color, manufactureYear, engine);

        when(carService.save(eq(1L), any(CarRepresentation.class))).thenReturn(carId);

        mockMvc.perform(
                post("/brands/{brandId}/cars", 1L).contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.href", is("http://localhost/cars/"+carId)))
                .andExpect(jsonPath("$.rel", is("self")));
    }
}