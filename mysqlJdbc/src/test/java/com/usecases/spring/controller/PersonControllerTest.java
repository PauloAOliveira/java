package com.usecases.spring.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.javafaker.Faker;
import com.usecases.spring.domain.Person;
import com.usecases.spring.exception.RestExceptionHandler;
import com.usecases.spring.service.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private MockMvc mockMvc;

    private Faker faker;

    @Before
    public void setup() {
        faker = new Faker();
        MockitoAnnotations.initMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setLocale(Locale.US);
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(personController)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();

    }

    @Test
    public void createPersonAllParamsNull() throws Exception{
        mockMvc.perform(
                post("/people").contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(6)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "firstName:may not be null",
                        "birthDate:may not be null",
                        "lastName:may not be null",
                        "email:may not be null",
                        "documentType:may not be null",
                        "documentNumber:may not be null"
                )));
    }

    @Test
    public void createPersonMinValues() throws Exception {
        String json = "" +
                "{" +
                    "\"documentType\":\"ASD\"," +
                    "\"documentNumber\":\"111\"," +
                    "\"firstName\":\"qwe\"," +
                    "\"lastName\":\"zxc\"," +
                    "\"email\":\"fgh\"," +
                    "\"birthDate\": \"" + LocalDate.now().toString() +"\""+
                "}";

        mockMvc.perform(
                post("/people").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(6)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "documentType:may not be null",
                        "documentNumber:size must be between 11 and 18",
                        "firstName:size must be between 4 and 30",
                        "lastName:size must be between 4 and 45",
                        "email:not a well-formed email address",
                        "birthDate:must be in the past"
                )));
    }

    @Test
    public void createPersonMaxValues() throws Exception {
        String documentNumber = faker.lorem().fixedString(19);
        String firstName = faker.lorem().fixedString(31);
        String lastName = faker.lorem().fixedString(46);
        String email = faker.lorem().characters(65)+"@email.com";
        String json = String.format(
                "{" +
                        "\"documentType\":\"CPF\"," +
                        "\"documentNumber\":\"%s\"," +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", documentNumber, firstName, lastName, email, LocalDate.now().plusDays(1).toString());

        mockMvc.perform(
                post("/people").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "documentNumber:size must be between 11 and 18",
                        "firstName:size must be between 4 and 30",
                        "lastName:size must be between 4 and 45",
                        "email:not a well-formed email address",
                        "birthDate:must be in the past"
                )));
    }

    @Test
    public void createPerson() throws Exception {
        String documentNumber = "48281875100";
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String json = String.format(
                "{" +
                        "\"documentType\":\"CPF\"," +
                        "\"documentNumber\":\"%s\"," +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", documentNumber, firstName, lastName, email, LocalDate.now().minusDays(1).toString());

        Long id = faker.number().randomNumber();
        when(personService.save(any(Person.class))).thenReturn(id);

        mockMvc.perform(
                post("/people").contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.href", is("http://localhost/people/"+id)))
                .andExpect(jsonPath("$.rel", is("self")));
    }

    @Test
    public void updatePersonAllParamsNull() throws Exception{
        mockMvc.perform(
                put("/people/123").contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(
                        "firstName:may not be null",
                        "birthDate:may not be null",
                        "lastName:may not be null",
                        "email:may not be null"
                )));
    }

    @Test
    public void updatePerson() throws Exception {
        String firstName = faker.lorem().characters(4, 30);
        String lastName = faker.lorem().characters(4, 45);
        String email = faker.internet().emailAddress();
        String json = String.format(
                "{" +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", firstName, lastName, email, LocalDate.now().minusDays(1).toString());

        Long id = faker.number().randomNumber();

        mockMvc.perform(
                put("/people/{personId}", id).contentType(MediaType.APPLICATION_JSON).content(json)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.href", is("http://localhost/people/"+id)))
                .andExpect(jsonPath("$.rel", is("self")));

        verify(personService, times(1)).update(eq(id), any(Person.class));
    }
}
