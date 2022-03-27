package business_logic;

import gui.SimulationFrame;
import model.Task;

import java.util.List;

public class SimulationManager implements Runnable{

    public int timeLimit = 100;
    public int maxProcessingTime = 10;
    public int minProcessingTime = 2;
    public int numberOfServers = 3;
    public int numberOfClients = 100;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;

    public SimulationManager(){

    }

    private void generateNRandomTask(){

    }

    @Override
    public void run(){
        int currentTime = 0;
        while (currentTime < timeLimit){
            currentTime++;
        }
    }

    public static void main(String[] args) {
        SimulationManager gen = new SimulationManager();
        Thread t = new Thread(gen);
        t.start();
    }
}
