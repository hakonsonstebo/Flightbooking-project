package OOPAirlines;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Flight {

    private FileHandler fileHandler = new FileHandler();
    private City departure;
    private City destination;
    private LocalDate departureDate;
    private List<City> cities = new ArrayList<>();
    private Map<String, PassengerSeat> passengerSeats = new LinkedHashMap<>();

    public Flight(String departure, String destination, LocalDate departureDate)
            throws FileNotFoundException {
        setAndSortCities();
        checkCities(departure, destination);
        setDepartureAndDestination(departure, destination);
        checkDepartureDate(departureDate);
        this.departureDate = departureDate;
        setSeats();
    }

    public Flight(String departure, String destination, LocalDate departureDate, boolean checkDate)
            throws FileNotFoundException {
        // This constructor have an additional parameter "checkDate" to determine wheter
        // to check he date or not.
        // In some cases, for example when reading earlier booked flights, the date
        // should be allowed to be before the current date.
        setAndSortCities();
        checkCities(departure.strip(), destination.strip());
        setDepartureAndDestination(departure.strip(), destination.strip());
        if (checkDate) {
            checkDepartureDate(departureDate);
        }
        if (departureDate == null) {
            throw new IllegalArgumentException("Plase enter a date before searching");
        }
        this.departureDate = departureDate;
        setSeats();
    }

    public double getDistance() {
        // This methoded finds the shortest air distance between two coordinates and is
        // inspired
        // by a code provided on the following website:
        // https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/

        double latitudeDeparture = Math.toRadians(departure.getLatitude());
        double latitudeDestination = Math.toRadians(destination.getLatitude());
        double longitudeDeparture = Math.toRadians(departure.getLongitude());
        double longitudeDestination = Math.toRadians(destination.getLongitude());

        double deltaLatitude = latitudeDestination - latitudeDeparture;
        double deltaLongitude = longitudeDestination - longitudeDeparture;

        double a = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2) + Math.sin(deltaLongitude / 2)
                * Math.sin(deltaLongitude / 2) * Math.cos(latitudeDeparture) * Math.cos(latitudeDestination);
        double c = 2 * Math.asin(Math.sqrt(a));
        return c * 6371;
    }

    public double getTravelTime() {
        // Calculates the travel time of the flight assuming that OOPAirlines' planes
        // avergaes on 800 km/h

        double distance = getDistance();
        double speed = 800.0;
        double approximatedTime = distance / speed;
        return approximatedTime;
    }

    public Map<String, PassengerSeat> getPassengerSeats() {
        Map<String, PassengerSeat> returnPassengerSeats = new LinkedHashMap<>(passengerSeats);
        return returnPassengerSeats;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public City getDeparture() {
        return departure;
    }

    public City getDestination() {
        return destination;
    }

    private void setAndSortCities() throws FileNotFoundException {
        this.cities = fileHandler.readCities("cities");
        cities.sort(new CityNameComparator());
    }

    private void setDepartureAndDestination(String departure, String destination) {
        // Takes the string representation and finds the corrensponding city objects.
        for (City city : cities) {
            if (city.getCity().equals(departure.toLowerCase().strip())) {
                this.departure = city;
            }
            if (city.getCity().equals(destination.toLowerCase().strip())) {
                this.destination = city;
            }
        }
    }

    private void setSeats() {
        char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F' };

        for (int row = 1; row <= 30; row++) {
            for (char letter : letters) {
                if (row <= 5) {
                    // The first 5 rows are first class
                    String seatPosition = Integer.toString(row) + Character.toString(letter);
                    PassengerSeat seat = new FirstClassSeat(seatPosition, this);
                    passengerSeats.put(seatPosition, seat);

                } else if (row <= 15) {
                    // Row 6 through 15 are business class
                    String seatPosition = Integer.toString(row) + Character.toString(letter);
                    PassengerSeat seat = new BusinessClassSeat(seatPosition, this);
                    passengerSeats.put(seatPosition, seat);

                } else {
                    // The last 50 rows are economy class
                    String seatPosition = Integer.toString(row) + Character.toString(letter);
                    PassengerSeat seat = new EconomyClassSeat(seatPosition, this);
                    passengerSeats.put(seatPosition, seat);
                }
            }
        }
    }

    private void checkCities(String departure, String destination) {
        if (departure == null || destination == null) {
            throw new IllegalArgumentException("Flight must have cities to fly between");
        }

        if (departure.strip().equals(destination.strip())) {
            throw new IllegalArgumentException("OOPAirlines only provides flights between different cities");
        }

        List<String> citiesName = cities.stream().map(c -> c.getCity()).toList();
        if (!citiesName.contains(departure.strip().toLowerCase())
                || !citiesName.contains(destination.strip().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Unfortunally, OOPAirlines cannot provide a flight between these two cities.");
        }
    }

    private void checkDepartureDate(LocalDate departureDate) {
        // A flight shall always know when it's departure time
        if (departureDate == null) {
            throw new IllegalArgumentException("Plase enter a date before searching");
        }

        if (departureDate.isAfter(LocalDate.of(2026, 1, 1))) {
            throw new IllegalArgumentException("OOPAirlines does not provide flights after 01.01.2026");
        }

        // It should not be possible to order a flight back in time
        LocalDate today = LocalDate.now();
        if (departureDate.isBefore(today)) {
            throw new IllegalArgumentException("It is not possible to order a flight that flies before today");
        }
    }
}
