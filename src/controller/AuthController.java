/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.User;

/**
 *
 * @author saksh
 */
public class AuthController {
    
    private User demoUser;

    public AuthController() {
        demoUser = new User("admin", "admin123");
    }

    public boolean login(String email, String password) {
        if (email == null || password == null) {
            return false;
        }

        return demoUser.getEmail().equals(email)
                && demoUser.getPassword().equals(password);
    }
}
