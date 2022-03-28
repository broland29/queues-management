package gui;

import business_logic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;

public class SetupFrame extends JFrame {

    final String HELP = "A project realised by broland29.";

    JFrame mainFrame;
    JPanel contentPane;

    JPanel titlePanel;
    JPanel setupPanelUp;
    JPanel setupPanelDown;
    JPanel buttonPanel;
    JPanel messagePanel;

    //titlePanel related
    JLabel titleLabel;

    //setupPanel related
    JPanel setupPanelUpUpper;
    JPanel setupPanelUpUpperLeft;
    JPanel setupPanelUpUpperRight;
    JPanel setupPanelUpLower;
    //setupPanelDown is one single panel

    JTextField clientCount;
    JTextField queueCount;
    JLabel clientCountLabel;
    JLabel queueCountLabel;

    JTextField maxTime;
    JLabel maxTimeLabel;

    JTextField arrivalIntervalFrom;
    JTextField arrivalIntervalTo;
    JTextField serviceIntervalFrom;
    JTextField serviceIntervalTo;
    JLabel arrivalIntervalFromLabel;
    JLabel arrivalIntervalToLabel;
    JLabel serviceIntervalFromLabel;
    JLabel serviceIntervalToLabel;

    //buttonPanel related
    JButton validateAndStartButton;
    JButton helpAndHaltButton;

    //messagePanel related
    JLabel messageLabel;

    public SetupFrame(){
        mainFrame = new JFrame("Queues Management Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700,600);
        mainFrame.setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(5,1));
        contentPane.setBackground(new Color(255,0,0));

        //titlePanel
        titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0,150,0));
        titleLabel = new JLabel("Queues Management Application");
        titleLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,30));
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.add(titleLabel);


        //setupPanel
        setupPanelUp = new JPanel();
        setupPanelUp.setBackground(new Color(100,0,0));
        setupPanelUp.setLayout(new GridLayout(2,1));

        setupPanelUpUpper = new JPanel();
        setupPanelUpUpper.setLayout(new GridLayout(1,4));

        setupPanelUpUpperLeft = new JPanel();
        clientCountLabel = new JLabel("Number of clients:");
        clientCount = new JTextField("",5);
        setupPanelUpUpperLeft.add(clientCountLabel);
        setupPanelUpUpperLeft.add(clientCount);

        setupPanelUpUpperRight = new JPanel();
        queueCountLabel = new JLabel("Number of queues:");
        queueCount = new JTextField("",5);
        setupPanelUpUpperRight.add(queueCountLabel);
        setupPanelUpUpperRight.add(queueCount);

        setupPanelUpUpper.add(setupPanelUpUpperLeft);
        setupPanelUpUpper.add(setupPanelUpUpperRight);

        setupPanelUpLower = new JPanel();
        maxTimeLabel = new JLabel("Max time:");
        maxTime = new JTextField("",5);
        setupPanelUpLower.add(maxTimeLabel);
        setupPanelUpLower.add(maxTime);

        setupPanelUp.add(setupPanelUpUpper);
        setupPanelUp.add(setupPanelUpLower);


        setupPanelDown = new JPanel();
        setupPanelDown.setBackground(new Color(80,0,200));
        setupPanelDown.setLayout(new GridBagLayout());

        arrivalIntervalFromLabel = new JLabel("Arrive from:");
        arrivalIntervalFrom = new JTextField("",5);
        arrivalIntervalToLabel = new JLabel("Arrive to:");
        arrivalIntervalTo = new JTextField("",5);
        serviceIntervalFromLabel = new JLabel("Service from:");
        serviceIntervalFrom = new JTextField("",5);
        serviceIntervalToLabel = new JLabel("Service to:");
        serviceIntervalTo = new JTextField("",5);
        setupPanelDown.add(arrivalIntervalFromLabel);
        setupPanelDown.add(arrivalIntervalFrom);
        setupPanelDown.add(arrivalIntervalToLabel);
        setupPanelDown.add(arrivalIntervalTo);
        setupPanelDown.add(serviceIntervalFromLabel);
        setupPanelDown.add(serviceIntervalFrom);
        setupPanelDown.add(serviceIntervalToLabel);
        setupPanelDown.add(serviceIntervalTo);

        //buttonPanel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0,0,100));

        validateAndStartButton = new JButton("Validate");
        helpAndHaltButton = new JButton("Help");

        buttonPanel.add(validateAndStartButton);
        buttonPanel.add(helpAndHaltButton);

        //messagePanel
        messagePanel = new JPanel();
        messagePanel.setBackground(new Color(0,150,200));
        messagePanel.setLayout(new GridBagLayout());
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));
        messagePanel.add(messageLabel);

        contentPane.add(titlePanel);
        contentPane.add(setupPanelUp);
        contentPane.add(setupPanelDown);
        contentPane.add(buttonPanel);
        contentPane.add(messagePanel);

        mainFrame.add(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void redraw(){

    }

    public void close(){
        this.dispose();
    }

    public void addButtonListener(ActionListener bl){
        validateAndStartButton.addActionListener(bl);
        helpAndHaltButton.addActionListener(bl);
    }

    public static void main(String[] args) {

        SimulationManager sm = new SimulationManager();
        //new Controller(new SetupFrame());
    }

    public JButton getValidateAndStartButton() {
        return validateAndStartButton;
    }

    public JButton getHelpAndHaltButton() {
        return helpAndHaltButton;
    }

    //open up the help popup
    public void openHelp(){
        final String[] options = {"Visit Me","Back"};
        int input = JOptionPane.showOptionDialog(null, HELP, "Help", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, 0);
        if (input == JOptionPane.OK_OPTION){
            try{
                Desktop.getDesktop().browse(new URI("https://github.com/broland29"));
            }
            catch (Exception e){
                System.out.println("Something went wrong");
            }
        }
    }

    public int getText(TextFieldNames textFieldName) throws NumberFormatException{

        String text;
        switch (textFieldName) {
            case CLIENT_COUNT -> text = clientCount.getText();
            case QUEUE_COUNT -> text = queueCount.getText();
            case MAX_TIME-> text = maxTime.getText();
            case ARRIVAL_INTERVAL_FROM -> text = arrivalIntervalFrom.getText();
            case ARRIVAL_INTERVAL_TO-> text = arrivalIntervalTo.getText();
            case SERVICE_INTERVAL_FROM -> text = serviceIntervalFrom.getText();
            case SERVICE_INTERVAL_TO -> text = serviceIntervalTo.getText();
            default -> {
                System.out.println("Invalid input of getText. (SetupFrame)");
                text = "";
            }
        }

        int value;
        try{
            value = Integer.parseInt(text);
        }catch (NumberFormatException nfe){
            throw new NumberFormatException("Invalid input: " + textFieldName);
        }

        if (value < 0){
            throw new NegativeNumberException("Negative " + textFieldName + " not allowed");
        }

        return value;
    }

    public void setMessage(String msg){
        messageLabel.setText(msg);
    }

    public void setValidateAndStartButton(String text){
        validateAndStartButton.setText(text);
    }

    public void setHelpAndHaltButton(String text){
        helpAndHaltButton.setText(text);
    }

    public void disableButton(){
        validateAndStartButton.setEnabled(false);
    }

    public void enableButton(){
        validateAndStartButton.setEnabled(true);
    }
}
