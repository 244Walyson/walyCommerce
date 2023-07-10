package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.UserDTO;
import com.walyCommerce.walycommerce.entities.User;
import com.walyCommerce.walycommerce.projections.UserDetailsProjection;
import com.walyCommerce.walycommerce.repositories.UserRepository;
import com.walyCommerce.walycommerce.tests.UserDetailsFactory;
import com.walyCommerce.walycommerce.tests.UserFactory;
import com.walyCommerce.walycommerce.util.CustonUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustonUserUtil userUtil;
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
        Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());

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

    @Test
    public void authenticatedShouldReturnUserWhenUserExists(){
        Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
        User result = service.authenticade();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingUsername);
    }
    @Test
    public void authenticatedShouldThrowUserNotFoundExceptionWhenUserDoesNotExists() throws Exception{
        Mockito.when(userUtil.getLoggedUsername()).thenThrow(ClassCastException.class);

        Assertions.assertThrows(UsernameNotFoundException.class, ()->{
            service.authenticade();
        });
    }

    @Test
    public void getMeShouldReturnUserWhenUserExists(){
        UserService spyUserService = Mockito.spy(service);
        Mockito.doReturn(user).when(spyUserService).authenticade();

        UserDTO result = spyUserService.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getEmail(), existingUsername);


    }
    @Test
    public void getMeShouldThrowUserNotFoundExceptionWhenUserNotAuthenticated() throws Exception{
        UserService spyUserService = Mockito.spy(service);
        Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticade();

        Assertions.assertThrows(UsernameNotFoundException.class, spyUserService::getMe);
    }

}
