package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PassengerTest {
    FileHandler fileHandler = new FileHandler();
    private File passengersAndFlightsFile;
    private BookingSystem bookingSystem;

    Passenger passenger;
    private FlightData listener1;
    private FlightData listener2;
    private FlightData listener3;

    private FlightData selectedFlight1;
    private FlightData selectedFlight2;
    private FlightData selectedFlight3;
    private FlightData selectedFlight4;
    Flight flight;

    @BeforeEach
    public void setUp() throws IOException {
        String passengerAndFlightsLine = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                + "Brasilia.New york.2023-04-28.1A.1025;Brasilia.London.2023-04-28.15B.700;Oslo.New york.2022-04-27.30A.300;New york.London.2022-04-29.1A.950;Paris.Oslo.2022-04-29.1A.575;Oslo.New york.2022-04-27.30A.300;Brasilia.Canberra.2022-04-21.1A.1450;Brasilia.Oslo.2022-04-27.3A.1150;"
                + "\n";
        passengersAndFlightsFile = new File(FileHandler.generateFilePath("passengersAndFlightsTestFile"));
        Files.write(passengersAndFlightsFile.toPath(), passengerAndFlightsLine.getBytes());
        bookingSystem = new BookingSystem("passengersAndFlightsTestFile");

        passenger = new Passenger("Ola", "Nordmann", "90323232", "ola.nordmann@gmail.com");
        listener1 = new FlightData(new Flight("Canberra", "Brasilia", LocalDate.now(), false), passenger, "7A",
                bookingSystem);
        listener2 = new FlightData(new Flight("New York", "London", LocalDate.now(), false), passenger, "7A",
                bookingSystem);
        listener3 = new FlightData(new Flight("Brasilia", "Paris", LocalDate.now(), false), passenger, "7A",
                bookingSystem);
        passenger.addFlightListener(listener1);
        passenger.addFlightListener(listener2);
        passenger.addFlightListener(listener3);

        flight = new Flight("Paris", "New York", LocalDate.now());
        selectedFlight1 = new FlightData(flight, passenger, "1A", bookingSystem);
        selectedFlight2 = new FlightData(flight, passenger, "2B", bookingSystem);
        selectedFlight3 = new FlightData(flight, passenger, "3C", bookingSystem);
        selectedFlight4 = new FlightData(flight, passenger, "4D", bookingSystem);

        passenger.addToShoppingCartFlights(selectedFlight1);
        passenger.addToShoppingCartFlights(selectedFlight2);
        passenger.addToShoppingCartFlights(selectedFlight3);
        passenger.addToShoppingCartFlights(selectedFlight4);
    }

    private void checkInvalidConstructor(String firstName, String secondName, String number, String email) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Passenger(firstName, secondName, number, email);
        });
    }

    @Test
    void testConstructor() {

        Assertions.assertDoesNotThrow(() -> {
            new Passenger("Jesper", "Amundsen", "95774488", "jesper@gmail.com");
        });
        Assertions.assertDoesNotThrow(() -> {
            new Passenger("Ola", "Shi", "95774488", "jesper@mail.no");
        });
        Assertions.assertDoesNotThrow(() -> {
            new Passenger("JESPER", "ILL", "99994444", "kaktus@wiwiw.com");
        });

        checkInvalidConstructor("J", "Amundsen", "95774488", "jesper@gmail.com");

        checkInvalidConstructor("Jesper", "A", "95774488", "jesper@gmail.com");

        checkInvalidConstructor("", "", "95774488", "jesper@gmail.com");

        checkInvalidConstructor("Jesper", "Amundsen", "953", "jesper@gmail.com");

        checkInvalidConstructor("Jesper", "Amundsen", "", "jesper@gmail.com");

        checkInvalidConstructor("Jesper", "Amundsen", "95409061", "@gmail.com");

        checkInvalidConstructor("Jesper", "Amundsen", "95409061", "jesper@.com");

        checkInvalidConstructor("Jesper", "Amundsen", "95409061", "jesper@gmail");

        checkInvalidConstructor(null, "Amundsen", "95774488", "jesper@gmail.com");
        checkInvalidConstructor("Jesper", null, "95774488", "jesper@gmail.com");
        checkInvalidConstructor("Jesper", "Amundsen", null, "jesper@gmail.com");
        checkInvalidConstructor("Jesper", "Amundsen", "95774488", null);

    }

    @Test
    void testAddFlightListener() throws FileNotFoundException {
        assertEquals(3, passenger.getFlightListeners().size());

    }

    @Test
    void testRemoveFlightListener() throws FileNotFoundException {
        passenger.removeFlightListener(listener1);
        passenger.removeFlightListener(listener2);
        assertEquals(1, passenger.getFlightListeners().size());
        assertFalse(passenger.getFlightListeners().contains(listener1));
        assertFalse(passenger.getFlightListeners().contains(listener2));

    }

    @Test
    void testEmptyFlightListeners() throws FileNotFoundException {
        passenger.emptyFlightListeners();
        assertEquals(0, passenger.getFlightListeners().size());

    }

    @Test
    void testSortListeners() {
        List<FlightData> flightsSortedInPriceDescendingOrder = List.of(listener1, listener3, listener2);
        List<FlightData> flightsSortedInPriceAscendingOrder = List.of(listener2, listener3, listener1);
        List<FlightData> flightsSortedInTimeDescendingOrder = List.of(listener1, listener3, listener2);
        List<FlightData> flightsSortedInTimeAscendingOrder = List.of(listener2, listener3, listener1);

        List<FlightData> sortedListeners = new ArrayList<>();

        // check price descending order
        sortedListeners = passenger.getSortedFlightListeners(false, 'P');
        assertEquals(flightsSortedInPriceDescendingOrder, sortedListeners);

        // check price ascending order
        sortedListeners = passenger.getSortedFlightListeners(true, 'P');
        assertEquals(flightsSortedInPriceAscendingOrder, sortedListeners);

        // check time descending order
        sortedListeners = passenger.getSortedFlightListeners(false, 'T');
        assertEquals(flightsSortedInTimeDescendingOrder, sortedListeners);

        // check time ascending order
        sortedListeners = passenger.getSortedFlightListeners(true, 'T');
        assertEquals(flightsSortedInTimeAscendingOrder, sortedListeners);
    }

    @Test
    void testFilterListeners() throws FileNotFoundException {
        FlightData listener4 = new FlightData(new Flight("Oslo", "London", LocalDate.now().plusDays(3), false),
                passenger, "7A", bookingSystem);
        FlightData listener5 = new FlightData(new Flight("London", "Canberra", LocalDate.now().plusDays(3), false),
                passenger, "7A", bookingSystem);

        passenger.addFlightListener(listener4);
        passenger.addFlightListener(listener5);

        List<FlightData> filteredListeners = new ArrayList<>();

        filteredListeners = passenger.getFilteredFlightListeners(500, 'U');
        assertEquals(1, filteredListeners.size());
        assertFalse(filteredListeners.stream().map(p -> p.getPriceInt()).anyMatch(q -> q >= 500));

        filteredListeners = passenger.getFilteredFlightListeners(750, 'U');
        assertTrue(filteredListeners.size() == 3);
        assertFalse(filteredListeners.stream().map(p -> p.getPriceInt()).anyMatch(q -> q >= 750));

        filteredListeners = passenger.getFilteredFlightListeners(750, 'A');
        assertTrue(filteredListeners.size() == 2);
        assertFalse(filteredListeners.stream().map(p -> p.getPriceInt()).anyMatch(q -> q <= 750));

        filteredListeners = passenger.getFilteredFlightListeners(1000, 'A');
        assertEquals(1, filteredListeners.size());
        assertFalse(filteredListeners.stream().map(p -> p.getPriceInt()).anyMatch(q -> q <= 1000));

    }

    @Test
    void testAddFlightToSoppingCart() throws FileNotFoundException {
        assertEquals(passenger.getNumberOfShoppingCartFlights(), 4);
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight1));
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight2));
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight3));
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight4));

    }

    @Test
    void testRemoveFlightToSoppingCart() throws FileNotFoundException {
        assertEquals(4, passenger.getNumberOfShoppingCartFlights());

        passenger.removeFromShoppingCartFlights(selectedFlight1);
        passenger.removeFromShoppingCartFlights(selectedFlight3);

        assertEquals(2, passenger.getNumberOfShoppingCartFlights());

        assertFalse(passenger.havePickedFlightToShoppingCart(selectedFlight1));
        assertFalse(passenger.havePickedFlightToShoppingCart(selectedFlight3));

        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight2));
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight4));
    }

    @Test
    void testCheckIfPassengerHaveOrderedFlight() throws FileNotFoundException {

        FlightData selectedFlight5 = new FlightData(flight, passenger, "19A", bookingSystem);
        FlightData selectedFlight6 = new FlightData(flight, passenger, "19A", bookingSystem);

        passenger.addToShoppingCartFlights(selectedFlight5);

        assertTrue(selectedFlight5.equals(selectedFlight6));
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight5));
        assertTrue(passenger.havePickedFlightToShoppingCart(selectedFlight6));

        assertEquals(5, passenger.getNumberOfShoppingCartFlights());
        passenger.addToShoppingCartFlights(selectedFlight6);
        assertEquals(5, passenger.getNumberOfShoppingCartFlights());
    }

    @AfterAll
    public void tearDown() {
        passengersAndFlightsFile.delete();
    }

}
