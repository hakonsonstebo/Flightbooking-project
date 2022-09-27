package OOPAirlines;

import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OOPAirlinesRegisterController extends OOPAirlinesController {

    @FXML
    private TextField firstNameField, secondNameField, numberField, emailField;

    @FXML
    protected void initialize() {
        firstNameField.setFocusTraversable(false);
        secondNameField.setFocusTraversable(false);
        numberField.setFocusTraversable(false);
        emailField.setFocusTraversable(false);
    }

    @FXML
    private void sendInfoToOrderAction(ActionEvent event) {
        try {

            bookingSystem = new BookingSystem(passengersAndFlightsFilename);
            passenger = new Passenger(firstNameField.getText(), secondNameField.getText(), numberField.getText(),
                    emailField.getText());

            if (bookingSystem.passengerHasConflictingPassenegerID(passengersAndFlightsFilename, passenger)) {
                displayErrorAlert("Someone have already registered this number or mail");
                return;
            }

            fileHandler.savePassenger(passengersAndFlightsFilename, activePassengerFilename, passenger);
            Stage tab = (Stage) ((Node) event.getSource()).getScene().getWindow();
            tab.setScene(new Scene(FXMLLoader.load(getClass().getResource("OOPAirlinesOrderApp.fxml"))));
            tab.setTitle("Order");
            tab.show();

        } catch (IllegalArgumentException e) {
            displayErrorAlert(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            displayErrorAlert(e.getMessage());
        } catch (FileNotFoundException e) {
            displayErrorAlert("Something went wrong loading your information");
        } catch (IOException e) {
            displayErrorAlert("Something went wrong");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayErrorAlert(
                    "Error. The file " + passengersAndFlightsFilename
                            + ".txt" + " might have more than one empty line or the lines might be wrongly formatted");
        }

    }

}
