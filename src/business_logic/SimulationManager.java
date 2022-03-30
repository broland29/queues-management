package business_logic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gui.Controller;
import gui.SetupFrame;
import gui.SimulationFrame;
import model.Server;
import model.Task;

import static business_logic.SelectionPolicy.SHORTEST_QUEUE;
import static business_logic.SelectionPolicy.SHORTEST_TIME;


public class SimulationManager implements Runnable{

    //limits defined in order to avoid huge simulations
    public static final int MAX_TIME_LIMIT = 200;
    public static final int MAX_NUMBER_OF_CLIENTS = 1000;
    public static final int MAX_NUMBER_OF_SERVERS = 20;

    //values hard-coded since not specified in problem statement
    public static final int QUEUE_CAPACITY = 5;
    private final SelectionPolicy selectionPolicy = SHORTEST_QUEUE;

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

        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

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

                printLog(currentTime,bufferedWriter);

                if (simulationStatus == SimulationStatus.RUNNING)
                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());
                scheduler.updateDetails(this,currentTime);

                try {
                    Thread.sleep(SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isJobDone() || currentTime == timeLimit){
                    String terminationCause;

                    if (isJobDone())
                        terminationCause = "Finished. " + currentTime + " seconds used out of " + timeLimit + ".";
                    else
                        terminationCause = "Time limit reached. Tasks not finished";

                    printDetails(bufferedWriter,terminationCause);

                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    simulationFrame.reDraw(currentTime,generatedTasks,scheduler.getServers());  //TODO: needed? else tasks not in last status
                    simulationFrame.reDrawFinal(terminationCause,getAverageWaitingTime(),peakHour,getAverageServiceTime());

                    //prepare for next simulation
                    simulationStatus = SimulationStatus.WAITING_FOR_INFO;
                    setupFrame.setMessage("Validate");
                }

                currentTime++;
                notifyServers();
            }
        }
    }

    private void printLog(int currentTime, BufferedWriter bufferedWriter){
        printLogLine(bufferedWriter,String.format("Time: %d\n", currentTime));
        printLogLine(bufferedWriter,"Waiting clients:");

        int inOneRow = 0;

        for (Task t : generatedTasks) {
            printLogLineWithoutNewLine(bufferedWriter,String.format("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime()));
            inOneRow++;
            if (inOneRow == LOG_WIDTH) {
                printLogLine(bufferedWriter,"");//new line
                inOneRow = 0;
            }
        }

        printLogLine(bufferedWriter,"");
        if (inOneRow != 0){
            printLogLine(bufferedWriter,"");
        }

        for (Server s : scheduler.getServers()) {
            printLogLine(bufferedWriter,"Queue " + s.getId() + ":");

            if (s.isEmpty()) {
                printLogLine(bufferedWriter,"closed");
            } else {
                inOneRow = 0;
                for (Task t : s.getTasks()) {

                    printLogLineWithoutNewLine(bufferedWriter,String.format("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime()));
                    inOneRow++;

                    if (inOneRow == LOG_WIDTH) {
                        printLogLine(bufferedWriter,"");
                        inOneRow = 0;
                    }
                }
                printLogLine(bufferedWriter,"");
            }
        }
        System.out.println(currentTime);
        printLogLine(bufferedWriter,"\n----------------------------------------\n");
    }

    private void printLogLine(BufferedWriter bufferedWriter, String line){
        System.out.println(line);
        try {
            bufferedWriter.write(line + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printLogLineWithoutNewLine(BufferedWriter bufferedWriter, String line){
        System.out.print(line);
        try {
            bufferedWriter.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printDetails(BufferedWriter bufferedWriter, String terminationCause){
            printLogLine(bufferedWriter,terminationCause + "\n");
            printLogLine(bufferedWriter,"Average waiting time: " + getAverageWaitingTime());
            printLogLine(bufferedWriter,"Peak hour: " + peakHour);
            printLogLine(bufferedWriter,"Average service time: " + getAverageServiceTime() + "\n");
            printLogLine(bufferedWriter,"Copyright: broland29");
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
        double avg = (double)totalWaitingTime / satisfiedClients;
        return Math.round(avg * 100.0) / 100.0;
    }

    private double getAverageServiceTime() {
        double avg =  (double)totalServiceTime / satisfiedClients;
        return Math.round(avg * 100.0) / 100.0;
    }

    public static void main(String[] args) {

        SetupFrame setupFrame = new SetupFrame();
        SimulationManager simulationManager = new SimulationManager(setupFrame);
        new Controller(simulationManager,setupFrame);

    }
}
