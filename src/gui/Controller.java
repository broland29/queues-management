package gui;

import business_logic.SimulationManager;
import business_logic.SimulationStatus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static business_logic.SimulationStatus.*;
import static gui.TextFieldNames.*;

public class Controller {
    private SimulationManager simulationManager;
    private SetupFrame frame;

    private int clientCount;
    private int queueCount;
    private int maxTime;
    private int arrivalIntervalFrom ;
    private int arrivalIntervalTo;
    private int serviceIntervalFrom;
    private int serviceIntervalTo;

    public Controller(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
        frame = simulationManager.getFrame();

        //fill with impossible values - is it needed?
        clientCount = -1;
        queueCount = -1;
        maxTime = -1;
        arrivalIntervalFrom = -1;
        arrivalIntervalTo = -1;
        serviceIntervalFrom = -1;
        serviceIntervalTo = -1;

        frame.addButtonListener(new ButtonListener());
    }

    class ButtonListener implements ActionListener{

        void validationRoutine(){
            boolean valid = true;

            //get integer values from gui

            try {
                clientCount = frame.getText(CLIENT_COUNT);
                queueCount = frame.getText(QUEUE_COUNT);
                maxTime = frame.getText(MAX_TIME);
                arrivalIntervalFrom = frame.getText(ARRIVAL_INTERVAL_FROM);
                arrivalIntervalTo = frame.getText(ARRIVAL_INTERVAL_TO);
                serviceIntervalFrom = frame.getText(SERVICE_INTERVAL_FROM);
                serviceIntervalTo = frame.getText(SERVICE_INTERVAL_TO);
            }catch (NumberFormatException | NegativeNumberException ne){
                valid = false;
                frame.setMessage(ne.getMessage());
            }

            //further validate values
            if (clientCount > SimulationManager.MAX_NUMBER_OF_CLIENTS) {
                valid = false;
                frame.setMessage(CLIENT_COUNT + " exceeds maximum allowed value.");
            }
            if (queueCount > SimulationManager.MAX_NUMBER_OF_SERVERS){
                valid = false;
                frame.setMessage(QUEUE_COUNT + " exceeds maximum allowed value.");
            }
            if (maxTime > SimulationManager.MAX_TIME_LIMIT){
                valid = false;
                frame.setMessage(MAX_TIME + " exceeds maximum allowed value.");
            }
            if (arrivalIntervalFrom > arrivalIntervalTo){
                valid = false;
                frame.setMessage("(" + arrivalIntervalFrom + "," + arrivalIntervalTo + ") is not a valid interval.");
            }
            if (serviceIntervalFrom > serviceIntervalTo){
                valid = false;
                frame.setMessage("(" + serviceIntervalFrom + "," + serviceIntervalTo + ") is not a valid interval.");
            }

            //validations passed
            if (valid){
                frame.setMessage("Input validated. Press \"Start\" to begin.");
                frame.setValidateAndStartButton("Start");
                frame.setHelpAndHaltButton("Cancel");
                simulationManager.setSimulationStatus(SimulationStatus.WAITING_FOR_START);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            SimulationStatus status = simulationManager.getSimulationStatus();

            if (e.getSource() == frame.getValidateAndStartButton()){

                switch (status){
                    case WAITING_FOR_INFO:      //not validated yet
                        validationRoutine();
                        break;
                    case WAITING_FOR_START:     //start not started yet
                        simulationManager.setSimulationStatus(RUNNING);
                        frame.setMessage("Simulation is running.");
                        frame.setValidateAndStartButton("Pause");
                        frame.setHelpAndHaltButton("Help");
                        SimulationManager.startSimulation(clientCount,queueCount,maxTime,arrivalIntervalFrom,arrivalIntervalTo,serviceIntervalFrom,serviceIntervalTo);
                        break;
                    case PAUSED:                //pressed resume
                        simulationManager.setSimulationStatus(RUNNING);
                        frame.setMessage("Simulation is running.");
                        frame.setValidateAndStartButton("Pause");
                        frame.setHelpAndHaltButton("Help");
                        break;
                    case RUNNING:               //pressed pause
                        simulationManager.setSimulationStatus(SimulationStatus.PAUSED);
                        frame.setMessage("Simulation paused.");
                        frame.setValidateAndStartButton("Resume");
                        frame.setHelpAndHaltButton("Halt");
                        break;
                }
            }

            else if (e.getSource() == frame.getHelpAndHaltButton()){
                if (status == PAUSED){          //pressed halt
                    frame.setMessage("Simulation halted.");
                    frame.setValidateAndStartButton("Start");
                    frame.setHelpAndHaltButton("Help");
                    simulationManager.setSimulationStatus(WAITING_FOR_INFO);
                }
                if (status == WAITING_FOR_START){
                    frame.setMessage("Simulation canceled.");
                    frame.setValidateAndStartButton("Start");
                    frame.setHelpAndHaltButton("Help");
                    simulationManager.setSimulationStatus(WAITING_FOR_INFO);
                }
                else if (status != PAUSED){
                    frame.openHelp();
                }
            }
        }
    }

}
