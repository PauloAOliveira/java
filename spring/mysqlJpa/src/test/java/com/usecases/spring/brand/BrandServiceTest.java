package com.usecases.spring.brand;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test(expected = BrandNotFoundException.class)
    public void updateNonExistingBrand() {
        Long id = faker.number().randomNumber();
        when(brandRepository.findOne(anyLong())).thenReturn(Optional.empty());

        brandService.update(id, new BrandRepresentation());
    }

    @Test
    public void updateBrand() {
        Long id = faker.number().randomNumber();
        String name = faker.lorem().characters(2, 15);
        String description = faker.lorem().characters(10, 200);

        BrandRepresentation representation = new BrandRepresentation();
        representation.setDescription(description);
        representation.setName(name);

        Brand brand = new Brand();
        brand.setName(faker.lorem().characters(2, 15));
        brand.setDescription(faker.lorem().characters(10, 200));
        brand.setId(id);

        when(brandRepository.findOne(eq(id))).thenReturn(Optional.of(brand));

        brandService.update(id, representation);

        verify(brandRepository, times(1)).save(eq(brand));

        assertEquals(id, brand.getId());
        assertEquals(name, brand.getName());
        assertEquals(description, brand.getDescription());
    }
}