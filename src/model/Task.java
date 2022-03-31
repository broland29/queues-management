package model;

public class Task {
    private final int arrivalTime;
    private int serviceTime;
    private final int id;

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

    public void decrementServiceTime(){
        serviceTime--;
    }

}
