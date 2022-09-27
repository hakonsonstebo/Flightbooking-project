package OOPAirlines;

import java.io.FileNotFoundException;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class OOPAirlinesTicketController extends OOPAirlinesController {

    @FXML
    private TableView<FlightData> tableView;

    @FXML
    private Label nameLabel;

    @FXML
    private Button button;

    @FXML
    protected void initialize() {
        try {
            bookingSystem = new BookingSystem(passengersAndFlightsFilename);
            createTableView();
            this.passenger = loadPassenger();
            nameLabel.setText("Thank you " + passenger.getSecondName());
            updatePickedFlightTable();
        } catch (FileNotFoundException e) {
            displayErrorAlert("Something went wrong loading your ordered flights");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayErrorAlert(
                    "Error. The file " + passengersAndFlightsFilename
                            + ".txt" + " might have more than one empty line or the lines might be wrongly formatted");
        }
    }

    private void createTableView() {
        TableColumn<FlightData, String> fromColumn = new TableColumn<FlightData, String>("From");
        fromColumn.setMinWidth(35);
        fromColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("departure"));

        TableColumn<FlightData, String> toColumn = new TableColumn<FlightData, String>("To");
        toColumn.setMinWidth(35);
        toColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("destination"));

        TableColumn<FlightData, String> dateColumn = new TableColumn<FlightData, String>("Date");
        dateColumn.setMinWidth(35);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<FlightData, String> seatColumn = new TableColumn<FlightData, String>("Seat");
        seatColumn.setMinWidth(25);
        seatColumn.setCellValueFactory(new PropertyValueFactory<>("seatPosition"));

        tableView.getColumns().add(fromColumn);
        tableView.getColumns().add(toColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(seatColumn);

        tableView.setPlaceholder(new Label(
                "You have not ordered any flights"));
    }

    private void updatePickedFlightTable() {
        try {
            tableView.getItems().setAll(loadPickedFlights());
        } catch (FileNotFoundException e) {
            displayErrorAlert(e.getMessage());
        }
        // The user should not be able to interact with the tableView
        tableView.setSelectionModel(null);

    }

    private Passenger loadPassenger() throws FileNotFoundException {
        return fileHandler.readActivePassenger(passengersAndFlightsFilename, activePassengerFilename);
    }

    private List<FlightData> loadPickedFlights() throws FileNotFoundException {
        return bookingSystem.getAPassengersFlights(passenger);
    }

    @FXML
    private void exitButtonOnAction(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }
}
