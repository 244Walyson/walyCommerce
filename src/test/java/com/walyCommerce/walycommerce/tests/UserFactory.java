package com.walyCommerce.walycommerce.tests;

import com.walyCommerce.walycommerce.entities.Role;
import com.walyCommerce.walycommerce.entities.User;

import java.time.LocalDate;

public class UserFactory {

    public static User createClientUser(){
        User user = new User(1L, "maria", "maria@gmail.com", "31996574533", LocalDate.of(1999,05,12), "$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    public static User createAdminUser(){
        User user = new User(2L, "alex", "alex@gmail.com", "31996574533", LocalDate.of(1999,05,12), "$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }
    public static User customAdminUser(Long id, String username){
        User user = new User(id, username.split("@" )[0], username, "31996574533", LocalDate.of(1999,05,12), "$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }
    public static User customClientUser(Long id, String username){
        User user = new User(id, username.split("@" )[0], username, "31996574533", LocalDate.of(1999,05,12), "$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }
}
