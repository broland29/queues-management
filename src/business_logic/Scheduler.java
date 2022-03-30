package business_logic;

import model.Server;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static business_logic.SelectionPolicy.*;

public class Scheduler {

    private final List<Server> servers;
    private final Strategy strategy;


    public Scheduler(int maxNoServers, int maxTasksPerServer, SelectionPolicy selectionPolicy){

        if (selectionPolicy == SHORTEST_QUEUE)
            strategy = new ConcreteStrategyQueue();
        else if (selectionPolicy == SHORTEST_TIME)
            strategy = new ConcreteStrategyTime();
        else
            strategy = new ConcreteStrategyTime();

        List<Thread> serverThreads = new ArrayList<>();

        //create server objects and threads from them
        servers = new ArrayList<>();
        for(int i=0; i<maxNoServers; i++){
            servers.add(new Server(maxTasksPerServer,i));
            serverThreads.add(new Thread(servers.get(i)));
            serverThreads.get(i).start();
        }
    }

    public int dispatchTask(Task t){
        return strategy.addTask(servers,t);
    }

    public  List<Server> getServers(){
        return servers;
    }

    public boolean areServersEmpty(){
        for (Server s : servers){
            if (!s.isEmpty())
                return false;
        }
        return true;
    }

    public void updateDetails(SimulationManager simulationManager, int time){
        int totalNumberOfTasks = 0;

        for (Server s : servers){
            if (s.isEmpty())
                continue;

            int numberOfTasks = s.getTasks().size();
            totalNumberOfTasks += numberOfTasks;

            simulationManager.addToTotalServiceTime(1);                 //first customer is service-waiting
            simulationManager.addToTotalWaitingTime(numberOfTasks - 1); //other customers are just waiting

        }

        simulationManager.checkIfPeakHour(totalNumberOfTasks,time);
    }
}
