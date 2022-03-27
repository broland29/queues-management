package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server(){

    }

    public void addTask(Task newTask){

    }

    public void run(){
        //while(true){
        //
        //}
    }

    public Task[] getTasks(){
        return null;
    }
}
