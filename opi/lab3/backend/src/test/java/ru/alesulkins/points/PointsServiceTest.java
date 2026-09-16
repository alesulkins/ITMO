package ru.alesulkins.points;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.quality.Strictness;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import ru.alesulkins.auth.UserEntity;
import ru.alesulkins.points.dto.PointRequest;
import ru.alesulkins.points.dto.PointResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PointsServiceTest {

    @Mock
    private PointsRepository repository;

    @InjectMocks
    private PointsService service;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUsername("testuser");
        when(repository.save(any(PointEntity.class))).thenAnswer(invokation -> invokation.getArgument(0));
    }

    @Test
    void third_quadrant_pointInsideCircle_returnsHit() {
        // подготовка
        PointRequest request = new PointRequest(-1, -1, 2);
        
        // действие
        PointResponse response = service.create(testUser, request);
        
        // проверка
        assertTrue(response.hit(),
                "Точка (-1,-1) при r=2: должно быть попадание");
    }

    @Test
    void third_quadrant_pointOutsideCircle_returnsMiss() {
        PointRequest request = new PointRequest(-3, -3, 2);
        PointResponse response = service.create(testUser, request);
        assertFalse(response.hit(),
                "Точка (-3,-3) при r=2: должен быть промах");
    }

    @Test 
    void third_quadrant_pointExactlyOnCircle_returnsHit() {
        PointRequest request = new PointRequest(-2,0, 2);
        PointResponse response = service.create(testUser, request);
        assertTrue(response.hit(),
                "Точка (-2,0) при r=2: должно быть попадание");
    }

    @Test
    void zero_radius_alwaysReturnsMiss() {
        PointRequest request = new PointRequest(0, 0, 0);
        PointResponse response = service.create(testUser, request);
        assertFalse(response.hit(),
                "При r=0 область не существует");
    }

    @Test
    void negative_radius_alwaysReturnsMiss() {
        PointRequest request = new PointRequest(0, 0, -1);
        PointResponse response = service.create(testUser, request);
        assertFalse(response.hit(),
                "При r<0 область не существует");
    }

    @Test
    void fourth_quadrant_insideRectangle_returnsHit() {
        PointRequest request = new PointRequest(1, -1, 2);
        PointResponse response = service.create(testUser, request);
        assertTrue(response.hit(),
                "Точка (1,-1) при r=2: должно быть попадание");
    }

    @Test
    void first_quadrant_anyPoint_alwaysReturnsMiss() {
        PointRequest request = new PointRequest(1, 1, 10);
        PointResponse response = service.create(testUser, request);
        assertFalse(response.hit(),
                "всегда должен быть промах");
    }
}
