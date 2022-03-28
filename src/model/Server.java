package model;

import business_logic.SimulationManager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private final int id;

    public Server(int maxTasksPerServer, int id){
        tasks = new ArrayBlockingQueue<Task>(maxTasksPerServer);
        waitingPeriod = new AtomicInteger(0);
        this.id = id;
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
        waitingPeriod.getAndAdd(newTask.getServiceTime());
    }

    public void run(){
        while(true){
            try {
                Task currentTask = tasks.peek();

                if (currentTask != null){
                    while (currentTask.getServiceTime() != 0){
                        Thread.sleep(SimulationManager.SECOND);
                        currentTask.decrementServiceTime();
                    }

                    tasks.remove(currentTask);
                }

            } catch (InterruptedException e) {
                System.out.println("Server " + id + " was interrupted.");
            }
        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public void printServer(){
        System.out.println("Server: " + id);
    }

    public int getWaitingPeriod() {
        return waitingPeriod.intValue();
    }

    public int getQueueSize(){
        return tasks.size();
    }

    public boolean isEmpty(){
        return tasks.isEmpty();
    }

    public int getId() {
        return id;
    }
}
