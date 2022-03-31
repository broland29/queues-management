package business_logic;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyTime implements  Strategy{


    @Override
    public int addTask(List<Server> servers, Task t){
        int i = 0;
        while (i < servers.size() && servers.get(i).isFull()){
            i++;
        }

        if (i == servers.size()){
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
