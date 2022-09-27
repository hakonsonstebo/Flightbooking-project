package OOPAirlines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

public class OOPAirlinesOrderController extends OOPAirlinesController {

    @FXML
    private ComboBox<String> comboBox = new ComboBox<>();
    @FXML
    private TableView<FlightData> tableView;
    @FXML
    private TableViewSelectionModel<FlightData> selectionModel, originalSelectionModel;
    @FXML
    private TextField fromField, toField, seatPositionField;
    @FXML
    private DatePicker dateField;
    @FXML
    private Button generateAllFlightsAction, generateFlights;
    @FXML
    private Button pickFlighButton, cancelFighButton;
    @FXML
    private Label discountStatusLabel, statsLabel;

    @FXML
    protected void initialize() {
        fromField.setFocusTraversable(false);
        toField.setFocusTraversable(false);
        seatPositionField.setFocusTraversable(false);
        dateField.setFocusTraversable(false);

        try {
            this.bookingSystem = new BookingSystem(passengersAndFlightsFilename);
            this.passenger = fileHandler.readActivePassenger(passengersAndFlightsFilename, activePassengerFilename);
            updateDiscountStatusLabel();
        } catch (FileNotFoundException e) {
            displayErrorAlert("Something went wrong loading your profile");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayErrorAlert(
                    "Error. The file " + passengersAndFlightsFilename
                            + ".txt" + " might have more than one empty line or the lines might be wrongly formatted");
        }
        updateStatsLabel();
        setComboBox();
        createTableView();
    }

    @FXML
    private void generateFlightsAction() {
        try {
            if (fromField.getText() == "" && toField.getText() == "") {
                generateAvailableFlights();
            } else {
                generateFlight();
            }
        } catch (FileNotFoundException e) {
            displayErrorAlert("Something went wrong finding available flights");
        } catch (IllegalArgumentException e) {
            displayErrorAlert(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            displayErrorAlert(
                    "Error. The file " + passengersAndFlightsFilename
                            + ".txt" + " might have empty lines or the lines might be wrongly formatted");
        }
    }

    @FXML
    private void removeFlightAction(ActionEvent event) {

        FlightData selectedRemoveFlight = tableView.getSelectionModel().getSelectedItem();
        try {
            if (bookingSystem.passengerHaveOrderedFlight(passenger, selectedRemoveFlight)) {
                removeConfirmation(selectedRemoveFlight);
                return;

            } else if (passenger.havePickedFlightToShoppingCart(selectedRemoveFlight)) {
                removeConfirmation(selectedRemoveFlight);
                return;

            } else {
                displayErrorAlert("You have not ordered this flight. Therefore you can not remove it from your order.");
            }
        } catch (IllegalArgumentException e) {
            displayErrorAlert(e.getMessage());
        }

    }

    @FXML
    private void pickFlightAction(ActionEvent event) {
        FlightData selectedFlight = tableView.getSelectionModel().getSelectedItem();
        try {
            if (bookingSystem.passengerHaveOrderedFlight(passenger, selectedFlight)) {
                displayErrorAlert("You have ordered this seat in an earlier session");
                return;
            }
            if (passenger.havePickedFlightToShoppingCart(selectedFlight)) {
                displayErrorAlert("You have already picked this flight");
                return;
            }
        } catch (IllegalArgumentException e) {
            // If no flights are selected
            displayErrorAlert(e.getMessage());
            return;
        }
        addToShoppingCartConfirmation(selectedFlight);
    }

    @FXML
    private void updateMyFlightsTable() {
        try {
            List<FlightData> myFlights = bookingSystem.generateAPassengersMyFlights(passenger);
            searchOrFilterFlights(myFlights);
        } catch (FileNotFoundException e) {
            displayErrorAlert("Something went wrong loading your ordered flights");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayErrorAlert(
                    "Error. The file " + passengersAndFlightsFilename
                            + ".txt" + " might have empty lines or the lines might be wrongly formatted");
        }
    }

    @FXML
    private void updateShoppingCart() {
        passenger.generateShoppingCartList();
        searchOrFilterFlights(new ArrayList<>());
    }

    private void generateAvailableFlights() throws FileNotFoundException, IllegalArgumentException {
        bookingSystem.generateAvailableFlights(dateField.getValue(), seatPositionField.getText(), passenger);
        updateFlightTable(bookingSystem);

    }

    private void generateFlight() throws FileNotFoundException, IllegalArgumentException {
        Flight flight = new Flight(fromField.getText().toLowerCase(), toField.getText().toLowerCase(),
                dateField.getValue());
        bookingSystem.generateFlight(flight, seatPositionField.getText(), passenger);
        updateFlightTable(bookingSystem);

    }

    private void updateFlightTable(BookingSystem bookingSystem) {
        try {
            passenger.generateFlightListeners(bookingSystem, seatPositionField.getText());
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Something went wrong loading your flights");
        }
        searchOrFilterFlights(new ArrayList<>());
    }

    @FXML
    private void displayInformationAction() {
        displayInformationAlert(
                "Please press 'Search', 'My flights' or 'Shopping cart' again in order to see the sorted or filtered flights");
    }

    private void updateDiscountStatusLabel() throws FileNotFoundException {
        String disccountStatus = "";
        if (bookingSystem.passengerHasDiscount(passenger)) {
            disccountStatus = "Discount: "
                    + "100(GBP) discount on first- and businessclass seats! (rows from 1-5 and 6-15)";
        }
        discountStatusLabel.setText(disccountStatus);
    }

    private void updateStatsLabel() {

        int totalFlightsOrdered = bookingSystem.getFlightCount(passenger);
        int flightsInShoppingCart = passenger.getNumberOfShoppingCartFlights();

        statsLabel.setText(
                "Booking history: " + totalFlightsOrdered + " Shopping cart: " + flightsInShoppingCart);
    }

    private void addToShoppingCartConfirmation(FlightData selectedFlight) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation of order");
        alert.setContentText("Are you sure you want to add this flight to shopping cart?");
        Optional<ButtonType> result = alert.showAndWait();

        try {
            if (result.get() == ButtonType.OK) {
                passenger.addToShoppingCartFlights(selectedFlight);
                updateStatsLabel();
                updateDiscountStatusLabel();
            }
        } catch (FileNotFoundException e) {
            displayErrorAlert("Something went wrong ordering flight");
        }

    }

