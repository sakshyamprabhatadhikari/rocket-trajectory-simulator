/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author saksh
 */

public class RocketRepository {

    private ArrayList<Rocket> rocketList;
    private Queue<Rocket> recentRockets;

    public RocketRepository() {
        rocketList = new ArrayList<>();
        recentRockets = new LinkedList<>();

        // Load minimum 5 rockets (GUIDELINE REQUIREMENT)
        addRocket(new Rocket("Falcon 9", 2015, "Orbital"));
        addRocket(new Rocket("Artemis I", 2022, "Orbital"));
        addRocket(new Rocket("Sounding-X", 2019, "Suborbital"));
        addRocket(new Rocket("Gaganyaan", 2023, "Orbital"));
        addRocket(new Rocket("Lunar Lander", 2021, "Experimental"));
    }

    public void addRocket(Rocket rocket) {
        rocketList.add(rocket);

        recentRockets.add(rocket);
        if (recentRockets.size() > 5) {
            recentRockets.poll();
        }
    }

    public ArrayList<Rocket> getAllRockets() {
        return rocketList;
    }

    public Queue<Rocket> getRecentRockets() {
        return recentRockets;
    }

    public int getTotalRockets() {
        return rocketList.size();
    }
}