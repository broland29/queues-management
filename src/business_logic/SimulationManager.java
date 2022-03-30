package business_logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gui.Controller;
import gui.SetupFrame;
import gui.SimulationFrame;
import model.Server;
import model.Task;

import static business_logic.SelectionPolicy.SHORTEST_TIME;


public class SimulationManager implements Runnable{

    //limits defined in order to avoid huge simulations
    public static final int MAX_TIME_LIMIT = 200;
    public static final int MAX_NUMBER_OF_CLIENTS = 1000;
    public static final int MAX_NUMBER_OF_SERVERS = 20;

    //values hard-coded since not specified in problem statement
    public static final int QUEUE_CAPACITY = 5;
    private final SelectionPolicy selectionPolicy = SHORTEST_TIME;

    //status of the simulation
    private SimulationStatus simulationStatus;

    //data from the GUI
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;


    private Scheduler scheduler;
    private final SetupFrame setupFrame;
    private SimulationFrame simulationFrame;
    private List<Task> generatedTasks;

    //hard-coded simulation customization
    private static final int SECOND = 1000;
    private static final int LOG_WIDTH = 10;

    //data needed to show details after simulation
    private int totalWaitingTime;
    private int totalServiceTime;
    private int satisfiedClients;
    private int peakHour;
    private int mostNumberOfClients;

    public SimulationManager(SetupFrame setupFrame){
        this.setupFrame = setupFrame;
        simulationStatus = SimulationStatus.WAITING_FOR_INFO;

        totalWaitingTime = 0;
        totalServiceTime = 0;
        satisfiedClients = 0;
        peakHour = 0;
        mostNumberOfClients = 0;
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

        scheduler = new Scheduler(numberOfServers, QUEUE_CAPACITY, selectionPolicy);
        simulationFrame = new SimulationFrame(numberOfServers, QUEUE_CAPACITY);

        generateRandomTasks();

        Thread thread = new Thread(this);
        thread.start();
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

                        if (scheduler.dispatchTask(t) == 0){    //only remove if successfully dispatched
                            generatedTasks.remove(t);
                            satisfiedClients++;
                        }

                        else {
                            totalWaitingTime += generatedTasks.size() - i;  //waiting outside of queue
                            break;  //if dispatching unsuccessful (i.e. all queues are full) no need to check for other tasks as well
                        }
                    }
                    else
                        break;


                }

                printLog(currentTime);
                if (simulationStatus == SimulationStatus.RUNNING)
                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());
                scheduler.updateDetails(this,currentTime);

                try {
                    Thread.sleep(SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isJobDone()){
                    String terminationCause = "Finished. " + currentTime + " seconds used out of " + timeLimit + ".";
                    System.out.println(terminationCause);

                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());  //TODO: needed? else tasks not in last status
                    simulationFrame.reDrawFinal(terminationCause,getAverageWaitingTime(),peakHour,getAverageServiceTime());

                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    setupFrame.setMessage("Validate");
                }
                else if (currentTime == timeLimit){
                    String terminationCause = "Time limit reached. Tasks not finished";

                    System.out.println(terminationCause);

                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());  //TODO: needed? else tasks not in last status
                    simulationFrame.reDrawFinal(terminationCause,getAverageWaitingTime(),peakHour,getAverageServiceTime());

                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    setupFrame.setMessage("Validate");
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

    private void notifyServers(){

        List<Server> servers = scheduler.getServers();
        for (Server s : servers){
            s.decrementTime();
        }
    }

    public void addToTotalWaitingTime(int num){
        totalWaitingTime += num;
    }

    public void addToTotalServiceTime(int num){
        totalServiceTime += num;
    }

    public void checkIfPeakHour(int numberOfClients, int time){
        if (numberOfClients > mostNumberOfClients){
            mostNumberOfClients = numberOfClients;
            peakHour = time;
        }
    }

    private double getAverageWaitingTime() {
        return (double)totalWaitingTime / satisfiedClients;
    }

    private double getAverageServiceTime() {
        return (double)totalServiceTime / satisfiedClients;
    }

    public static void main(String[] args) {

        SetupFrame setupFrame = new SetupFrame();
        SimulationManager simulationManager = new SimulationManager(setupFrame);
        new Controller(simulationManager,setupFrame);

    }
}
