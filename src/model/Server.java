package model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private AtomicInteger currentTime;
    int queueCapacity;
    private Task currentTask;
    private final int id;
    boolean processing;

    public boolean isFull(){
        return tasks.size() == queueCapacity;
    }

    public Server(int maxTasksPerServer, int id){
        queueCapacity = maxTasksPerServer;
        tasks = new ArrayBlockingQueue<Task>(maxTasksPerServer);
        waitingPeriod = new AtomicInteger(0);
        currentTime = new AtomicInteger();
        this.id = id;
        processing = false;
    }

    public void addTask(Task newTask){
        try {
            tasks.put(newTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitingPeriod.getAndAdd(newTask.getServiceTime());
    }

    public void run(){
        while(true){
            try {
                Task checkTask = tasks.peek();

                if (checkTask != null){
                    currentTask = checkTask;
                    currentTime.set(currentTask.getServiceTime());
                    processing = true;

                    while (currentTime.get() != 0){
                    }

                    tasks.take();
                }

            } catch (InterruptedException e) {
                System.out.println("Server " + id + " was interrupted.");
            }
        }
    }

    public void decrementTime(){
        if (processing){
            currentTime.getAndDecrement();
            currentTask.decrementServiceTime();
        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public void printServer(){
        System.out.println("Server: " + id);
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
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
