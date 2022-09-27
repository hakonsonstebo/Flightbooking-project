package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class FlightDataTest {

    private Flight flight;
    private Passenger passenger;
    private File passengersAndFlightsFile;
    BookingSystem bookingSystem;

    @BeforeEach
    public void setUp() throws IOException {

        flight = new Flight("New York", "Oslo", LocalDate.now());
        String passengerAndFlightsLine = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                + "Brasilia.New york.2023-04-28.1A.1025;Brasilia.London.2023-04-28.15B.700;Oslo.New york.2022-04-27.30A.300;New york.London.2022-04-29.1A.950;Paris.Oslo.2022-04-29.1A.575;Oslo.New york.2022-04-27.30A.300;Brasilia.Canberra.2022-04-21.1A.1450;Brasilia.Oslo.2022-04-27.3A.1150;"
                + "\n";
        passengersAndFlightsFile = new File(FileHandler.generateFilePath("passengersAndFlightsTestFile"));
        Files.write(passengersAndFlightsFile.toPath(), passengerAndFlightsLine.getBytes());
        bookingSystem = new BookingSystem("passengersAndFlightsTestFile");
    }

    @Test
    void testConstructors() throws FileNotFoundException {
        Assertions.assertDoesNotThrow(() -> {
            new FlightData(flight, passenger, "7E", bookingSystem);
        });

        Assertions.assertDoesNotThrow(() -> {
            new FlightData(flight, "17F");
        });
    }

    @Test
    void testDataIsSet() throws FileNotFoundException {
        Flight flight = new Flight("canberra", "oslo", LocalDate.now());
        Passenger passenger = new Passenger("Ola", "Nordmann", "89547374", "nordmann@gmail.com");
        FlightData flightData = new FlightData(flight, passenger, "6A", bookingSystem);

        assertEquals(flight, flightData.getFlight());
        assertEquals(passenger, flightData.getPassenger());
        assertEquals("6A", flightData.getSeatPosition());
        assertEquals("Canberra", flightData.getDeparture());
        assertEquals("Oslo", flightData.getDestination());
        assertEquals(LocalDate.now().toString(), flightData.getDate());
        assertEquals("20,0", flightData.getTime());
        assertEquals(Integer.toString(1050), flightData.getPrice());

    }

    @Test
    public void testDiscountActivation() throws IOException {

        Passenger olaNordmann = new Passenger("Ola", "Nordmann", "98448822", "nordmann34@gmail.com");

        Flight flight1 = new Flight("Brasilia", "New York", LocalDate.now());
        Flight flight2 = new Flight("Brasilia", "London", LocalDate.now());
        Flight flight3 = new Flight("Oslo", "New York", LocalDate.now());
        FlightData discountFlightFirstClass = new FlightData(flight1, olaNordmann, "2A", bookingSystem);
        FlightData discountFlightBusinessClass = new FlightData(flight2, olaNordmann, "11A", bookingSystem);
        FlightData discountFlightEconomyClass = new FlightData(flight3, olaNordmann, "30B", bookingSystem);

        // Cheks that olaNordmann has a discount
        assertTrue(bookingSystem.passengerHasDiscount(olaNordmann));

        // Checks that the price is reduced from 1025 to 925
        assertEquals(925, discountFlightFirstClass.getPriceInt());
        // Checks that the price is reduced from 700 to 600
        assertEquals(600, discountFlightBusinessClass.getPriceInt());
        // Checks that the price is still 3000
        assertEquals(300, discountFlightEconomyClass.getPriceInt());

    }

    @Test
    public void testHashCodeWorks() throws FileNotFoundException {
        FlightData flightData1 = new FlightData(flight, passenger, "18C", bookingSystem);
        FlightData flightData2 = new FlightData(flight, passenger, "18C", bookingSystem);

        assertTrue(flightData1.equals(flightData2));

        List<FlightData> flightDatas = new ArrayList<>();
        flightDatas.add(flightData1);
        assertTrue(flightDatas.contains(flightData2));
    }

    @AfterAll
    public void tearDown() {
        passengersAndFlightsFile.delete();
    }
}
