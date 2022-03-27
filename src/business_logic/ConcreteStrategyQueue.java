package business_logic;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t){
        Server minimumQueueServer = servers.get(0);
        int minimumQueue = minimumQueueServer.getQueueSize();

        for(int i=1; i< servers.size(); i++){
            int qs = servers.get(i).getQueueSize();

            if (qs < minimumQueue){
                minimumQueue = qs;
                minimumQueueServer = servers.get(i);
            }
        }
        minimumQueueServer.addTask(t);
    }
}
