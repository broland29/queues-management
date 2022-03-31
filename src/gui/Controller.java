package gui;

import business_logic.SimulationManager;
import business_logic.SimulationStatus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static business_logic.SimulationStatus.*;
import static gui.TextFieldNames.*;

public class Controller {
    private final SimulationManager simulationManager;
    private final SetupFrame setupFrame;

    private int numberOfClients;
    private int numberOfServers;
    private int timeLimit;
    private int minProcessingTime;
    private int maxProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;


    public Controller(SimulationManager simulationManager, SetupFrame setupFrame) {
        this.simulationManager = simulationManager;
        this.setupFrame = setupFrame;
        this.setupFrame.addButtonListener(new ButtonListener());
    }

    class ButtonListener implements ActionListener{

        private void validationRoutine(){

            //get integer values from gui
            try {
                numberOfClients = setupFrame.getText(NUMBER_OF_CLIENTS);
                numberOfServers = setupFrame.getText(NUMBER_OF_SERVERS);
                timeLimit = setupFrame.getText(TIME_LIMIT);
                minArrivalTime = setupFrame.getText(MIN_ARRIVAL_TIME);
                maxArrivalTime = setupFrame.getText(MAX_ARRIVAL_TIME);
                minProcessingTime = setupFrame.getText(MIN_PROCESSING_TIME);
                maxProcessingTime = setupFrame.getText(MAX_PROCESSING_TIME);
            }catch (NumberFormatException | NegativeNumberException ne){
                setupFrame.setMessage(ne.getMessage());
                return;
            }

            //further validate values
            if (numberOfClients > SimulationManager.MAX_NUMBER_OF_CLIENTS) {
                setupFrame.setMessage("Input " + NUMBER_OF_CLIENTS + " exceeds maximum allowed value.");
                return;
            }
            if (numberOfServers > SimulationManager.MAX_NUMBER_OF_SERVERS){
                setupFrame.setMessage("Input" + NUMBER_OF_SERVERS + " exceeds maximum allowed value.");
                return;
            }
            if (timeLimit > SimulationManager.MAX_TIME_LIMIT){
                setupFrame.setMessage("Input" + TIME_LIMIT + " exceeds maximum allowed value.");
                return;
            }
            if (minArrivalTime > maxArrivalTime){
                setupFrame.setMessage("(" + minArrivalTime + "," + maxArrivalTime + ") is not a valid interval.");
                return;
            }
            if (minProcessingTime > maxProcessingTime){
                setupFrame.setMessage("(" + minProcessingTime + "," + maxProcessingTime + ") is not a valid interval.");
                return;
            }

            //reached here <=> validations passed

            setupFrame.setMessage("Input validated. Press \"Start\" to begin.");
            setupFrame.setValidateAndStartButton("Start");
            setupFrame.setHelpAndHaltButton("Help");
            simulationManager.setSimulationStatus(SimulationStatus.WAITING_FOR_START);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            SimulationStatus status = simulationManager.getSimulationStatus();

            if (e.getSource() == setupFrame.getValidateAndStartButton()){

                switch (status) {
                    case WAITING_FOR_INFO ->      //not validated yet
                            validationRoutine();
                    case WAITING_FOR_START -> {     //start not started yet
                        simulationManager.setSimulationStatus(RUNNING);
                        setupFrame.setMessage("Simulation is running.");
                        setupFrame.setValidateAndStartButton("Halt");
                        setupFrame.setHelpAndHaltButton("Help");
                        simulationManager.startSimulation(numberOfClients, numberOfServers, timeLimit, minArrivalTime, maxArrivalTime, minProcessingTime, maxProcessingTime);
                    }
                    case RUNNING -> {               //pressed halt
                        simulationManager.setSimulationStatus(WAITING_FOR_INFO);
                        setupFrame.setMessage("Simulation halted.");
                        setupFrame.setValidateAndStartButton("Validate");
                        setupFrame.setHelpAndHaltButton("Help");
                    }
                }
            }

            if (e.getSource() == setupFrame.getHelpAndHaltButton()){
                setupFrame.openHelp();
            }
        }
    }
}
