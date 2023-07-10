package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.entities.User;
import com.walyCommerce.walycommerce.services.exceptions.ForbiddenException;
import com.walyCommerce.walycommerce.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService service;

    @Mock
    private UserService userService;

    private User admin, selfClient, otherClient;

    @BeforeEach
    void setUp() throws Exception {
        admin = UserFactory.createAdminUser();
        selfClient = UserFactory.customClientUser(1L, "bob");
        otherClient = UserFactory.customClientUser(2L, "ana");

    }

    @Test
    void ValidateSelfOrAdminShouldDoNothingWhenAdminLogged() {
        Mockito.when(userService.authenticade()).thenReturn(admin);

        Long userId = admin.getId();

        Assertions.assertDoesNotThrow(()->{
            service.validateSelfOrAdmin(userId);
        });
    }

    @Test
    void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {
        Mockito.when(userService.authenticade()).thenReturn(selfClient);
        Long userId = selfClient.getId();
        
        Assertions.assertDoesNotThrow(()->{
            service.validateSelfOrAdmin(userId);
        });
    }

    @Test
    void validateSelfOrAdminShouldThrowsForbiddenExceptionWhenClientOtherLogged() {
        Mockito.when(userService.authenticade()).thenReturn(selfClient);
        Long userId = otherClient.getId();

        Assertions.assertThrows(ForbiddenException.class,()->{
           service.validateSelfOrAdmin(userId);
        });
    }
}
