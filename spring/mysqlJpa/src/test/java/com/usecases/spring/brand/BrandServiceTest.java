package com.usecases.spring.brand;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Captor
    private ArgumentCaptor<Brand> brandArgumentCaptor;

    private Faker faker;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        faker = new Faker();
    }

    @Test
    public void save() {
        Long id = faker.number().randomNumber();
        String name = faker.lorem().characters(2, 15);
        String description = faker.lorem().characters(10, 200);

        BrandRepresentation rep = new BrandRepresentation();
        rep.setName(name);
        rep.setDescription(description);

        Brand brand = new Brand();
        brand.setId(id);

        when(brandRepository.save(brandArgumentCaptor.capture())).thenReturn(brand);

        Long resp = brandService.save(rep);
        assertEquals(brand.getId(), resp);

        //verifying if object's values were not modified
        Brand captured = brandArgumentCaptor.getValue();
        assertNull(captured.getId());
        assertEquals(description, captured.getDescription());
        assertEquals(name, captured.getName());
    }

    @Test
    public void getById() {
        Long id = faker.number().randomNumber();
        String name = faker.lorem().characters(2, 15);
        String description = faker.lorem().characters(10, 200);

        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setDescription(description);

        when(brandRepository.findOne(eq(id))).thenReturn(Optional.of(brand));

        Brand resp = brandService.getById(id);

        assertEquals(brand, resp);
        assertEquals(id, resp.getId());
        assertEquals(description, resp.getDescription());
        assertEquals(name, resp.getName());
    }

    @Test(expected = BrandNotFoundException.class)
    public void getByIdNotFound() {
        when(brandRepository.findOne(anyLong())).thenReturn(Optional.empty());

        brandService.getById(faker.number().randomNumber());
    }
}