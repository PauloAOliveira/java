package com.usecases.spring.brand;

import com.github.javafaker.Faker;
import com.usecases.spring.UtilsTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    private MockMvc mockMvc;

    private Faker faker;

    @Before
    public void setup() {
        faker = new Faker();
        MockitoAnnotations.initMocks(this);
        mockMvc = UtilsTest.getControllerMockMvc(brandController);
    }

    @Test
    public void createBrandAllParamsNull() throws Exception{
        mockMvc.perform(
                post("/brands").contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:may not be null"
                )));
    }

    @Test
    public void createBrandMinValues() throws Exception {
        String json = "" +
                "{" +
                "\"name\":\"a\"" +
                "}";

        mockMvc.perform(
                post("/brands").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:size must be between 2 and 20"
                )));
    }

    @Test
    public void createBrandMaxValues() throws Exception {
        String json = String.format("{" +
                "\"name\":\"%s\"," +
                "\"description\":\"%s\"" +
                "}", faker.lorem().characters(21), faker.lorem().characters(301));

        mockMvc.perform(
                post("/brands").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:size must be between 2 and 20",
                        "description:size must be between 0 and 300"
                )));
    }

    @Test
    public void createBrand() throws Exception {
        String name = faker.lorem().characters(2, 15);
        String description = faker.lorem().characters(1, 295);
        String json = String.format(
                "{" +
                "\"name\":\"%s\"," +
                "\"description\":\"%s\"" +
                "}", name, description);

        Long id = faker.number().randomNumber();
        when(brandService.save(any(BrandRepresentation.class))).thenReturn(id);

        mockMvc.perform(
                post("/brands").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.href", is("http://localhost/brands/"+id)))
                .andExpect(jsonPath("$.rel", is("self")));
    }

    @Test
    public void UpdateBrandAllParamsNull() throws Exception{
        mockMvc.perform(
                put("/brands/1").contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:may not be null"
                )));
    }

    @Test
    public void updateBrandMinValues() throws Exception {
        String json = "" +
                "{" +
                "\"name\":\"a\"" +
                "}";

        mockMvc.perform(
                put("/brands/1").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:size must be between 2 and 20"
                )));
    }

    @Test
    public void updateBrandMaxValues() throws Exception {
        String json = String.format("{" +
                "\"name\":\"%s\"," +
                "\"description\":\"%s\"" +
                "}", faker.lorem().characters(21), faker.lorem().characters(301));

        mockMvc.perform(
                put("/brands/1").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "name:size must be between 2 and 20",
                        "description:size must be between 0 and 300"
                )));
    }

    @Test
    public void updateBrand() throws Exception {
        String name = faker.lorem().characters(2, 15);
        String description = faker.lorem().characters(1, 295);
        String json = String.format(
                "{" +
                        "\"name\":\"%s\"," +
                        "\"description\":\"%s\"" +
                        "}", name, description);

        mockMvc.perform(
                put("/brands/1").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.href", is("http://localhost/brands/1")))
                .andExpect(jsonPath("$.rel", is("self")));

        verify(brandService, times(1)).update(any(Long.class), any(BrandRepresentation.class));
    }
}