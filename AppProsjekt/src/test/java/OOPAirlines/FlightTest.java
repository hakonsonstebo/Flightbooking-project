package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FlightTest {
    Flight flight;
    FileHandler fileHandler = new FileHandler();

    private void checkInvalidConstructor(String departure, String destination, LocalDate date) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Flight(departure, destination, date);
        });
    }

    private boolean isAnOkayApproximaton(double actualDistance, double estimatedDistance) {
        // OOPAirlines should be able to provide a 0.99 accuracy when it comes to
        // distances according to:
        // https://developers.google.com/public-data/docs/canonical/countries_csv
        double delta = actualDistance * 0.01;
        if (estimatedDistance > (actualDistance - delta) && estimatedDistance < (actualDistance + delta)) {
            return true;
        }
        return false;
    }

    @Test
    @DisplayName("Constructor")
    void testConstructor() throws FileNotFoundException {
        LocalDate date1 = LocalDate.now().minusDays(10);
        LocalDate date2 = LocalDate.now().plusDays(10);
        LocalDate date3 = LocalDate.now().plusDays(99999);
        // Invalid date
        checkInvalidConstructor("Paris", "London", date1);
        checkInvalidConstructor("Paris", "London", null);

        // Invalid locations
        checkInvalidConstructor("invalid", "location", date2);
        checkInvalidConstructor("invalid", "Paris", date2);
        checkInvalidConstructor("New York", "location", date2);
        checkInvalidConstructor("New York", "Oslo", date3);

    }

    @Test
    void testGeneratedSeats() throws FileNotFoundException {
        LocalDate date = LocalDate.now().plusDays(10);
        Flight flight = new Flight("Paris", "London", date);
        assertEquals(180, flight.getPassengerSeats().size());
        Collection<String> someSeatPositions = List.of("1A", "5F", "6A", "15F", "16A", "30F");
        assertTrue(flight.getPassengerSeats().keySet().containsAll(someSeatPositions));

    }

    @Test
    void testGetDistance() throws FileNotFoundException {
        LocalDate date = LocalDate.now().plusDays(10);

        // Should I make a private method?
        Flight flight1 = new Flight("Paris", "London", date);
        Flight flight1Reverse = new Flight("London", "Paris", date);

        Flight flight2 = new Flight("Canberra", "New York", date);
        Flight flight3 = new Flight("Paris", "Oslo", date);
        Flight flight4 = new Flight("Canberra", "Paris", date);
        Flight flight5 = new Flight("Oslo", "Brasilia", date);

        // Check that two flights with the same two cities have the same distance
        assertEquals(flight1.getDistance(), flight1Reverse.getDistance());

        assertTrue(isAnOkayApproximaton(343, flight1.getDistance()));
        assertTrue(isAnOkayApproximaton(16226, flight2.getDistance()));
        assertTrue(isAnOkayApproximaton(1342, flight3.getDistance()));
        assertTrue(isAnOkayApproximaton(16925, flight4.getDistance()));
        assertTrue(isAnOkayApproximaton(9861, flight5.getDistance()));
    }
}
