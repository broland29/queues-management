package model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    final private int serverNumber;

    public Server(int serverNumber, int maxTasksPerServer){
        waitingPeriod = new AtomicInteger(0);
        tasks = new ArrayBlockingQueue<Task>(maxTasksPerServer);
        this.serverNumber = serverNumber;
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
        waitingPeriod.getAndAdd(newTask.getServiceTime());
    }

    public void run(){
        //printServer();
        while(true){
            try {
                Task currentTask = tasks.take();
                Thread.sleep(currentTask.getServiceTime());
                waitingPeriod.getAndAdd(-currentTask.getServiceTime());
            } catch (InterruptedException e) {
                System.out.println("Server " + serverNumber + " was interrupted.");
            }
        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public void printServer(){
        System.out.println("Server: " + serverNumber);
    }

    public int getWaitingPeriod() {
        return waitingPeriod.intValue();
    }

    public int getQueueSize(){
        return tasks.size();
    }
}
