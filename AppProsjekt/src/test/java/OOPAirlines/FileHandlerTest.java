package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileHandlerTest {
        // The constructor in FileHandler does not do anything else than creating an
        // object. Therefore it is not
        // tested.

        private IFileHandler fileHandler;
        private Passenger passenger;
        private BookingSystem bookingSystem;
        private File passengersAndFlightsCorrectFile;
        private File activePassengerCorrectFile;
        private File citiesCorrectFile;
        private Path passengersAndFlightsPath;
        private Path activePassengerPath;

        private IFileHandler getFileHandler() {
                return new FileHandler();
        }

        private void createBookingSystem() throws FileNotFoundException {
                bookingSystem = new BookingSystem("new_passengersAndFlights");
        }

        @BeforeAll
        public void setUp() throws IOException {
                fileHandler = getFileHandler();
                passenger = new Passenger("Ola", "Nordmann", "98449343", "nordmann34@gmail.com");
                passengersAndFlightsCorrectFile = new File(
                                FileHandler.generateFilePath("passengersAndFlightsCorrectFile"));
                activePassengerCorrectFile = new File(FileHandler.generateFilePath("activePassengerCorrectFile"));
                citiesCorrectFile = new File(FileHandler.generateFilePath("CitiesCorrectFile"));
        }

        @Test
        public void testSavePassenger() throws IOException {

                activePassengerPath = Path.of(FileHandler.generateFilePath("new_activePassenger"));
                passengersAndFlightsPath = Path.of(FileHandler.generateFilePath("new_passengersAndFlights"));
                Passenger olaNordmann = new Passenger("Ola", "Nordmann", "98449343", "nordmann34@gmail.com");
                fileHandler.savePassenger("new_passengersAndFlights", "new_activePassenger", olaNordmann);

                String actualActivePassengerContent = Files.readString(activePassengerPath);
                String actualPassengerAndFlightsContent = Files.readString(passengersAndFlightsPath);

                String correctActivePassengerContent = "Ola;Nordmann;98449343;nordmann34@gmail.com" + "\n";
                String correctPassengerAndFlightsContent = "Ola;Nordmann;98449343;nordmann34@gmail.com: " + "\n";
                assertEquals(correctActivePassengerContent, actualActivePassengerContent);
                assertEquals(correctPassengerAndFlightsContent, actualPassengerAndFlightsContent);

                fileHandler.savePassenger("new_passengersAndFlights", "new_activePassenger", olaNordmann);
                assertEquals(correctPassengerAndFlightsContent, actualPassengerAndFlightsContent,
                                "When the same passenger registeres the file should not update.");
        }

        @Test
        public void testReadActivePassenger() throws IOException {
                Assertions.assertThrows(FileNotFoundException.class, () -> {
                        fileHandler.readActivePassenger("inavlid", "filename");
                });

                String correctActivePassengerContent = "Ola;Nordmann;98449343;nordmann34@gmail.com" + "\n";
                activePassengerPath = Path.of(FileHandler.generateFilePath("new_activePassenger"));
                Files.write(activePassengerCorrectFile.toPath(), correctActivePassengerContent.getBytes());
                Passenger expectedPassenger = new Passenger("Ola", "Nordmann", "98449343", "nordmann34@gmail.com");
                Passenger Passenger = fileHandler.readActivePassenger("new_passengersAndFlights",
                                "new_activePassenger");

                assertTrue(expectedPassenger.equals(Passenger));
        }

        @Test
        public void testReadPassengersAndFlights() throws IOException {
                Assertions.assertThrows(FileNotFoundException.class, () -> {
                        fileHandler.readPassengersAndFlights("invalid");
                });

                Passenger olaNordmann = new Passenger("Ola", "Nordmann", "98448822", "nordmann34@gmail.com");
                Passenger trulsNordmann = new Passenger("Truls", "Nordmann", "98123456", "truls32@gmail.com");
                Passenger arneNordmann = new Passenger("Arne", "Nordmann", "98443391", "arne344@gmail.com");
                Passenger olaHansen = new Passenger("Ola", "Hansen", "98440822", "hansen@gmail.com");
                Passenger trulsGustavsen = new Passenger("Truls", "Gustavsen", "98123456", "trulsen@gmail.com");

                String passengerAndFlightsLine1 = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                                + "Canberra.Paris.2022-04-12.9F.1050;London.New York.2022-04-12.9F.500;" + "\n";
                String passengerAndFlightsLine2 = "Truls;Nordmann;98123456;truls32@gmail.com: " + "\n";
                String passengerAndFlightsLine3 = "Arne;Nordmann;98443391;arne344@gmail.com: "
                                + "London.Paris.2022-04-12.9F.200;" + "\n";
                String passengerAndFlightsLine4 = "Ola;Hansen;98440822;hansen@gmail.com: "
                                + "New York.Oslo.2023-04-28.1F.900;Brasilia.Paris.2023-04-28.1F.1125;" + "\n";
                String passengerAndFlightsLine5 = "Truls;Gustavsen;98123456;trulsen@gmail.com: " + "" + "\n";
                String passengersAndFlightsContent = passengerAndFlightsLine1 + passengerAndFlightsLine2
                                + passengerAndFlightsLine3 + passengerAndFlightsLine4 + passengerAndFlightsLine4
                                + passengerAndFlightsLine5;

                Files.write(passengersAndFlightsCorrectFile.toPath(), passengersAndFlightsContent.getBytes());

                Map<Passenger, List<FlightData>> passengersAndFlights = fileHandler
                                .readPassengersAndFlights("passengersAndFlightsCorrectFile");
                assertTrue(passengersAndFlights.containsKey(olaNordmann));
                assertTrue(passengersAndFlights.containsKey(trulsNordmann));
                assertTrue(passengersAndFlights.containsKey(arneNordmann));
                assertTrue(passengersAndFlights.containsKey(olaHansen));
                assertTrue(passengersAndFlights.containsKey(trulsGustavsen));

                assertEquals(passengersAndFlights.get(olaNordmann).size(), 2);
                assertEquals(passengersAndFlights.get(trulsNordmann).size(), 0);
                assertEquals(passengersAndFlights.get(arneNordmann).size(), 1);
                assertEquals(passengersAndFlights.get(olaHansen).size(), 2);
                assertEquals(passengersAndFlights.get(trulsGustavsen).size(), 0);

        }

        @Test
        public void testAddOrderedFlights() throws IOException {
                String passengerAndFlightsLine1BeforeFlightsAreAdded = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                                + "Canberra.Paris.2023-04-12.9F.1050;London.New York.2023-04-12.9F.500;" + "\n";
                String passengerAndFlightsLine2BeforeFlightsAreAdded = "Truls;Nordmann;98123456;truls32@gmail.com: "
                                + "\n";
                String passengersAndFlightsContentBeforeFlightsAreAdded = passengerAndFlightsLine1BeforeFlightsAreAdded
                                + passengerAndFlightsLine2BeforeFlightsAreAdded;

                passengersAndFlightsPath = Path.of(FileHandler.generateFilePath("new_passengersAndFlights"));
                Files.write(passengersAndFlightsPath,
                                passengersAndFlightsContentBeforeFlightsAreAdded.getBytes());

                String expected = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                                + "Canberra.Paris.2023-04-12.9F.1050;London.New York.2023-04-12.9F.500;" + "\n"
                                + "Truls;Nordmann;98123456;truls32@gmail.com: London.New york.2023-04-12.10F.500;"
                                + "\n";

                createBookingSystem();
                Flight flight = new Flight("London", "New York", LocalDate.of(2023, 4, 12), false);
                Passenger trulsNordmann = new Passenger("Truls", "Nordmann", "98123456", "truls32@gmail.com");
                FlightData flightData = new FlightData(flight, trulsNordmann, "10F", bookingSystem);
                List<FlightData> orderedFlights = List.of(flightData);

                fileHandler.addOrderedFlights("new_passengersAndFlights", trulsNordmann,
                                orderedFlights);

                String actual = Files.readString(passengersAndFlightsPath);

                assertEquals(expected, actual);
        }

        @Test
        public void testRemoveOrderedFlights() throws IOException {

                String passengerAndFlightsLine1BeforeFlightsAreRemoved = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                                + "Canberra.Paris.2023-04-12.9F.1050;London.New York.2023-04-12.9F.500;" + "\n";
                String passengerAndFlightsLine2BeforeFlightsAreRemoved = "Truls;Nordmann;98123456;truls32@gmail.com: "
                                + "\n";
                String passengersAndFlightsContentBeforeFlightsAreRemoved = passengerAndFlightsLine1BeforeFlightsAreRemoved
                                + passengerAndFlightsLine2BeforeFlightsAreRemoved;

                passengersAndFlightsPath = Path.of(FileHandler.generateFilePath("new_passengersAndFlights"));
                Files.write(passengersAndFlightsPath,
                                passengersAndFlightsContentBeforeFlightsAreRemoved.getBytes());

                String expected = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                                + "Canberra.Paris.2023-04-12.9F.1050;" + "\n"
                                + "Truls;Nordmann;98123456;truls32@gmail.com: "
                                + "\n";

                createBookingSystem();
                Flight flight = new Flight("London", "New York", LocalDate.of(2023, 4, 12), false);
                Passenger olaNordmann = new Passenger("Ola", "Nordmann", "98448822", "nordmann34@gmail.com");
                FlightData flightData = new FlightData(flight, olaNordmann, "9F", bookingSystem);
                fileHandler.removeOrderedFlight("new_passengersAndFlights", olaNordmann, flightData);

                String actual = Files.readString(passengersAndFlightsPath);

                assertEquals(expected, actual);
        }

        @Test
        public void testReadAPassengersOrderedFlights() throws IOException {
                Assertions.assertThrows(FileNotFoundException.class, () -> {
                        fileHandler.readAPassengersOrderedFlights("invalid", passenger);
                });

                String emptyLines = "\n\n\n\n";
                String invalidLines = "Ola;Nordmadmann34@gmail.com: "
                                + "Canberra.Paris.22023-04-12.9F.500";
                Files.write(passengersAndFlightsCorrectFile.toPath(), emptyLines.getBytes());
                Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
                        fileHandler.readAPassengersOrderedFlights("passengersAndFlightsCorrectFile", passenger);
                });

                Files.write(passengersAndFlightsCorrectFile.toPath(), invalidLines.getBytes());
                Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
                        fileHandler.readAPassengersOrderedFlights("passengersAndFlightsCorrectFile", passenger);
                });

                String passengerAndFlightsLine = "Ola;Nordmann;98448822;nordmann34@gmail.com: "
                                + "Canberra.Paris.2020-04-12.9F.1050;London.New York.2025-04-12.9F.500;" + "\n";
                passengersAndFlightsPath = Path.of(FileHandler.generateFilePath("new_passengersAndFlights"));

                Files.write(passengersAndFlightsPath,
                                passengerAndFlightsLine.getBytes());

                createBookingSystem();
                Flight flight1 = new Flight("London", "New York", LocalDate.of(2025, 4, 12), false);
                Flight flight2 = new Flight("London", "New York", LocalDate.of(2020, 4, 12), false);
                Passenger passenger = new Passenger("Ola", "Nordmann", "98448822", "nordmann34@gmail.com");
                List<FlightData> bookedFlights = fileHandler.readAPassengersOrderedFlights("new_passengersAndFlights",
                                passenger);

                assertTrue(bookedFlights.contains(new FlightData(flight1, passenger, "9F", bookingSystem)));
                assertFalse(bookedFlights.contains(new FlightData(flight2, passenger, "9F", bookingSystem)),
                                "'bookedFlights' should not contain flightDatas that has departure in the past.");
                assertEquals(1, bookedFlights.size());
        }

        @Test
        public void testReadCities() throws IOException {
                Assertions.assertThrows(FileNotFoundException.class, () -> {
                        fileHandler.readCities("invalid");
                });

                String correctCitiesContent = "London;51.509865;-0.118092\nOslo;59.911491;10.757933\nParis;48.864716;2.349014\nCanberra;-35.282001;149.128998\nNew York;40.730610;-73.935242\nBrasilia;-15.793889;-47.882778";
                Files.write(citiesCorrectFile.toPath(), correctCitiesContent.getBytes());
                City london = new City("London", 51.509865, -0.118092);
                City oslo = new City("Oslo", 59.911491, 10.757933);
                City paris = new City("Paris", 48.864716, 2.349014);
                City canberra = new City("Canberra", -35.282001, 149.128998);
                City newYork = new City("New York", 40.730610, -73.935242);
                City brasilia = new City("Brasilia", -15.793889, -47.882778);
                List<City> actualCities = fileHandler.readCities("citiesCorrectFile");

                assertTrue(actualCities.contains(london));
                assertTrue(actualCities.contains(oslo));
                assertTrue(actualCities.contains(paris));
                assertTrue(actualCities.contains(canberra));
                assertTrue(actualCities.contains(newYork));
                assertTrue(actualCities.contains(brasilia));
        }

        @AfterAll
        public void tearDown() {
                passengersAndFlightsCorrectFile.delete();
                activePassengerCorrectFile.delete();
                citiesCorrectFile.delete();
                activePassengerCorrectFile.delete();
                passengersAndFlightsCorrectFile.delete();
                passengersAndFlightsPath.toFile().delete();
                activePassengerPath.toFile().delete();
        }
}