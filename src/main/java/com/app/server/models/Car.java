package com.app.server.models;

public class Car {

    public String getId() {
        return id;
    }
    public String getMake() {
        return make;
    }
    public String getModel() {
        return model;
    }
    public Number getYear() {
        return year;
    }
    public String getSize() {
        return size;
    }
    public String getColor() {
        return color;
    }
    public Number getOdometer() {
        return odometer;
    }
    public String getDriverId() {
        return driverId;
    }


    String id = null;
    String make, model, size, color, driverId;
    Number year, odometer ;

    public Car(String make, String model, Number year, String size,
               String color, Number odometer, String driverId) {
        this.make = make;
        this.model = model;
        this.size = size;
        this.year = year;
        this.odometer = odometer;
        this.color = color;
        this.driverId = driverId;
    }
    public void setId(String id) {
        this.id = id;
    }
}
