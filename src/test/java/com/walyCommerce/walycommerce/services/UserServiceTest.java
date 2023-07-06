package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.entities.User;
import com.walyCommerce.walycommerce.projections.UserDetailsProjection;
import com.walyCommerce.walycommerce.repositories.UserRepository;
import com.walyCommerce.walycommerce.services.tests.UserDetailsFactory;
import com.walyCommerce.walycommerce.services.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    private String existingUsername, nonExistingUsername;
    private User user;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp() throws Exception{
        existingUsername = "maria@gmail.com";
        nonExistingUsername = "user@gmail.com";

        user = UserFactory.customClientUser(1L, existingUsername);

        userDetails = UserDetailsFactory.createAdminListUserDetails(existingUsername);

        Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());


    }

    @Test
    public void loadByUsernameShouldReturnUserDetailsWhenUserExists(){
        UserDetails resutl = service.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(resutl);
        Assertions.assertEquals(resutl.getUsername(), existingUsername);
    }

    @Test
    public void loadByUsernameShouldThrowNotFoundExceptionWhennonExistsUsername() throws Exception{
        Assertions.assertThrows(UsernameNotFoundException.class, ()->{
            service.loadUserByUsername(nonExistingUsername);
        });
    }
}
