package business_logic;

import gui.Controller;
import gui.SetupFrame;
import gui.SimulationFrame;
import model.Server;
import model.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulationManager implements Runnable{

    //limits defined in order to avoid huge simulations
    public static final int MAX_TIME_LIMIT = 200;
    public static final int MAX_NUMBER_OF_CLIENTS = 1000;
    public static final int MAX_NUMBER_OF_SERVERS = 20;
    public static final int MAX_QUEUE_CAPACITY = 5;

    private SimulationStatus simulationStatus;

    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int minArrivalTime;
    public int maxArrivalTime;
    public int numberOfServers;
    public int numberOfClients;
    public SelectionPolicy selectionPolicy;

    private Scheduler scheduler;
    private final SetupFrame setupFrame;
    private SimulationFrame simulationFrame;
    private List<Task> generatedTasks;

    public static final int SECOND = 1000;
    public static final int LOG_WIDTH = 10;


    public SimulationManager(SetupFrame setupFrame){
        this.setupFrame = setupFrame;

        selectionPolicy = SelectionPolicy.SHORTEST_TIME;
        simulationStatus = SimulationStatus.WAITING_FOR_INFO;

    }


    //comparator for sorting generated tasks
    private static class TaskComparator implements Comparator<Task>{
        @Override
        public int compare(Task task1, Task task2) {
            return task1.getArrivalTime() - task2.getArrivalTime();
        }
    }


    private void generateRandomTasks(){
        generatedTasks = new ArrayList<>();

        //generate random tasks
        for (int i=0; i<numberOfClients; i++){
            //random processing time, in [minProcessingTime,maxProcessingTime]
            int serviceTime = (int) ((Math.random() * (maxProcessingTime - minProcessingTime)) + minProcessingTime);
            //random arrival time, in [1,timeLimit]
            int arrivalTime = (int) (Math.random() * (maxArrivalTime - minArrivalTime) + minArrivalTime);
            generatedTasks.add(new Task(arrivalTime,serviceTime,i));
        }

        //sort them by their arrival time
        generatedTasks.sort(new TaskComparator());
    }

    private boolean isJobDone(){
        return generatedTasks.isEmpty() && scheduler.areServersEmpty();
    }

    @Override
    public void run(){

        int currentTime = 0;

        while (true){
            //System.out.println(simulationStatus);
            if(simulationStatus == SimulationStatus.RUNNING){
                int i = 0;
                while(i<generatedTasks.size()){
                    Task t = generatedTasks.get(i);
                    if (t.getArrivalTime() <= currentTime){ //also take tasks which may came earlier

                        if (scheduler.dispatchTask(t) == 0) //only remove if successfully dispatched
                            generatedTasks.remove(t);
                        else
                            break;  //if dispatching unsuccessful (i.e. all queues are full) no need to check for other tasks as well
                    }
                    else
                        break;


                }

                printLog(currentTime);
                if (simulationStatus == SimulationStatus.RUNNING)
                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());

                try {
                    Thread.sleep(SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                if (currentTime == timeLimit){
                    String terminationCause = "Time limit reached (" + timeLimit + ").";
                    String tasksStatus;

                    System.out.println(terminationCause);

                    if (isJobDone())
                        tasksStatus = "Tasks finished.";
                    else
                        tasksStatus = "Tasks not finished.";

                    System.out.println(tasksStatus);

                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());  //TODO: needed? else tasks not in last status
                    simulationFrame.reDrawFinal(terminationCause,tasksStatus);

                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    setupFrame.setMessage("Validate");
                    //break;
                }
                if (isJobDone()){
                    String terminationCause = "Finished. " + currentTime + " seconds used out of " + timeLimit + ".";
                    System.out.println(terminationCause);

                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());  //TODO: needed? else tasks not in last status
                    simulationFrame.reDrawFinal(terminationCause,"");

                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    setupFrame.setMessage("Validate");
                    //break;
                }

                currentTime++;
                notifyServers();
            }
            //System.out.println(simulationStatus);
        }
    }

    private void printLog(int currentTime) {
        System.out.printf("Time: %d\n\n", currentTime);
        System.out.println("Waiting clients: ");

        int inOneRow = 0;
        for (Task t : generatedTasks) {
            System.out.printf("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime());
            inOneRow++;
            if (inOneRow == LOG_WIDTH) {
                System.out.println();
                inOneRow = 0;
            }
        }

        System.out.println();
        if (inOneRow != 0)
            System.out.println();

        for (Server s : scheduler.getServers()) {
            System.out.println("Queue " + s.getId() + ":");
            if (s.isEmpty()) {
                System.out.println("closed");
            } else {
                inOneRow = 0;
                for (Task t : s.getTasks()) {
                    System.out.printf("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime());
                    inOneRow++;
                    if (inOneRow == LOG_WIDTH) {
                        System.out.println();
                        inOneRow = 0;
                    }
                }
                System.out.println();
            }
        }
        System.out.println("\n----------------------------------------\n");
    }

    public SimulationStatus getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(SimulationStatus simulationStatus) {
        this.simulationStatus = simulationStatus;
    }



    public void startSimulation(int numberOfClients, int numberOfServers, int timeLimit,
                                int minArrivalTime, int maxArrivalTime,
                                int minProcessingTime, int maxProcessingTime){

        simulationStatus = SimulationStatus.RUNNING;

        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;

        scheduler = new Scheduler(numberOfServers,MAX_QUEUE_CAPACITY);
        simulationFrame = new SimulationFrame(numberOfServers,MAX_QUEUE_CAPACITY);

        generateRandomTasks();

        Thread thread = new Thread(this);
        thread.start();
    }

    private void notifyServers(){

        List<Server> servers = scheduler.getServers();
        for (Server s : servers){
            s.decrementTime();
        }
    }

    public static void main(String[] args) {

        SetupFrame setupFrame = new SetupFrame();
        SimulationManager simulationManager = new SimulationManager(setupFrame);
        new Controller(simulationManager,setupFrame);

    }
}
