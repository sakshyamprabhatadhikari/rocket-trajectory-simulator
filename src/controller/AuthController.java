/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author saksh
 */
public class AuthController {

    public boolean login(String email, String password) {

        if (email == null || password == null) {
            return false;
        }

        email = email.trim();
        password = password.trim();

        return email.equals("admin@nepalaerospace.com")
                && password.equals("admin123");
    }
}
