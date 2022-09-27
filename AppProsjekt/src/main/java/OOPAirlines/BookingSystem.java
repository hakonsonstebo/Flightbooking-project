package OOPAirlines;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingSystem {

    private FileHandler fileHandler = new FileHandler();
    private List<Flight> flights = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private Map<Passenger, List<FlightData>> passengersAndFlights = new LinkedHashMap<>();
    private String filename;

    public BookingSystem(String filename) throws FileNotFoundException {
        setAndSortCities();
        this.filename = filename;
        updateBookingInfo();
    }

    public void generateAvailableFlights(LocalDate departureDate, String seatPosition, Passenger passenger)
            throws FileNotFoundException {

        if (departureDate == null) {
            throw new IllegalArgumentException("Please choose a date in order to search flights");
        }

        if (departureDate.isAfter(LocalDate.of(2026, 1, 1))) {
            throw new IllegalArgumentException("OOPAirlines does not provide flights after 01.01.2026");
        }

        List<List<City>> combinationsOfCities = generateCombinationsOfCities();
        List<Flight> generatedFlights = new ArrayList<>();

        for (int i = 0; i < combinationsOfCities.size(); i++) {
            String departure = combinationsOfCities.get(i).get(0).getCity();
            String destination = combinationsOfCities.get(i).get(1).getCity();
            Flight newFlight = new Flight(departure, destination,
                    departureDate);

            if (!someoneHaveOrderedFlight(new FlightData(newFlight, passenger, seatPosition, this))) {
                generatedFlights.add(newFlight);
            }
        }

        this.flights = generatedFlights;
    }

    public void generateFlight(Flight flight, String seatPosition, Passenger passenger) throws FileNotFoundException {
        if (!someoneHaveOrderedFlight(new FlightData(flight, passenger, seatPosition, this))) {
            this.flights = List.of(flight);
        } else {
            this.flights = new ArrayList<>();
        }
    }

    private boolean someoneHaveOrderedFlight(FlightData flightData) throws FileNotFoundException {
        for (Map.Entry<Passenger, List<FlightData>> passengerAndFlights : getPassengersAndFlights().entrySet()) {
            if (passengerAndFlights.getValue().contains(flightData)) {
                return true;
            }
        }
        return false;
    }

    public List<City> getCities() {
        List<City> returnCities = new ArrayList<>(cities);
        return returnCities;
    }

    public List<Flight> getFlights() {
        List<Flight> returnFlights = new ArrayList<>(flights);
        return returnFlights;
    }

    public Map<Passenger, List<FlightData>> getPassengersAndFlights() {
        Map<Passenger, List<FlightData>> returnPassengersAndFlights = new LinkedHashMap<>(passengersAndFlights);
        return returnPassengersAndFlights;
    }

    public boolean passengerHasDiscount(Passenger passenger) {
        return getFlightCount(passenger) >= 5;
    }

    public int getFlightCount(Passenger passenger) {
        if (passengerHaveRegistred(passenger)) {
            return getPassengersAndFlights().get(passenger).size();
        }
        return 0;
    }

    public boolean passengerHaveOrderedFlight(Passenger passenger, FlightData selectedFlight)
            throws IllegalArgumentException {
        if (selectedFlight == null) {
            throw new IllegalArgumentException("Please select a flight before doing this operation");
        }
        if (passengerHaveRegistred(passenger)) {
            if (getPassengersAndFlights().get(passenger).contains(selectedFlight)) {
                return true;
            }
        }
        return false;
    }

    public List<FlightData> getAPassengersFlights(Passenger passenger) {
        return getPassengersAndFlights().getOrDefault(passenger, new ArrayList<>());
    }

    public void removeAPassengersFlight(Passenger passenger, FlightData selectedRemoveFlight) {
        if (passengerHaveRegistred(passenger)) {
            passengersAndFlights.get(passenger).remove(selectedRemoveFlight);
        } else {
            throw new IllegalArgumentException("The passenger have not registred");
        }
    }

    public boolean passengerHaveRegistred(Passenger passenger) {
        if (getPassengersAndFlights().containsKey(passenger)) {
            return true;
        }
        return false;
    }

    public List<FlightData> generateAPassengersMyFlights(Passenger passenger)
            throws FileNotFoundException {
        this.updateBookingInfo();
        List<FlightData> aPassengersMyFlights = fileHandler.readAPassengersOrderedFlights(filename, passenger);
        passenger.setBookingHistory(aPassengersMyFlights);
        return aPassengersMyFlights;
    }

    public void updateBookingInfo() throws FileNotFoundException, ArrayIndexOutOfBoundsException {
        this.passengersAndFlights = fileHandler.readPassengersAndFlights(filename);
    }

    public boolean passengerHasConflictingPassenegerID(String filename, Passenger passenger)
            throws FileNotFoundException, IllegalStateException {

        for (Map.Entry<Passenger, List<FlightData>> passengerAndFlights : getPassengersAndFlights().entrySet()) {
            Passenger potensialConflictPassenger = passengerAndFlights.getKey();

            if (passenger.getNumber().equals(potensialConflictPassenger.getNumber())
                    || passenger.getEmail().equals(potensialConflictPassenger.getEmail())) {
                // If the passenger also have the same first name and second name it is allowed
                // to register since all the information matches.
                if (passenger.getFirstName().equals(potensialConflictPassenger.getFirstName())
                        && passenger.getSecondName().equals(potensialConflictPassenger.getSecondName())) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public String getFilename() {
        return filename;
    }

    private List<List<City>> generateCombinationsOfCities() {
        // Generates every combination of different locations
        List<List<City>> combinations = new ArrayList<>();

        for (int i = 0; i < this.cities.size(); i++) {
            for (int j = 0; j < this.cities.size(); j++) {
                if (!cities.get(i).equals(cities.get(j))) {
                    List<City> newCombination = new ArrayList<>();
                    newCombination.add(cities.get(i));
                    newCombination.add(cities.get(j));
                    combinations.add(newCombination);
                }
            }
        }
        return combinations;
    }

    private void setAndSortCities() throws FileNotFoundException {
        this.cities = fileHandler.readCities("cities");
        cities.sort(new CityNameComparator());
    }
}
