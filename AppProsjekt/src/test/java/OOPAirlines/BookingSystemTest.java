package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingSystemTest {
    private Flight flight;
    private BookingSystem bookingSystem;
    private File passengersAndFlightsTestFile;
    private Passenger olaNordmann;
    private Passenger trulsNordmann;
    private Passenger arneNordmann;
    private Passenger olaHansen;
    private Passenger trulsGustavsen;

    private void createBookingSystem() throws FileNotFoundException {
        bookingSystem = new BookingSystem("passengersAndFlightsTestFile");
    }

    private void createFileForBookingSystem() throws IOException {
        String passengerAndFlightsLine1 = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                + "Canberra.Paris.2023-04-12.9F.1050;London.New York.2023-04-12.9F.500;" + "\n";
        String passengerAndFlightsLine2 = "Truls;Nordmann;98123456;truls32@gmail.com: " + "" + "\n";
        String passengerAndFlightsLine3 = "Arne;Nordmann;98443391;arne344@gmail.com: "
                + "London.Paris.2023-04-12.9F.200;" + "\n";
        String passengerAndFlightsLine4 = "Ola;Hansen;98440822;hansen@gmail.com: "
                + "New York.Oslo.2023-04-28.1F.900;Brasilia.Paris.2023-04-28.1F.1125;" + "\n";
        String passengerAndFlightsLine5 = "Truls;Gustavsen;98123456;trulsen@gmail.com: " + "" + "\n";

        String passengersAndFlightsContent = passengerAndFlightsLine1 + passengerAndFlightsLine2
                + passengerAndFlightsLine3 + passengerAndFlightsLine4 + passengerAndFlightsLine4
                + passengerAndFlightsLine5;
        passengersAndFlightsTestFile = new File(FileHandler.generateFilePath("passengersAndFlightsTestFile"));
        Files.write(passengersAndFlightsTestFile.toPath(), passengersAndFlightsContent.getBytes());
    }

    @BeforeEach
    public void setUp() throws IOException {
        olaNordmann = new Passenger("Ola", "Nordmann", "98448822", "nordmann34@gmail.com");
        trulsNordmann = new Passenger("Truls", "Nordmann", "98123456", "truls32@gmail.com");
        arneNordmann = new Passenger("Arne", "Nordmann", "98443391", "arne344@gmail.com");
        olaHansen = new Passenger("Ola", "Hansen", "98440822", "hansen@gmail.com");
        trulsGustavsen = new Passenger("Truls", "Gustavsen", "98123456", "trulsen@gmail.com");
        createFileForBookingSystem();
        //
    }

    @Test
    public void testConstructor() throws IOException {
        createBookingSystem();
        assertEquals("passengersAndFlightsTestFile", bookingSystem.getFilename());
        assertEquals(6, bookingSystem.getCities().size());

        FileHandler fileHandler = new FileHandler();
        FlightData flightData = new FlightData(new Flight("Oslo", "Paris", LocalDate.now()), trulsGustavsen, "4A",
                bookingSystem);
        List<FlightData> flightsOrdered = List
                .of(flightData);
        fileHandler.addOrderedFlights("passengersAndFlightsTestFile", trulsGustavsen, flightsOrdered);
        Map<Passenger, List<FlightData>> PassengersAndFlights = bookingSystem.getPassengersAndFlights();
        BookingSystem updatedBookingSystem = new BookingSystem("passengersAndFlightsTestFile");

        Map<Passenger, List<FlightData>> updatedPassengersAndFlights = updatedBookingSystem.getPassengersAndFlights();

        // Test that no new passenger is added in to the updated bookingSystem.
        assertEquals(PassengersAndFlights.size(), updatedPassengersAndFlights.size());

        // Test that flightdata is added to the
        // passenger by comparing the before and after Maps.
        assertFalse(PassengersAndFlights.get(trulsGustavsen).contains(flightData));
        assertTrue(updatedPassengersAndFlights.get(trulsGustavsen).contains(flightData));
    }

    @Test
    public void testGenerateFlight() throws FileNotFoundException {
        createBookingSystem();
        flight = new Flight("Oslo", "New York", LocalDate.of(2024, 2, 19), false);
        bookingSystem.generateFlight(flight, "17A", olaHansen);
        assertEquals(1, bookingSystem.getFlights().size());
        assertEquals("oslo", bookingSystem.getFlights().get(0).getDeparture().getCity());
        assertEquals("new york", bookingSystem.getFlights().get(0).getDestination().getCity());
    }

    @Test
    public void testGenerateAvailableFlights() throws FileNotFoundException {
        createBookingSystem();
        flight = new Flight("Oslo", "New York", LocalDate.now());
        bookingSystem.generateAvailableFlights(LocalDate.now(), "17A", olaHansen);

        // Check that cities does not get combinated with themself.
        int numberOfFlights = (bookingSystem.getCities().size() * bookingSystem.getCities().size())
                - bookingSystem.getCities().size();
        assertEquals(numberOfFlights,
                bookingSystem.getFlights().size());

        // Check that no flights is generated more than once
        assertEquals(bookingSystem.getFlights().size(), bookingSystem.getFlights().stream().distinct().toList().size());

        // Check that every date is the same
        for (Flight flight : bookingSystem.getFlights()) {
            assertEquals(LocalDate.now(), flight.getDepartureDate());
        }
    }

    @Test
    public void testNotGenerateAlreadyOrderedFlight() throws IOException {
        // Test that a flight that is already ordered does not get generated with
        // neither the generateFlight nor generateAvailableFlights methods.
        createBookingSystem();

        Flight flight = new Flight("Oslo", "New York", LocalDate.of(2024, 2, 19), false);
        String passengerAndFlightsLine = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                + "Oslo.New York.2024-02-19.9F.1050;" + "\n";

        Files.write(passengersAndFlightsTestFile.toPath(), passengerAndFlightsLine.getBytes());
        bookingSystem.updateBookingInfo();

        bookingSystem.generateFlight(flight, "9F", olaHansen);
        assertFalse(bookingSystem.getFlights().contains(flight));

        bookingSystem.generateAvailableFlights(LocalDate.of(2024, 2, 19), "9F", trulsGustavsen);
        assertFalse(bookingSystem.getFlights().contains(flight));
    }

    @Test
    public void testCheckSeatPosition() throws FileNotFoundException {
        // Test that the seat position typed is on the correct format when genereating
        // flights. Note that the same method is used in both generateAvailableFlights
        // and generateFlight to check the seat position.
        createBookingSystem();
        Flight flight = new Flight("Oslo", "New York", LocalDate.now());

        Assertions.assertDoesNotThrow(() -> {
            bookingSystem.generateFlight(flight, "4a", arneNordmann);
        });

        Assertions.assertDoesNotThrow(() -> {
            bookingSystem.generateFlight(flight, "29a", arneNordmann);
        });

        Assertions.assertDoesNotThrow(() -> {
            bookingSystem.generateFlight(flight, "9d", arneNordmann);
        });

        Assertions.assertDoesNotThrow(() -> {
            bookingSystem.generateFlight(flight, "4A", arneNordmann);
        });

        Assertions.assertDoesNotThrow(() -> {
            bookingSystem.generateFlight(flight, "29A", arneNordmann);
        });

        Assertions.assertDoesNotThrow(() -> {
            bookingSystem.generateFlight(flight, "9D", arneNordmann);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateFlight(flight, null, arneNordmann);
        }, "The seat position cannot be null in order to generate flights");

        // Test that seat number cannot be just numbers
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateAvailableFlights(LocalDate.now(), "19", arneNordmann);
        }, "The seat position must be both numbers and letters");

        // Test that seat number cannot be just letters
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateFlight(flight, "AA", trulsGustavsen);
        }, "The seat position must be both numbers and letters");

        // Test that the seat position must be two or three chars long
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateFlight(flight, "", olaHansen);
        }, "The seat position can only be two or three chars long");

        // Test that the seat position must be two or three chars long
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateAvailableFlights(LocalDate.now(), "1AA", trulsGustavsen);
        }, "The seat position can only be two or three chars long");

        // Test that the seat numbers must go from 1 to 30
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateFlight(flight, "31A", olaNordmann);
        }, "The seat position can only go from 1 to 30");

        // Test that the seat numbers must go from 1 to 30
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateAvailableFlights(LocalDate.now(), "0A", olaHansen);
        }, "The seat position can only go from 1 to 30");

        // Test that the seat numbers must go from 1 to 30
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateFlight(flight, "-1A", trulsGustavsen);
        }, "The seat position can only go from 1 to 30");

        // Test that must be on format "numbers from 1 to 9" + "Only capitilized letters
        // (A-F)"
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateFlight(flight, "1T", arneNordmann);
        }, "The seat letter must be between A and F");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.generateAvailableFlights(LocalDate.of(2026, 4, 28), "1A",
                    new Passenger("Harald", "Mykle", "90897622", "mykle@gmail.com"));
        }, "It is not possible to order a flight after 01.01.2026");
    }

    @Test
    public void testGetFlightsCount() throws FileNotFoundException {
        createBookingSystem();
        assertEquals(2, bookingSystem.getFlightCount(olaNordmann));
        assertEquals(0, bookingSystem.getFlightCount(trulsNordmann));
        assertEquals(1, bookingSystem.getFlightCount(arneNordmann));
        assertEquals(2, bookingSystem.getFlightCount(olaHansen));
        assertEquals(0, bookingSystem.getFlightCount(trulsGustavsen));
    }

    @Test
    public void testPassengerHasConflictingID() throws IOException {
        String passengerAndFlightsLine1 = "Ola;Nordmann;98448822;nordmann34@gmail.com: " + "\n";
        String passengerAndFlightsLine2 = "Truls;Nordmann;98123456;truls32@gmail.com: " + "\n";
        String passengerAndFlightsLine3 = "Arne;Nordmann;98443391;arne344@gmail.com: " + "\n";
        Files.write(passengersAndFlightsTestFile.toPath(), passengerAndFlightsLine1.getBytes());
        Files.write(passengersAndFlightsTestFile.toPath(), passengerAndFlightsLine2.getBytes());
        Files.write(passengersAndFlightsTestFile.toPath(), passengerAndFlightsLine3.getBytes());
        createBookingSystem();
        Passenger passengerValid1 = new Passenger("Vetle", "Jensen", "78636742", "vetle@gmail.com");
        Passenger passengerValid2 = new Passenger("Arne", "Nordmann", "98443391", "arne344@gmail.com");
        assertFalse(bookingSystem.passengerHasConflictingPassenegerID("passengersAndFlightsTestFile", passengerValid1));
        assertFalse(bookingSystem.passengerHasConflictingPassenegerID("passengersAndFlightsTestFile", passengerValid2));

        Passenger passengerInvalid1 = new Passenger("Petter", "Bonde", "98443391", "arne344@gmail.com");
        Passenger passengerInvalid2 = new Passenger("Petter", "Bonde", "87644533", "arne344@gmail.com");
        Passenger passengerInvalid3 = new Passenger("Petter", "Bonde", "98443391", "arne@gmail.com");

        assertTrue(
                (bookingSystem.passengerHasConflictingPassenegerID("passengersAndFlightsTestFile", passengerInvalid1)));
        assertTrue(
                (bookingSystem.passengerHasConflictingPassenegerID("passengersAndFlightsTestFile", passengerInvalid2)));
        assertTrue(
                (bookingSystem.passengerHasConflictingPassenegerID("passengersAndFlightsTestFile", passengerInvalid3)));
    }

    @Test
    public void testPassengerHaveOrderedFlight() throws FileNotFoundException {
        createBookingSystem();

        // The flights created here is based on the file created in the setUp method
        Flight flight1 = new Flight("New York", "Oslo", LocalDate.of(2023, 4, 28), false);
        Flight flight2 = new Flight("London", "Paris", LocalDate.of(2023, 4, 12), false);
        FlightData flightData1 = new FlightData(flight1, olaHansen, "1F", bookingSystem);
        FlightData flightData2 = new FlightData(flight2, arneNordmann, "9F", bookingSystem);

        assertTrue(bookingSystem.passengerHaveOrderedFlight(olaHansen, flightData1));
        assertTrue(bookingSystem.passengerHaveOrderedFlight(arneNordmann, flightData2));

    }

    @Test
    public void testGetPassengesFlights() throws FileNotFoundException {

        createBookingSystem();

        // The flights created here is based on the file created in the setUp method
        Flight flight1 = new Flight("New York", "Oslo", LocalDate.of(2023, 4, 28), false);
        Flight flight2 = new Flight("Brasilia", "Paris", LocalDate.of(2023, 4, 28), false);
        FlightData flightData1 = new FlightData(flight1, olaHansen, "1F", bookingSystem);
        FlightData flightData2 = new FlightData(flight2, olaHansen, "1F", bookingSystem);

        List<FlightData> orderedFlights = bookingSystem.getAPassengersFlights(olaHansen);

        assertEquals(2, orderedFlights.size());
        assertTrue(orderedFlights.contains(flightData1) && orderedFlights.contains(flightData2));

    }

    @Test
    public void testRemovePassengersFlight() throws FileNotFoundException, IllegalAccessException {

        createBookingSystem();

        // In the setUp method, olaHansen should have two flights booked
        List<FlightData> orderedFlights = bookingSystem.getAPassengersFlights(olaHansen);
        assertEquals(2, orderedFlights.size());

        // The flight created here is bbased on the file created in the setUp method
        Flight flight = new Flight("New York", "Oslo", LocalDate.of(2023, 4, 28), false);
        FlightData flightData1 = new FlightData(flight, olaHansen, "1F", bookingSystem);

        bookingSystem.removeAPassengersFlight(olaHansen, flightData1);

        List<FlightData> updatedOrderedFlights = bookingSystem.getAPassengersFlights(olaHansen);
        assertEquals(1, updatedOrderedFlights.size());
        assertFalse(updatedOrderedFlights.contains(flightData1));

    }

    @AfterAll
    public void tearDown() {
        passengersAndFlightsTestFile.delete();
    }
}