    private void displayInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void removeConfirmation(FlightData selectedRemoveFlight) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation of delete order");
        if (isRemovingFromShoppingCart(selectedRemoveFlight)) {
            updateShoppingCart();
            alert.setContentText("Are you sure you want to remove this order from your shopping cart?");
        } else {
            alert.setContentText("Are you sure you want to remove this order from your already booked flights?");
        }

        Optional<ButtonType> result = alert.showAndWait();

        try {
            if (result.get() == ButtonType.OK) {
                fileHandler.removeOrderedFlight(passengersAndFlightsFilename, passenger, selectedRemoveFlight);

                bookingSystem.updateBookingInfo();

                tableView.setPlaceholder(new Label(
                        "No results"));

                if (isRemovingFromShoppingCart(selectedRemoveFlight)) {
                    passenger.removeFromShoppingCartFlights(selectedRemoveFlight);
                    updateShoppingCart();
                } else {
                    bookingSystem.removeAPassengersFlight(passenger, selectedRemoveFlight);
                    updateMyFlightsTable();
                }

                updateStatsLabel();
                updateDiscountStatusLabel();
            }

        } catch (IOException e) {
            displayErrorAlert("Unfortuanlly, something went wrong removing your flight");
        } catch (IllegalStateException e) {
            displayErrorAlert(e.getMessage());
        } catch (IllegalArgumentException e) {
            displayErrorAlert(e.getMessage());
        }

    }

    private void setComboBox() {
        comboBox.getItems().add("Default");
        comboBox.getItems().add("Sort on price (Ascending)");
        comboBox.getItems().add("Sort on price (Descending)");
        comboBox.getItems().add("Sort on flight time (Ascending)");
        comboBox.getItems().add("Sort on flight time (Descending)");
        comboBox.getItems().add("Filter on price under 500 (GBP)");
        comboBox.getItems().add("Filter on price under 750 (GBP)");
        comboBox.getItems().add("Filter on price above 750 (GBP)");
        comboBox.getItems().add("Filter on price above 1000 (GBP)");
    }

    private void createTableView() {
        TableColumn<FlightData, String> fromColumn = new TableColumn<FlightData, String>("From");
        fromColumn.setMinWidth(100);
        fromColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("departure"));
        TableColumn<FlightData, String> toColumn = new TableColumn<FlightData, String>("To");
        toColumn.setMinWidth(100);
        toColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("destination"));
        TableColumn<FlightData, String> dateColumn = new TableColumn<FlightData, String>("Date");
        dateColumn.setMinWidth(100);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<FlightData, String> timeColumn = new TableColumn<FlightData, String>("Time (h)");
        timeColumn.setMinWidth(100);
        timeColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("time"));
        TableColumn<FlightData, String> priceColumn = new TableColumn<FlightData, String>("Price (GBP)");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("price"));
        TableColumn<FlightData, String> seatPositionColumn = new TableColumn<FlightData, String>("Seat Position");
        seatPositionColumn.setMinWidth(100);
        seatPositionColumn.setCellValueFactory(new PropertyValueFactory<FlightData, String>("seatPosition"));
        tableView.getColumns().add(fromColumn);
        tableView.getColumns().add(toColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(timeColumn);
        tableView.getColumns().add(priceColumn);
        tableView.getColumns().add(seatPositionColumn);

        tableView.setPlaceholder(new Label(
                "No results"));
    }

    private boolean isRemovingFromShoppingCart(FlightData selectedRemoveFlight) {
        if (passenger.havePickedFlightToShoppingCart(selectedRemoveFlight)) {
            return true;
        }
        return false;
    }

    private void searchOrFilterFlights(List<FlightData> flightsToBeSortedOrFiltered) {
        if (comboBox.getValue() != null) {
            String choice = comboBox.getValue();
            if (choice.equals("Default")) {
                flightsToBeSortedOrFiltered = passenger.getFlightListeners();
            } else if (choice.equals("Sort on price (Ascending)")) {
                flightsToBeSortedOrFiltered = passenger.getSortedFlightListeners(true, 'P');
            } else if (choice.equals("Sort on price (Descending)")) {
                flightsToBeSortedOrFiltered = passenger.getSortedFlightListeners(false, 'P');
            } else if (choice.equals("Sort on flight time (Ascending)")) {
                flightsToBeSortedOrFiltered = passenger.getSortedFlightListeners(true, 'T');
            } else if (choice.equals("Sort on flight time (Descending)")) {
                flightsToBeSortedOrFiltered = passenger.getSortedFlightListeners(false, 'T');
            } else if (choice.equals("Filter on price under 500 (GBP)")) {
                flightsToBeSortedOrFiltered = passenger.getFilteredFlightListeners(500, 'U');
            } else if (choice.equals("Filter on price under 750 (GBP)")) {
                flightsToBeSortedOrFiltered = passenger.getFilteredFlightListeners(750, 'U');
            } else if (choice.equals("Filter on price above 750 (GBP)")) {
                flightsToBeSortedOrFiltered = passenger.getFilteredFlightListeners(750, 'A');
            } else if (choice.equals("Filter on price above 1000 (GBP)")) {
                flightsToBeSortedOrFiltered = passenger.getFilteredFlightListeners(1000, 'A');
            }
            tableView.getItems().setAll(flightsToBeSortedOrFiltered);
        } else {
            tableView.getItems().setAll(passenger.getFlightListeners());
        }
    }

    @FXML
    private void sendInfoToTicketAction(ActionEvent event) {
        boolean userWantsToOrder = false;

        if (passenger.getShoppingCartFlights().isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Confirmation of not adding any flights to your order");
            alert.setContentText("Are you sure you do not want to add any flights to your shopping cart?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            } else {
                userWantsToOrder = true;
            }
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Confirmation of not adding any flights to your order");
            alert.setContentText(
                    "Please confirm that you have added all the flights you wanted to order in the shopping cart");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                userWantsToOrder = true;
            } else {
                return;
            }
        }

        if (userWantsToOrder) {
            try {
                fileHandler.addOrderedFlights(passengersAndFlightsFilename, passenger,
                        passenger.getShoppingCartFlights());
                Stage tab = (Stage) ((Node) event.getSource()).getScene().getWindow();
                tab.setScene(new Scene(FXMLLoader.load(getClass().getResource("OOPAirlinesTicketApp.fxml"))));
                tab.setTitle("Ticket");
                tab.show();
            } catch (IOException e) {
                displayErrorAlert("Something went wrong loading your ordered flights");
            }
        }
    }
}