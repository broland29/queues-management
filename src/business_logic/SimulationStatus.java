package business_logic;

public enum SimulationStatus {
    WAITING_FOR_INFO,   //user did not enter data/ the data was not valid
    WAITING_FOR_START,  //data is validated, is correct, waiting for user to press start
    RUNNING,            //simulation is running. after finishing, will get back to WAITING_FOR_INFO
}
