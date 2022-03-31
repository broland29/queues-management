package model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;
    private final AtomicInteger currentTime;
    private final int queueCapacity;
    private Task currentTask;
    private final int id;
    private boolean processing;

    public boolean isFull(){
        return tasks.size() == queueCapacity;
    }

    public Server(int maxTasksPerServer, int id){
        queueCapacity = maxTasksPerServer;
        tasks = new ArrayBlockingQueue<>(maxTasksPerServer);
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
