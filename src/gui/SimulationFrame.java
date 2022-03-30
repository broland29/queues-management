package gui;


import model.Server;
import model.Task;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SimulationFrame extends JFrame {

    public static final int MAX_NUMBER_OF_CLIENTS_ON_GUI = 5;
    public static final int MAX_NUMBER_OF_SERVERS_ON_GUI = 5;
    public static final int MAX_NUMBER_OF_WAITING_CLIENTS_ON_GUI = 5;

    private final Font titleFont = new Font(Font.SANS_SERIF,Font.PLAIN,30);
    private final Font smallerFont = new Font(Font.SANS_SERIF,Font.PLAIN,20);
    private final Font smallestFont = new Font(Font.SANS_SERIF,Font.PLAIN,15);
    private final Border border = BorderFactory.createLineBorder(new Color(103, 89, 94));
    private final int WAITING_WIDTH = 5;

    //information needed to display data
    private int numberOfQueues = 6;
    private int capacityOfQueues = 5;

    //swing components
    JFrame mainFrame;
    JPanel contentPane;

    JPanel headerPanel;         //title, simulation info, time info
    JPanel queuesPanel;         //display queues
    JPanel footerPanel;         //waiting tasks

    JPanel headerPanelUp;       //simulation info
    JPanel headerPanelDown;     //other info
    JLabel titleLabel;          //title
    JLabel leftLabel;           //average waiting time
    JLabel middleLabel;         //time left/ peak hour
    JLabel rightLabel;          //average service time

    JPanel[] separateQueuePanels;   //horizontal panels, one for each queue
    JLabel[][] smallLabels;         //vertical panels, title + one for each slot - for each queue

    JLabel footerLabel;         //display waiting tasks

    public SimulationFrame(int numberOfQueues, int capacityOfQueues){
        this.numberOfQueues = Math.min(numberOfQueues, MAX_NUMBER_OF_SERVERS_ON_GUI);
        this.capacityOfQueues = Math.min(capacityOfQueues, MAX_NUMBER_OF_CLIENTS_ON_GUI);

        mainFrame = new JFrame("Queues Management Application");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(700,600);
        mainFrame.setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(new Color(103, 89, 94));


        headerPanel = new JPanel();
        //headerPanel.setBackground(Color.BLUE);
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new GridLayout(2,1));

        headerPanelUp = new JPanel();
        headerPanelUp.setOpaque(false);
        headerPanelUp.setLayout(new GridBagLayout());
        titleLabel = new JLabel("Simulation Info");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(220, 174, 150));
        headerPanelUp.add(titleLabel);

        headerPanelDown = new JPanel();
        headerPanelDown.setOpaque(false);
        headerPanelDown.setLayout(new GridLayout(1,3));
        leftLabel = new JLabel();
        leftLabel.setHorizontalAlignment(JLabel.CENTER);
        leftLabel.setForeground(new Color(220, 174, 150));
        middleLabel = new JLabel();
        middleLabel.setFont(smallerFont);
        middleLabel.setHorizontalAlignment(JLabel.CENTER);
        middleLabel.setForeground(new Color(220, 174, 150));
        rightLabel = new JLabel();
        rightLabel.setHorizontalAlignment(JLabel.CENTER);
        rightLabel.setForeground(new Color(220, 174, 150));
        headerPanelDown.add(leftLabel);
        headerPanelDown.add(middleLabel);
        headerPanelDown.add(rightLabel);

        headerPanel.add(headerPanelUp);
        headerPanel.add(headerPanelDown);


        queuesPanel = new JPanel();
        queuesPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(103, 89, 94)));
        queuesPanel.setLayout(new GridLayout(1,numberOfQueues));
        separateQueuePanels = new JPanel[numberOfQueues];
        for (int i=0; i<numberOfQueues; i++){
            separateQueuePanels[i] = new JPanel();
            if (i % 2 == 0)
                separateQueuePanels[i].setBackground(new Color(220, 174, 150));
            else
                separateQueuePanels[i].setBackground(new Color(164, 147, 147));
            separateQueuePanels[i].setLayout(new GridLayout(capacityOfQueues + 1,1));
            queuesPanel.add(separateQueuePanels[i]);
        }
        //queuesPanel.setBackground(Color.GREEN);
        queuesPanel.setOpaque(false);

        smallLabels = new JLabel[capacityOfQueues + 1][numberOfQueues];
        for (int i=0; i<numberOfQueues; i++){
            for (int j=0; j<capacityOfQueues + 1; j++){
                smallLabels[j][i] = new JLabel();
                if (j == 0)
                    smallLabels[j][i].setText("Queue " + i);
                else
                    smallLabels[j][i].setText("Slot empty");
                smallLabels[j][i].setHorizontalAlignment(JLabel.CENTER);
                smallLabels[j][i].setBorder(border);
                separateQueuePanels[i].add(smallLabels[j][i]);
            }
        }


        footerPanel = new JPanel();
        footerLabel = new JLabel("Waiting tasks:");
        footerLabel.setFont(smallestFont);
        footerLabel.setForeground(new Color(220, 174, 150));
        footerPanel.add(footerLabel);
        footerPanel.setOpaque(false);
       // footerPanel.setBackground(Color.MAGENTA);

        contentPane.add(headerPanel,BorderLayout.NORTH);
        contentPane.add(queuesPanel,BorderLayout.CENTER);
        contentPane.add(footerPanel,BorderLayout.SOUTH);

        mainFrame.add(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void reDraw(int currentTime, List<Task> generatedTasks, List<Server> servers){
        leftLabel.setText("");
        middleLabel.setText("Simulation time: " + currentTime);
        rightLabel.setText("");

        String waitingClients = "";
        String client;

        int numberOfWaitingTasks = 0;
        for(Task t : generatedTasks){
            client = String.format("(%d,%d,%d) ", t.getId(), t.getArrivalTime(), t.getServiceTime());
            waitingClients = waitingClients.concat(client);
            numberOfWaitingTasks++;
            if (numberOfWaitingTasks == MAX_NUMBER_OF_WAITING_CLIENTS_ON_GUI)
                break;
        }
        if (waitingClients.isEmpty())
            waitingClients = "No waiting clients";
        else
            waitingClients = "Waiting clients: " + waitingClients;

        footerLabel.setText(waitingClients);

        for (int i=0; i<servers.size(); i++) {
            BlockingQueue<Task> tasks = servers.get(i).getTasks();
            if (tasks.isEmpty()) {
                smallLabels[1][i].setText("Slot empty");//can become empty only by finishing last task and having no other after
            } else {
                int j = 1;//first cell name of queue
                for (Task t : tasks){
                    String task = String.format("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime());
                    smallLabels[j][i].setText(task);
                    j++;
                }
                if (j < capacityOfQueues)
                    smallLabels[j][i].setText("Slot empty");
            }
        }
    }

    public void reDrawFinal(String terminationCause, double averageWaitingTime, int peakHour, double averageServiceTime){
        titleLabel.setText(terminationCause);

        leftLabel.setFont(smallestFont);
        middleLabel.setFont(smallestFont);
        rightLabel.setFont(smallestFont);

        leftLabel.setText("Average waiting time: " + averageWaitingTime);
        middleLabel.setText("Peak hour: " + peakHour);
        rightLabel.setText("Average service time: " + averageServiceTime);

        footerLabel.setText("");
    }

    public static void main(String[] args) {
        new SimulationFrame(3,3);
    }
}
