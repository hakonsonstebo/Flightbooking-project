package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassengerSeatTest {

    Flight flight;
    Flight shortTimeflight;
    Flight longTimeUntilFlight;
    LocalDate shortTimeDate;
    LocalDate longTimeUntilDate;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        flight = new Flight("New York", "Oslo", LocalDate.now(), false);
    }

    @Test
    public void testAbstractClassPasssengerSeatConstructor() {
        Assertions.assertDoesNotThrow(() -> {
            new FirstClassSeat("1A", flight);
        });

        Assertions.assertDoesNotThrow(() -> {
            new BusinessClassSeat("10D", flight);
        });

        Assertions.assertDoesNotThrow(() -> {
            new EconomyClassSeat("23C", flight);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FirstClassSeat("3SA", flight);
        }, "The planes only contains seat numbers up to F");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new BusinessClassSeat("13S", flight);
        }, "The planes only contains seat letters up to F");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new EconomyClassSeat("35A", flight);
        }, "The planes only contains seat numbers up to 30");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FirstClassSeat("0", flight);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new BusinessClassSeat("0", flight);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new EconomyClassSeat("0", flight);
        });
    }

    @Test
    public void testFirstClassSeatConstructor() {
        Assertions.assertDoesNotThrow(() -> {
            new FirstClassSeat("1A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new FirstClassSeat("4A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new FirstClassSeat("5A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new FirstClassSeat("4F", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new FirstClassSeat("2D", flight);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FirstClassSeat("-1A", flight);
        }, "Cannot set negative seat position");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FirstClassSeat("12A", flight);
        }, "Cannot set a seat position that does not belong to the seat class");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new FirstClassSeat(null, flight);
        }, "Cannot set negative seat position to 'Null'");
    }

    @Test
    public void testBusinessClassSeatConstructor() {
        Assertions.assertDoesNotThrow(() -> {
            new BusinessClassSeat("6A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new BusinessClassSeat("13A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new BusinessClassSeat("9A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new BusinessClassSeat("10F", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new BusinessClassSeat("15D", flight);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new BusinessClassSeat("-9A", flight);
        }, "Cannot set negative seat position");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new BusinessClassSeat("1A", flight);
        }, "Cannot set a seat position that does not belong to the seat class");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new BusinessClassSeat(null, flight);
        }, "Cannot set seat position to 'null'");
    }

    @Test
    public void testEconomyClassSeatConstructor() {
        Assertions.assertDoesNotThrow(() -> {
            new EconomyClassSeat("19A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new EconomyClassSeat("22A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new EconomyClassSeat("16A", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new EconomyClassSeat("30F", flight);
        });
        Assertions.assertDoesNotThrow(() -> {
            new EconomyClassSeat("25D", flight);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new EconomyClassSeat("-18A", flight);
        }, "Cannot set negative seat position");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new EconomyClassSeat("15F", flight);
        }, "Cannot set a seat position that does not belong to the seat class");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new EconomyClassSeat(null, flight);
        }, "Cannot set negative seat position to 'Null'");
    }

    @BeforeEach
    public void setUpPricesTests() throws FileNotFoundException {
        shortTimeDate = LocalDate.now().plusDays(10);
        longTimeUntilDate = LocalDate.now().plusDays(110);
        shortTimeflight = new Flight("Paris", "Oslo", shortTimeDate);
        longTimeUntilFlight = new Flight("Paris", "Oslo", longTimeUntilDate);
    }

    @Test
    public void testFirstClassSeatPrices() throws FileNotFoundException {
        PassengerSeat firstClassPassengerSeat = new FirstClassSeat("1A", shortTimeflight);
        firstClassPassengerSeat.setFlight(shortTimeflight);
        assertEquals(500 + 1 * 75, firstClassPassengerSeat.getPrice());
        firstClassPassengerSeat.setFlight(longTimeUntilFlight);
        assertEquals(500 + 1 * 75 - 50, firstClassPassengerSeat.getPrice());

        shortTimeflight = new Flight("London", "Canberra", shortTimeDate);
        longTimeUntilFlight = new Flight("London", "Canberra", longTimeUntilDate);
        firstClassPassengerSeat.setFlight(shortTimeflight);
        assertEquals(500 + 17 * 75, firstClassPassengerSeat.getPrice());
        firstClassPassengerSeat.setFlight(longTimeUntilFlight);
        assertEquals(500 + 17 * 75 - 50, firstClassPassengerSeat.getPrice());
    }

    @Test
    public void testBusinessClassSeatPrices() throws FileNotFoundException {
        PassengerSeat businessClassPassengerSeat = new BusinessClassSeat("6B", shortTimeflight);
        businessClassPassengerSeat.setFlight(shortTimeflight);
        assertEquals(250 + 1 * 50, businessClassPassengerSeat.getPrice());
        businessClassPassengerSeat.setFlight(longTimeUntilFlight);
        assertEquals(250 + 1 * 50 - 50, businessClassPassengerSeat.getPrice());

        shortTimeflight = new Flight("London", "Canberra", shortTimeDate);
        longTimeUntilFlight = new Flight("London", "Canberra", longTimeUntilDate);
        businessClassPassengerSeat.setFlight(shortTimeflight);
        assertEquals(250 + 17 * 50, businessClassPassengerSeat.getPrice());
        businessClassPassengerSeat.setFlight(longTimeUntilFlight);
        assertEquals(250 + 17 * 50 - 50, businessClassPassengerSeat.getPrice());
    }

    @Test
    public void testEconomyClassSeatPrices() throws FileNotFoundException {
        PassengerSeat economonyClassPassengerSeat = new EconomyClassSeat("21C", shortTimeflight);
        economonyClassPassengerSeat.setFlight(shortTimeflight);
        assertEquals(150 + 1 * 25, economonyClassPassengerSeat.getPrice());
        economonyClassPassengerSeat.setFlight(longTimeUntilFlight);
        assertEquals(150 + 1 * 25 - 50, economonyClassPassengerSeat.getPrice());

        shortTimeflight = new Flight("London", "Canberra", shortTimeDate);
        longTimeUntilFlight = new Flight("London", "Canberra", longTimeUntilDate);
        economonyClassPassengerSeat.setFlight(shortTimeflight);
        assertEquals(150 + 17 * 25, economonyClassPassengerSeat.getPrice());
        economonyClassPassengerSeat.setFlight(longTimeUntilFlight);
        assertEquals(150 + 17 * 25 - 50, economonyClassPassengerSeat.getPrice());
    }
}
