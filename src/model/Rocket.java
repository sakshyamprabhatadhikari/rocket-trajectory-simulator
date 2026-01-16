package model;

public class Rocket {
    private String rocketID;
    private String rocketName;
    private String country;
    private double mass;
    private double thrust;
    private String status;     // Active, Inactive, Testing
    private int launchYear;

    public Rocket(String rocketID, String rocketName, String country, double mass, double thrust, String status, int launchYear) {
        this.rocketID = rocketID;
        this.rocketName = rocketName;
        this.country = country;
        this.mass = mass;
        this.thrust = thrust;
        this.status = status;
        this.launchYear = launchYear;
    }

    // Getters
    public String getRocketID() {
        return rocketID;
    }

    public String getRocketName() {
        return rocketName;
    }
    
    public String getCountry() {
    return country;
    }

    public double getMass() {
        return mass;
    }

    public double getThrust() {
        return thrust;
    }

    public String getStatus() {
        return status;
    }

    public int getLaunchYear() {
        return launchYear;
    }

    // Setters
    public void setRocketID(String rocketID) {
        this.rocketID = rocketID;
    }

    public void setRocketName(String rocketName) {
        this.rocketName = rocketName;
    }
    
    public void setCountry(String country) {
    this.country = country;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setThrust(double thrust) {
        this.thrust = thrust;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLaunchYear(int launchYear) {
        this.launchYear = launchYear;
    }

    @Override
    public String toString() {
        return rocketName + " (" + launchYear + ") - " + status;
    }
}
