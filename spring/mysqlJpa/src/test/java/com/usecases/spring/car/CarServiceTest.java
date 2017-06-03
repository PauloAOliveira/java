package com.usecases.spring.car;

import com.github.javafaker.Faker;
import com.usecases.spring.brand.Brand;
import com.usecases.spring.brand.BrandService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private BrandService brandService;

    @InjectMocks
    private CarService carService;

    @Captor
    private ArgumentCaptor<Car> carArgumentCaptor;

    private Faker faker;

    private Brand brand;
    private Long brandId;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        faker = new Faker();
        brandId = faker.number().randomNumber();

        brand = new Brand();
        brand.setId(brandId);

        when(brandService.getById(eq(brandId))).thenReturn(brand);
    }

    @Test
    public void save() throws Exception {
        Long carId = faker.number().randomNumber();
        String name = faker.name().firstName();
        Integer numberDoors = faker.number().numberBetween(2, 5);
        String color = faker.color().name();
        Integer manufactureYear = faker.number().numberBetween(1970, LocalDate.now().getYear());
        Boolean airbags = faker.bool().bool();
        BigDecimal engine = new BigDecimal(faker.commerce().price(0d, 10d).replace(",", "."));

        CarRepresentation carRepresentation = new CarRepresentation();
        carRepresentation.setName(name);
        carRepresentation.setNumberDoors(numberDoors);
        carRepresentation.setColor(color);
        carRepresentation.setManufactureYear(manufactureYear);
        carRepresentation.setAirbags(airbags);
        carRepresentation.setEngine(engine);

        Car car = new Car();
        car.setId(carId);
        when(carRepository.save(carArgumentCaptor.capture())).thenReturn(car);

        Long save = carService.save(brandId, carRepresentation);

        assertEquals(carId, save);

        Car captured = carArgumentCaptor.getValue();

        assertEquals(brand, captured.getBrand());
        assertEquals(name, captured.getName());
        assertEquals(numberDoors, captured.getNumberDoors());
        assertEquals(color, captured.getColor());
        assertEquals(manufactureYear, captured.getManufactureYear());
        assertEquals(airbags, captured.getAirbags());
        assertEquals(engine, captured.getEngine());
    }

    @Test(expected = CarNotFoundException.class)
    public void getByIdNotFound() throws Exception {
        when(carRepository.findOne(eq(100L))).thenReturn(Optional.empty());

        carService.getById(100L);
    }

    @Test
    public void getById() throws Exception {
        Car car = new Car();

        when(carRepository.findOne(eq(100L))).thenReturn(Optional.of(car));

        Car resp = carService.getById(100L);

        assertEquals(car, resp);
    }
}