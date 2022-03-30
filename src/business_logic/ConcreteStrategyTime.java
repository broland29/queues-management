package business_logic;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyTime implements  Strategy{

    int roli = 0;

    @Override
    public int addTask(List<Server> servers, Task t){
        int i = 0;
        System.out.println("sup " + roli);
        while (i < servers.size() && servers.get(i).isFull()){
            System.out.println("Queue " + i + " is full.");
            i++;
        }

        if (i == servers.size()){
            System.out.println("All queues full");
            return -1;
        }

        Server minimumTimeServer = servers.get(i);
        int minimumTime = minimumTimeServer.getWaitingPeriod();

        while(i < servers.size()){
            Server server = servers.get(i);

            if (server.isFull()){
                i++;
                continue;
            }


            int wp = server.getWaitingPeriod();

            if (wp < minimumTime){
                minimumTime = wp;
                minimumTimeServer = server;
            }

            i++;
        }

        minimumTimeServer.addTask(t);
        return 0;
    }
}
