/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author saksh
 */
public class Rocket {

    private String name;
    private int launchYear;
    private String rocketType;

    public Rocket(String name, int launchYear, String rocketType) {
        this.name = name;
        this.launchYear = launchYear;
        this.rocketType = rocketType;
    }

    public String getName() {
        return name;
    }

    public int getLaunchYear() {
        return launchYear;
    }

    public String getRocketType() {
        return rocketType;
    }
}
