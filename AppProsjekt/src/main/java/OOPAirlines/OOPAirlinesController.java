package OOPAirlines;

import javafx.scene.control.Alert;

public abstract class OOPAirlinesController {
    protected Passenger passenger;
    protected BookingSystem bookingSystem;
    protected IFileHandler fileHandler = new FileHandler();
    protected final String passengersAndFlightsFilename = "passengersAndFlights";
    protected final String activePassengerFilename = "activePassenger";

    protected void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected abstract void initialize();

}
