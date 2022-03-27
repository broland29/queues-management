package business_logic;

import gui.SimulationFrame;
import model.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulationManager implements Runnable{

    public int timeLimit = 100;
    public static int maxProcessingTime = 10;
    public int minProcessingTime = 2;
    public int numberOfServers = 3;
    public int numberOfClients = 100;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    private Scheduler scheduler;        //queue management and client distribution
    private SimulationFrame frame;      //graphical user interface
    private List<Task> generatedTasks;  //pool of tasks

    final int SECOND = 1000;

    public SimulationManager(){
        scheduler = new Scheduler(numberOfServers,20);  //TODO: find out what value
        frame = new SimulationFrame();
        generatedTasks = new ArrayList<>();
    }

    //comparator for sorting generated tasks
    private static class TaskComparator implements Comparator<Task>{
        @Override
        public int compare(Task task1, Task task2) {
            return task1.getArrivalTime() - task2.getArrivalTime();
        }
    }

    private void generateNRandomTask(){

        //generate random tasks
        for (int i=0; i<numberOfClients; i++){
            //random processing time, in [minProcessingTime,maxProcessingTime]
            int serviceTime = (int) ((Math.random() * (maxProcessingTime - minProcessingTime)) + minProcessingTime);
            //random arrival time, in [0,timeLimit]
            int arrivalTime = (int) (Math.random() * (timeLimit));
            generatedTasks.add(new Task(arrivalTime,serviceTime));
        }

        //sort them by their arrival time
        generatedTasks.sort(new TaskComparator());

        //for(Task t : generatedTasks){
        //   t.printTask();
        //}
    }

    @Override
    public void run(){
        int currentTime = 0;
        while (currentTime < timeLimit){
            System.out.println(currentTime);

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
            frame.redraw();
            //currentTime++;

            try {
                Thread.sleep(SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTime++;
        }
    }

    public static void main(String[] args) {

        SimulationManager gen = new SimulationManager();
        Thread t = new Thread(gen);
        t.start();

        gen.generateNRandomTask();
    }
}
