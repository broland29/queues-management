package model;

import business_logic.SimulationManager;

public class Task {
    private int arrivalTime;
    private int serviceTime;
    private int id;

    public Task(int arrivalTime, int serviceTime, int id) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.id = id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getId() {
        return id;
    }

    public void printTask(){
        System.out.println("Arrival time: " + arrivalTime + "\nService Time: " + serviceTime + "\n");
    }

    public void decrementServiceTime(){
        serviceTime--;
    }
}
