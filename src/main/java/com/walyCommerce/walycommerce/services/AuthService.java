package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.entities.User;
import com.walyCommerce.walycommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;
    public void validateSelfOrAdmin(Long userId){
        User me = userService.authenticade();
        if(me.hasRole("ROLE_ADMIN")){
            return;
        }
        if(!me.getId().equals(userId)){
            throw new ForbiddenException("Acces denied. Should be self or admin");
        }
    }
}
