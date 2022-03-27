package gui;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {

    JFrame mainFrame;
    JPanel contentPane;

    JPanel titlePanel;
    JPanel setupPanel;
    JPanel displayPanelUp;
    JPanel displayPanelDown;

    JPanel setupPanelUp;
    JPanel setupPanelDown;

    //titlePanel related
    JLabel titleLabel;

    //setupPanel related
    JPanel setupPanelUpLeft;
    JPanel setupPanelUpRight;
    JPanel setupPanelDownLeft;
    JPanel setupPanelDownMiddle;
    JPanel setupPanelDownRight;
    JTextField clientCount;
    JTextField queueCount;
    JTextField maxTime;
    JTextField arrivalIntervalFrom;
    JTextField arrivalIntervalTo;
    JTextField serviceIntervalFrom;
    JTextField serviceIntervalTo;

    public SimulationFrame(){
        mainFrame = new JFrame("Queues Management Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700,600);
        mainFrame.setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(4,1));
        contentPane.setBackground(new Color(255,0,0));

        //titlePanel
        titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0,150,0));
        titleLabel = new JLabel("Queues Management Application");
        titleLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,30));
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.add(titleLabel);


        //setupPanel
        setupPanel = new JPanel();
        setupPanel.setLayout(new GridLayout(2,1));

        setupPanelUp = new JPanel();
        setupPanelUp.setBackground(new Color(100,0,0));
        setupPanelUp.setLayout(new GridLayout(1,2));

        setupPanelUpLeft = new JPanel();
        clientCount = new JTextField("n");
        setupPanelUpLeft.add(clientCount);

        setupPanelUpRight = new JPanel();
        queueCount = new JTextField("q");
        setupPanelUpRight.add(queueCount);

        setupPanelUp.add(setupPanelUpLeft);
        setupPanelUp.add(setupPanelUpRight);


        setupPanelDown = new JPanel();
        setupPanelDown.setBackground(new Color(80,0,0));
        setupPanelDown.setLayout(new GridLayout(1,3));

        setupPanelDownLeft = new JPanel();
        maxTime = new JTextField("max time");
        setupPanelDownLeft.add(maxTime);

        setupPanelDownMiddle = new JPanel();
        setupPanelDownMiddle.setLayout(new GridLayout(1,2));
        arrivalIntervalFrom = new JTextField("arrive from");
        arrivalIntervalTo = new JTextField("arrive to");
        setupPanelDownMiddle.add(arrivalIntervalFrom);
        setupPanelDownMiddle.add(arrivalIntervalTo);

        setupPanelDownRight = new JPanel();


        setupPanelDown.add(setupPanelDownLeft);
        setupPanelDown.add(setupPanelDownMiddle);
        setupPanelDown.add(setupPanelDownRight);

        setupPanel.add(setupPanelUp);
        setupPanel.add(setupPanelDown);


        //displayPanelUp
        displayPanelUp = new JPanel();
        displayPanelUp.setBackground(new Color(0,0,100));


        //displayPanelDown
        displayPanelDown = new JPanel();
        displayPanelDown.setBackground(new Color(0,0,80));



        contentPane.add(titlePanel);
        contentPane.add(setupPanel);
        contentPane.add(displayPanelUp);
        contentPane.add(displayPanelDown);

        mainFrame.add(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void redraw(){

    }

    public static void main(String[] args) {
        new SimulationFrame();
    }
}
