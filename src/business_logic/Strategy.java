package business_logic;

import model.Server;
import model.Task;

import java.util.List;

public interface Strategy {
    int addTask(List<Server> servers, Task t);
}
