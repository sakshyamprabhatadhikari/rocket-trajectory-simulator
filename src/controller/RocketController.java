/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Rocket;
import model.RocketRepository;
import java.util.ArrayList;

/**
 *
 * @author saksh
 */
public class RocketController {

    private RocketRepository repository;

    public RocketController(RocketRepository repository) {
        this.repository = repository;
    }

    public void addRocket(String name, int year, String type) {
        Rocket rocket = new Rocket(name, year, type);
        repository.addRocket(rocket);
    }

    public ArrayList<Rocket> getAllRockets() {
        return repository.getAllRockets();
    }

    public int getTotalRocketCount() {
        return repository.getTotalRockets();
    }
}
