package business_logic;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t){
        int i = 0;

        while (i < servers.size() && servers.get(i).isFull()){
            System.out.println("Queue " + i + " is full.");
            i++;
        }

        if (i == servers.size()){
            System.out.println("All queues full");
            return;
        }

        Server minimumQueueServer = servers.get(0);
        int minimumQueue = minimumQueueServer.getQueueSize();

        while(i < servers.size()){
            Server server = servers.get(i);

            if (server.isFull()){
                i++;
                continue;
            }

            int qs = server.getQueueSize();

            if (qs < minimumQueue){
                minimumQueue = qs;
                minimumQueueServer = server;
            }

            i++;
        }
        minimumQueueServer.addTask(t);
    }
}
