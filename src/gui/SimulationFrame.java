package gui;


import model.Server;
import model.Task;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SimulationFrame extends JFrame {

    private final Font titleFont = new Font(Font.SANS_SERIF,Font.PLAIN,30);
    private final Font smallerFont = new Font(Font.SANS_SERIF,Font.PLAIN,20);
    private final Border border = BorderFactory.createLineBorder(Color.black);
    //private final Border border = BorderFactory.createMatteBorder(2, 1, 2, 1, Color.BLACK);
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
        this.numberOfQueues = numberOfQueues;
        this.capacityOfQueues = capacityOfQueues;

        mainFrame = new JFrame("Queues Management Application");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(700,600);
        mainFrame.setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.BLACK);


        headerPanel = new JPanel();
        headerPanel.setBackground(Color.BLUE);
        headerPanel.setLayout(new GridLayout(2,1));

        headerPanelUp = new JPanel();
        headerPanelUp.setLayout(new GridBagLayout());
        titleLabel = new JLabel("Simulation Info");
        titleLabel.setFont(titleFont);
        headerPanelUp.add(titleLabel);

        headerPanelDown = new JPanel();
        headerPanelDown.setLayout(new GridLayout(1,3));
        leftLabel = new JLabel("average waiting time: ");
        leftLabel.setFont(smallerFont);
        leftLabel.setHorizontalAlignment(JLabel.CENTER);
        middleLabel = new JLabel("peak hour: ");
        middleLabel.setFont(smallerFont);
        middleLabel.setHorizontalAlignment(JLabel.CENTER);
        rightLabel = new JLabel("average service time: ");
        rightLabel.setFont(smallerFont);
        rightLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanelDown.add(leftLabel);
        headerPanelDown.add(middleLabel);
        headerPanelDown.add(rightLabel);

        headerPanel.add(headerPanelUp);
        headerPanel.add(headerPanelDown);


        queuesPanel = new JPanel();
        queuesPanel.setLayout(new GridLayout(1,numberOfQueues));
        separateQueuePanels = new JPanel[numberOfQueues];
        for (int i=0; i<numberOfQueues; i++){
            separateQueuePanels[i] = new JPanel();
            if (i % 2 == 0)
                separateQueuePanels[i].setBackground(Color.ORANGE);
            else
                separateQueuePanels[i].setBackground(Color.PINK);
            separateQueuePanels[i].setLayout(new GridLayout(capacityOfQueues + 1,1));
            queuesPanel.add(separateQueuePanels[i]);
        }
        queuesPanel.setBackground(Color.GREEN);

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
        footerLabel.setFont(smallerFont);
        footerPanel.add(footerLabel);
        footerPanel.setBackground(Color.MAGENTA);

        contentPane.add(headerPanel,BorderLayout.NORTH);
        contentPane.add(queuesPanel,BorderLayout.CENTER);
        contentPane.add(footerPanel,BorderLayout.SOUTH);

        mainFrame.add(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void reDraw(int currentTime, List<Task> generatedTasks, List<Server> servers){
        middleLabel.setText("Time left: " + currentTime);

        String waitingClients = "Waiting clients:\n";
        String client;

        int inOneRow = 0;
        for(Task t : generatedTasks){
            client = String.format("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime());
            waitingClients = waitingClients.concat(client);
            inOneRow++;
            if (inOneRow == WAITING_WIDTH) {
                waitingClients = waitingClients + "\n";
                inOneRow = 0;
            }
        }

        for (int i=0; i<servers.size(); i++) {
            BlockingQueue<Task> tasks = servers.get(i).getTasks();
            if (tasks.isEmpty()) {
                smallLabels[0][i].setText("Slot empty");//can become empty only by finishing last task and having no other after
            } else {
                int j = 0;
                for (Task t : tasks){
                    String task = String.format("(%d,%d,%d)   ", t.getId(), t.getArrivalTime(), t.getServiceTime());
                    smallLabels[j][i].setText(task);
                    j++;
                }
            }
        }
    }

    public static void main(String[] args) {
        //new SimulationFrame();
    }
}