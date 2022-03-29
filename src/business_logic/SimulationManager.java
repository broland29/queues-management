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

    private SimulationStatus simulationStatus = SimulationStatus.WAITING_FOR_INFO;

    public int timeLimit = 20;
    public static int maxProcessingTime = 5;
    public int minProcessingTime = 2;
    public int numberOfServers = 3;
    public int numberOfClients = 10;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    private Scheduler scheduler;        //queue management and client distribution
    private SetupFrame frame;      //graphical user interface
    private SimulationFrame simulationFrame;
    private Controller controller;
    private List<Task> generatedTasks;  //pool of tasks

    public static final int SECOND = 3000;
    public static final int LOG_WIDTH = 3;

    public SimulationManager(){
        //scheduler = new Scheduler(numberOfServers,20);  //TODO: find out what value
        frame = new SetupFrame();
//        controller = new Controller(this);
        generatedTasks = new ArrayList<>();
//
//        generateNRandomTask();
    }

    public SimulationManager(int clientCount, int queueCount, int maxTime,
                             int arrivalIntervalFrom, int arrivalIntervalTo,
                             int serviceIntervalFrom, int serviceIntervalTo){

        //TODO: meh
        frame = new SetupFrame();
        simulationStatus = SimulationStatus.RUNNING;

        numberOfClients = clientCount;
        numberOfServers = queueCount;
        timeLimit = maxTime;
        minProcessingTime = serviceIntervalFrom;
        maxProcessingTime = serviceIntervalTo;

        scheduler = new Scheduler(numberOfServers,5);  //TODO: find out what value
        simulationFrame = new SimulationFrame(numberOfServers,5);
        controller = new Controller(this);
        generatedTasks = new ArrayList<>();

        generateNRandomTask(arrivalIntervalFrom,arrivalIntervalTo);
    }

    //comparator for sorting generated tasks
    private static class TaskComparator implements Comparator<Task>{
        @Override
        public int compare(Task task1, Task task2) {
            return task1.getArrivalTime() - task2.getArrivalTime();
        }
    }

    private void generateNRandomTask(int minArrivalTime, int maxArrivalTime){

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

        //for(Task t : generatedTasks){
        //   t.printTask();
        //}
    }

    private boolean isJobDone(){
        return generatedTasks.isEmpty() && scheduler.areServersEmpty();
    }

    @Override
    public void run(){
        int currentTime = 0;
        while (true){

            if(simulationStatus == SimulationStatus.RUNNING){
                int i = 0;
                while(i<generatedTasks.size()){
                    Task t = generatedTasks.get(i);
                    if (t.getArrivalTime() == currentTime){
                        scheduler.dispatchTask(t);
                        generatedTasks.remove(t);
                    }
                    else{
                        i++; //TODO: can break?
                    }
                }

                printLog(currentTime);
                if (simulationStatus == SimulationStatus.RUNNING)
                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());

                try {
                    Thread.sleep(SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                currentTime++;

                if (currentTime == timeLimit){
                    System.out.println("Time limit reached (" + timeLimit + ").");
                    if (isJobDone()){
                        System.out.println("Tasks finished.");
                    }
                    else{
                        System.out.println("Tasks not finished.");
                    }

                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    frame.setMessage("Validate");
                    break;
                }
                if (isJobDone()){
                    System.out.println("Tasks finished before reaching time limit (" + timeLimit + ").");
                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    frame.setMessage("Validate");
                    break;
                }
            }
            //else{
                //System.out.println("Not running...");
            //}
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

    public static void main(String[] args) {

        SimulationManager gen = new SimulationManager();

        new Controller(gen);
        Thread t = new Thread(gen);
        t.start();
    }

    public static void startSimulation(int clientCount, int queueCount, int maxTime,
                                int arrivalIntervalFrom, int arrivalIntervalTo,
                                int serviceIntervalFrom, int serviceIntervalTo){
        SimulationManager gen = new SimulationManager(clientCount, queueCount, maxTime, arrivalIntervalFrom, arrivalIntervalTo, serviceIntervalFrom, serviceIntervalTo);
        Thread t = new Thread(gen);
        t.start();
    }

    public SetupFrame getFrame() {
        return frame;
    }
}
