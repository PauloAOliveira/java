package com.usecases.spring.brand;

import com.usecases.spring.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BrandITTest extends IntegrationTest {

    @Autowired
    private BrandRepository brandRepository;

    @Before
    public void setup() {
        setMockMvc();
    }

    @Test
    public void createBrand() throws Exception{
        String name = faker.lorem().characters(4, 20);
        String description = faker.lorem().characters(1, 300);
        String json = String.format(
                "{" +
                        "\"name\":\"%s\"," +
                        "\"description\":\"%s\"" +
                        "}", name, description);

        String responseString = mockMvc.perform(post("/brands")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("rel", is("self")))
                .andExpect(jsonPath("href", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> response = parseResponse(responseString);

        String href = response.get("href").toString();
        String[] split = href.split("/");
        Long id = Long.valueOf(split[split.length-1]);

        Brand after = brandRepository.findOne(id).get();
        assertEquals(name, after.getName());
        assertEquals(description, after.getDescription());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getCreatedDate().truncatedTo(ChronoUnit.HOURS));
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getModifiedDate().truncatedTo(ChronoUnit.HOURS));

        assertEquals(String.format("http://localhost/brands/%d", after.getId()), href);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getById() throws Exception {
        Long id = 1L;
        Brand before = brandRepository.findOne(id).get();

        mockMvc.perform(get("/brands/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(before.getName())))
                .andExpect(jsonPath("description", is(before.getDescription())))
                .andExpect(jsonPath("links", hasSize(1)))
                .andExpect(jsonPath("links[0].rel", is("self")))
                .andExpect(jsonPath("links[0].href", is("http://localhost/brands/1")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/brands/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0].error", is("Brand does not exist")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateNotFound() throws Exception {
        String body = "{\"name\":\"any name\"}";

        mockMvc.perform(put("/brands/{id}", 999L)
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0].error", is("Brand does not exist")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateNameAndDescription() throws Exception {
        Long id = 2L;
        Brand before = brandRepository.findOne(id).get();

        String newName = "updated name";
        String newDescription = "updated description";

        String body = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", newName, newDescription);

        mockMvc.perform(put("/brands/{id}", 2L)
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("rel", is("self")))
                .andExpect(jsonPath("href", notNullValue()));

        Brand after = brandRepository.findOne(id).get();

        assertNotEquals(before.getName(), after.getName());
        assertNotEquals(before.getDescription(), after.getDescription());
        assertEquals(after.getName(), newName);
        assertEquals(after.getDescription(), newDescription);
        assertEquals(LocalDateTime.of(2017,3,3,3,3,3), after.getCreatedDate());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getModifiedDate().truncatedTo(ChronoUnit.HOURS));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateDescriptionToNull() throws Exception {
        Long id = 3L;
        Brand before = brandRepository.findOne(id).get();

        String newName = "updated name";

        String body = String.format("{\"name\":\"%s\"}", newName);

        mockMvc.perform(put("/brands/{id}", 3L)
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("rel", is("self")))
                .andExpect(jsonPath("href", notNullValue()));

        Brand after = brandRepository.findOne(id).get();

        assertNotEquals(before.getName(), after.getName());
        assertNotNull(before.getDescription());

        assertEquals(after.getName(), newName);
        assertNull(after.getDescription());
    }
}
