package business_logic;

import model.Server;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    private List<Thread> serverThreads;     //???

    public Scheduler(){}

    public Scheduler(int maxNoServers, int maxTasksPerServer){
        //make sure nothing is null
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        strategy = new ConcreteStrategyQueue();
        serverThreads = new ArrayList<>();

        //create server objects and threads from them
        servers = new ArrayList<>();
        for(int i=0; i<maxNoServers; i++){
            servers.add(new Server(maxTasksPerServer,i));
            serverThreads.add(new Thread(servers.get(i)));
            serverThreads.get(i).start();
        }
    }

    public void changeStrategy(SelectionPolicy policy){
        if (policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }
        if (policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task t){
        System.out.println("in dispatch");
        strategy.addTask(servers,t);
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
}
