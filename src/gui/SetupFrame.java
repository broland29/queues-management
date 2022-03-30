package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;

public class SetupFrame extends JFrame {

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

    JTextField numberOfClientsField;
    JTextField numberOfServersField;
    JLabel numberOfClientsLabel;
    JLabel numberOfServersLabel;

    JTextField timeLimitField;
    JLabel timeLimitLabel;

    JTextField minArrivalTimeField;
    JTextField maxArrivalTimeField;
    JTextField minProcessingTimeField;
    JTextField maxProcessingTimeField;
    JLabel minArrivalTimeLabel;
    JLabel maxArrivalTimeLabel;
    JLabel minProcessingTimeLabel;
    JLabel maxProcessingTimeLabel;

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
        contentPane.setBackground(new Color(103, 89, 94));

        //titlePanel
        titlePanel = new JPanel();
        //titlePanel.setBackground(new Color(0,150,0));
        titlePanel.setOpaque(false);
        titleLabel = new JLabel("Queues Management Application");
        titleLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,30));
        titleLabel.setForeground(new Color(220, 174, 150));
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.add(titleLabel);


        //setupPanel
        setupPanelUp = new JPanel();
        setupPanelUp.setBackground(new Color(164, 147, 147));
        //setupPanelUp.setOpaque(false);
        setupPanelUp.setLayout(new GridLayout(2,1));

        setupPanelUpUpper = new JPanel();
        setupPanelUpUpper.setLayout(new GridLayout(1,4));
        setupPanelUp.setBorder(BorderFactory.createMatteBorder(40, 0, 0, 0, new Color(164, 147, 147)));
        setupPanelUpUpper.setOpaque(false);

        setupPanelUpUpperLeft = new JPanel();
        setupPanelUpUpperLeft.setOpaque(false);
        numberOfClientsLabel = new JLabel("Number of Clients:");
        numberOfClientsField = new JTextField("",3);
        setupPanelUpUpperLeft.add(numberOfClientsLabel);
        setupPanelUpUpperLeft.add(numberOfClientsField);

        setupPanelUpUpperRight = new JPanel();
        setupPanelUpUpperRight.setOpaque(false);
        numberOfServersLabel = new JLabel("Number of Queues:");
        numberOfServersField = new JTextField("",3);
        setupPanelUpUpperRight.add(numberOfServersLabel);
        setupPanelUpUpperRight.add(numberOfServersField);

        setupPanelUpUpper.add(setupPanelUpUpperLeft);
        setupPanelUpUpper.add(setupPanelUpUpperRight);

        setupPanelUpLower = new JPanel();
        setupPanelUpLower.setOpaque(false);
        setupPanelUpLower.setBorder(BorderFactory.createMatteBorder(10, 0, 0, 0, new Color(164, 147, 147)));
        timeLimitLabel = new JLabel("Simulation Time Limit:");
        timeLimitField = new JTextField("",3);
        setupPanelUpLower.add(timeLimitLabel);
        setupPanelUpLower.add(timeLimitField);

        setupPanelUp.add(setupPanelUpUpper);
        setupPanelUp.add(setupPanelUpLower);


        setupPanelDown = new JPanel();
        setupPanelDown.setBackground(new Color(164, 147, 147));
        setupPanelDown.setBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, new Color(164, 147, 147)));
        //setupPanelDown.setOpaque(false);
        setupPanelDown.setLayout(new GridBagLayout());

        minArrivalTimeLabel = new JLabel("Min. Arrival Time: ");
        minArrivalTimeField = new JTextField("",3);
        maxArrivalTimeLabel = new JLabel("   Max. Arrival Time: ");
        maxArrivalTimeField = new JTextField("",3);
        minProcessingTimeLabel = new JLabel("   Min. Service Time: ");
        minProcessingTimeField = new JTextField("",3);
        maxProcessingTimeLabel = new JLabel("   Max. Service Time: ");
        maxProcessingTimeField = new JTextField("",3);
        setupPanelDown.add(minArrivalTimeLabel);
        setupPanelDown.add(minArrivalTimeField);
        setupPanelDown.add(maxArrivalTimeLabel);
        setupPanelDown.add(maxArrivalTimeField);
        setupPanelDown.add(minProcessingTimeLabel);
        setupPanelDown.add(minProcessingTimeField);
        setupPanelDown.add(maxProcessingTimeLabel);
        setupPanelDown.add(maxProcessingTimeField);

        //buttonPanel
        buttonPanel = new JPanel();
        //buttonPanel.setBackground(new Color(0,0,100));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 0, 0, new Color(103, 89, 94)));
        buttonPanel.setOpaque(false);

        validateAndStartButton = new JButton("Validate");
        validateAndStartButton.setBackground(new Color(164, 147, 147));
        validateAndStartButton.setFocusable(false);
        validateAndStartButton.setPreferredSize(new Dimension(100,25));
        helpAndHaltButton = new JButton("Help");
        helpAndHaltButton.setBackground(new Color(164, 147, 147));
        helpAndHaltButton.setFocusable(false);
        helpAndHaltButton.setPreferredSize(new Dimension(100,25));

        buttonPanel.add(validateAndStartButton);
        buttonPanel.add(helpAndHaltButton);

        //messagePanel
        messagePanel = new JPanel();
        //messagePanel.setBackground(new Color(0,150,200));
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new GridBagLayout());
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));
        messageLabel.setForeground(new Color(220, 174, 150));
        messageLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 100, 0, new Color(103, 89, 94)));
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

    public void addButtonListener(ActionListener bl){
        validateAndStartButton.addActionListener(bl);
        helpAndHaltButton.addActionListener(bl);
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
        String HELP = "A project realised by broland29.";
        int input = JOptionPane.showOptionDialog(null, HELP, "Help", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, 0);
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

        String text = "";
        switch (textFieldName) {
            case NUMBER_OF_CLIENTS -> text = numberOfClientsField.getText();
            case NUMBER_OF_SERVERS -> text = numberOfServersField.getText();
            case TIME_LIMIT -> text = timeLimitField.getText();
            case MIN_ARRIVAL_TIME -> text = minArrivalTimeField.getText();
            case MAX_ARRIVAL_TIME -> text = maxArrivalTimeField.getText();
            case MIN_PROCESSING_TIME -> text = minProcessingTimeField.getText();
            case MAX_PROCESSING_TIME -> text = maxProcessingTimeField.getText();
        }

        int value;
        try{
            value = Integer.parseInt(text);
        }catch (NumberFormatException nfe){
            throw new NumberFormatException("Invalid " + textFieldName + ".");
        }

        if (value < 0){
            throw new NegativeNumberException("Negative " + textFieldName + " not allowed.");
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

    public static void main(String[] args) {
        new SetupFrame();
    }
}
