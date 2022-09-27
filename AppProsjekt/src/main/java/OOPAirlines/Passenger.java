package OOPAirlines;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Passenger {
    private String firstName;
    private String secondName;
    private String number;
    private String email;
    private List<FlightData> shoppingCartFlights = new ArrayList<>();
    private List<FlightData> flightListeners = new ArrayList<>();

    public Passenger(String firstName, String secondName, String number, String email) {
        if (!isValidName(firstName)) {
            throw new IllegalArgumentException("This is not a valid first name");
        }
        this.firstName = formatName(firstName.strip());

        if (!isValidName(secondName)) {
            throw new IllegalArgumentException("This is not a valid second name");
        }
        this.secondName = formatName(secondName.strip());

        if (!isValidNumber(number)) {
            throw new IllegalArgumentException("This is not a valid number");
        }
        this.number = number.strip();

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("This is not a valid email. An example could be 'name@gmail.com'");
        }
        this.email = email.toLowerCase().strip();
    }

    public boolean havePickedFlightToShoppingCart(FlightData selectedFlight) {
        if (shoppingCartFlights.contains(selectedFlight)) {
            return true;
        }
        return false;
    }

    public void addToShoppingCartFlights(FlightData selectedFlight) {
        if (!havePickedFlightToShoppingCart(selectedFlight)) {
            shoppingCartFlights.add(selectedFlight);
        }
    }

    public void removeFromShoppingCartFlights(FlightData selectedFlight) {
        if (havePickedFlightToShoppingCart(selectedFlight)) {
            shoppingCartFlights.remove(selectedFlight);
        }

    }

    public List<FlightData> getShoppingCartFlights() {
        List<FlightData> returnshoppingCartFlights = new ArrayList<>(shoppingCartFlights);
        return returnshoppingCartFlights;
    }

    public void generateShoppingCartList() {
        emptyFlightListeners();
        for (FlightData flightData : getShoppingCartFlights()) {
            addFlightListener(flightData);
        }
    }

    public void addFlightListener(FlightData flightData) {
        this.flightListeners.add(flightData);
    }

    public void removeFlightListener(FlightData flightData) {
        this.flightListeners.remove(flightData);
    }

    public void emptyFlightListeners() {
        this.flightListeners = new ArrayList<>();
    }

    public List<FlightData> getFlightListeners() {
        List<FlightData> returnflightListeners = new ArrayList<>(flightListeners);
        return returnflightListeners;
    }

    public List<FlightData> getSortedFlightListeners(boolean value, char option) {
        List<FlightData> returnSortedflightListeners = new ArrayList<>(flightListeners);
        if (option == 'P') {
            Collections.sort(returnSortedflightListeners, new FlightPriceComprator(value));
        } else if (option == 'T') {
            Collections.sort(returnSortedflightListeners, new FlightTimeComparator(value));
        }
        return returnSortedflightListeners;
    }

    public List<FlightData> getFilteredFlightListeners(int value, char option) {
        List<FlightData> returnFilteredFlightListeners = new ArrayList<>(flightListeners);
        if (option == 'U') {
            returnFilteredFlightListeners = returnFilteredFlightListeners.stream()
                    .filter(flight -> flight.getPriceInt() <= value).toList();
        } else if (option == 'A') {
            returnFilteredFlightListeners = returnFilteredFlightListeners.stream()
                    .filter(flight -> flight.getPriceInt() >= value).toList();
        }
        return returnFilteredFlightListeners;
    }

    public int getNumberOfShoppingCartFlights() {
        return shoppingCartFlights.size();
    }

    public void generateFlightListeners(BookingSystem bookingSystem, String seatPosition) throws FileNotFoundException {
        bookingSystem.updateBookingInfo();
        emptyFlightListeners();
        for (Flight flight : bookingSystem.getFlights()) {
            this.addFlightListener(new FlightData(flight, this,
                    seatPosition, bookingSystem));
        }
    }

    public void setBookingHistory(List<FlightData> orderedFlights)
            throws FileNotFoundException {
        emptyFlightListeners();
        for (FlightData flightData : orderedFlights) {
            addFlightListener(flightData);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    private boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        if (!name.strip().matches("[a-zA-Z]+")) {
            return false;
        }

        if (name.length() < 2) {
            return false;
        }

        return true;
    }

    private boolean isValidNumber(String number) {
        if (number == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(0047|47|\\+47)?[2-9]\\d{7}");
        Matcher matcher = pattern.matcher(number.strip());
        return matcher.matches();
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        // Regex from https://stackoverflow.com/questions/8204680/java-regex-email
        Pattern pattern = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-zA-Z]{2,})$");
        Matcher matcher = pattern.matcher(email.strip());
        return matcher.matches();
    }

    private String formatName(String name) {
        String returnName = "";
        returnName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return returnName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((secondName == null) ? 0 : secondName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Passenger other = (Passenger) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (secondName == null) {
            if (other.secondName != null)
                return false;
        } else if (!secondName.equals(other.secondName))
            return false;
        return true;
    }
}
