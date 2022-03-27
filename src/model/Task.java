package model;

public class Task {
    private int arrivalTime;
    private int serviceTime;

    public Task(int arrivalTime, int serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void printTask(){
        System.out.println("Arrival time: " + arrivalTime + "\nService Time: " + serviceTime + "\n");
    }
}
