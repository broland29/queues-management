package business_logic;

import model.Server;
import model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t){
        Server minimumTimeServer = servers.get(0);
        int minimumTime = minimumTimeServer.getWaitingPeriod();

        for(int i=1; i< servers.size(); i++){
            int wp = servers.get(i).getWaitingPeriod();

            if (wp < minimumTime){
                minimumTime = wp;
                minimumTimeServer = servers.get(i);
            }
        }
        minimumTimeServer.addTask(t);
    }
}
