package OOPAirlines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileHandler implements IFileHandler {

    // In order to be sure that every scanner and fileWriter closes no matter what
    // exception gets thrown, the methods in this class
    // uses try_resourceses. If it catches anything, the exception will force those
    // who use the class to deal with it.

    public final static String SAVE_FOLDER = "src/main/resources/OOPAirlines/files";

    public static String generateFilePath(String filename) {
        return SAVE_FOLDER + "/" + filename + ".txt";
    }

    @Override
    public void savePassenger(String passengersAndFlightsFilename, String activePassengerFilename,
            Passenger passenger) throws IOException {

        // The format on the lines is: "firstName;secondName;number;email: "
        // The " " is included at the end of the line in order to split on the ":" with
        // the methods that reads the data.
        String line = passenger.getFirstName() + ";" + passenger.getSecondName() + ";"
                + passenger.getNumber()
                + ";" + passenger.getEmail() + ": " + "\n";

        try (FileWriter fileWriter1 = new FileWriter(new File(generateFilePath(passengersAndFlightsFilename)),
                true);) {

            BookingSystem bookingSystem = new BookingSystem(passengersAndFlightsFilename);

            if (!bookingSystem.passengerHaveRegistred(passenger)) {
                fileWriter1.write(line);
            }
            // In order to remove all the data present in the writer, the flush() is called
            fileWriter1.flush();
        } catch (Exception e) {
            throw e;
        }

        try (FileWriter fileWriter2 = new FileWriter(new File(generateFilePath(activePassengerFilename)));) {
            // The " " is unesecarry because the line will not get
            // splitted in the active passenger file
            fileWriter2.write(line.replace(": ", ""));
            fileWriter2.flush();
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Passenger readActivePassenger(String passengersAndFlightsFilename, String activePassengerFilename)
            throws FileNotFoundException {

        Passenger passenger = null;
        try (Scanner scanner = new Scanner(new File(generateFilePath(activePassengerFilename)));) {
            String passengerInfo = scanner.nextLine();

            // Splits the line in order to get an array on the format [firstName,
            // secondName, number, email]
            String[] passengerInfoArray = getPassengerInfoArray(passengerInfo);

            passenger = new Passenger(passengerInfoArray[0], passengerInfoArray[1], passengerInfoArray[2],
                    passengerInfoArray[3]);

        } catch (Exception e) {
            throw e;
        }

        return passenger;
    }

    @Override
    public Map<Passenger, List<FlightData>> readPassengersAndFlights(String filename)
            throws FileNotFoundException, ArrayIndexOutOfBoundsException {

        Map<Passenger, List<FlightData>> passengersAndFlights = new LinkedHashMap<>();

        try (Scanner scanner = new Scanner(new File(generateFilePath(filename)));) {

            while (scanner.hasNextLine()) {
                String passengerAndFlights = scanner.nextLine();
                String[] passengerAndFlightsArray = passengerAndFlights.replace("\n", "").split(":");

                String passengerInfo = passengerAndFlightsArray[0];
                String[] passengerInfoArray = getPassengerInfoArray(passengerInfo);

                Passenger passenger;
                try {
                    passenger = new Passenger(passengerInfoArray[0], passengerInfoArray[1],
                            passengerInfoArray[2], passengerInfoArray[3]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw e;
                }

                // Need to append .substring(1) in order to avoid the " " at the start of the
                // line
                String allFlightsInfo = passengerAndFlightsArray[1].substring(1);
                String[] allFlightsInfoArray = allFlightsInfo.split(";");
                List<FlightData> orderedFlights = new ArrayList<>();

                if (passengerHaveNotOrderedAnyFlights(passengerAndFlightsArray[1])) {
                    passengersAndFlights.put(passenger, orderedFlights);
                    continue;
                }

                for (String flightInfo : allFlightsInfoArray) {
                    String[] flightInfoArray = getFlightInfoArray(flightInfo);

                    LocalDate date = LocalDate.of(Integer.parseInt(flightInfoArray[2].substring(0, 4)),
                            Integer.parseInt(flightInfoArray[2].substring(5, 7)),
                            Integer.parseInt(flightInfoArray[2].substring(8)));

                    FlightData flightData = new FlightData(
                            new Flight(flightInfoArray[0], flightInfoArray[1], date, false),
                            flightInfoArray[3]);

                    orderedFlights.add(flightData);
                }
                passengersAndFlights.put(passenger, orderedFlights);
            }
        } catch (Exception e) {
            throw e;
        }
        return passengersAndFlights;
    }

    @Override
    public List<FlightData> readAPassengersOrderedFlights(String filename, Passenger passenger)
            throws FileNotFoundException {
        List<FlightData> pickedFlights = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(generateFilePath(filename)));) {
            while (scanner.hasNextLine()) {
                String passengerAndFlights = scanner.nextLine();
                String[] passengerAndFlightsArray = passengerAndFlights.replace("\n",
                        "").split(":");
                if (foundPassengerMatch(passenger, passengerAndFlightsArray, filename)) {

                    if (passengerHaveNotOrderedAnyFlights(passengerAndFlightsArray[1])) {
                        return pickedFlights;
                    }
                    String allFlightsInfo = passengerAndFlightsArray[1].substring(1);
                    String[] allFlightsInfoArray = allFlightsInfo.split(";");

                    for (String flightInfo : allFlightsInfoArray) {
                        String[] flightInfoArray = getFlightInfoArray(flightInfo);
                        LocalDate date = LocalDate.of(Integer.parseInt(flightInfoArray[2].substring(0, 4)),
                                Integer.parseInt(flightInfoArray[2].substring(5, 7)),
                                Integer.parseInt(flightInfoArray[2].substring(8)));
                        Flight flight = new Flight(flightInfoArray[0], flightInfoArray[1],
                                date, false);

                        if (date.isAfter(LocalDate.now().minusDays(1))) {
                            // Since "My Flights" in the UI only should display future booked flights, only
                            // these flightDatas will be added to "pickedFlights"
                            BookingSystem bookingSystem = new BookingSystem(filename);
                            FlightData flightData = new FlightData(flight, passenger, flightInfoArray[3],
                                    bookingSystem);
                            // Since FlightData-objects have variable price depending on the passengers
                            // ordered flights, this
                            // method must set the orginal price it had when it was ordered, else the
                            // not-discounted-price-version of the FlightData object would have beeen
                            // generated and displayed.
                            flightData.setPrice(flightInfoArray[4]);
                            pickedFlights.add(flightData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return pickedFlights;
    }

    @Override
    public void addOrderedFlights(String filename, Passenger passenger, List<FlightData> flightsOrdered)
            throws IOException {

        Map<String, String> passengerAndFlightsMap = new LinkedHashMap<>();

        try (Scanner scanner = new Scanner(new File(generateFilePath(filename)));) {
            while (scanner.hasNextLine()) {
                String passengerAndFlights = scanner.nextLine();
                String[] passengerAndFlightsArray = passengerAndFlights.replace("\n", "").split(":");
                String passengerInfo = passengerAndFlightsArray[0];

                if (foundPassengerMatch(passenger, passengerAndFlightsArray, filename)) {
                    String flights = "";
                    // In the files, FlightDatas will be represented on the format:
                    // "departure.destination.date,seatPosition.price" and different FlightDatas
                    // will be seperated by ";"
                    for (FlightData flightData : flightsOrdered) {
                        flights += flightData.getDeparture() + "." + flightData.getDestination() + "."
                                + flightData.getDate() + "." + flightData.getSeatPosition() + "."
                                + flightData.getPrice() + ";";
                    }

                    passengerAndFlightsMap.put(passengerInfo,
                            passengerAndFlightsArray[1] + flights);

                } else {
                    passengerAndFlightsMap.put(passengerInfo, passengerAndFlightsArray[1]);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        // Overwrites the passengers-and-flights file with updated
        // information about the passengers orderd flights
        try (FileWriter fileWriter = new FileWriter(new File(generateFilePath(filename)))) {
            for (Map.Entry<String, String> passengerAndFlights : passengerAndFlightsMap.entrySet()) {
                fileWriter.write(passengerAndFlights.getKey() + ":" + passengerAndFlights.getValue() + "\n");
            }
            fileWriter.flush();

        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public void removeOrderedFlight(String filename, Passenger passenger, FlightData selectedRemoveFlight)
            throws IOException {

        if (passenger.getShoppingCartFlights().contains(selectedRemoveFlight)) {
            passenger.removeFlightListener(selectedRemoveFlight);
            return;
        }

        if ((LocalDate.now()).isAfter(selectedRemoveFlight.getDateLocalDate().minusDays(2))
                && (LocalDate.now()).isBefore(selectedRemoveFlight.getDateLocalDate())) {
            throw new IllegalStateException(
                    "Unfortunally, it is not possible to remove a flight when it is under two days until departure");
        }

        Map<String, String> passengerAndFlightsMap = new LinkedHashMap<>();

        try (Scanner scanner = new Scanner(new File(generateFilePath(filename)))) {
            while (scanner.hasNextLine()) {
                String passengerAndFlights = scanner.nextLine();
                String[] passengerAndFlightsArray = passengerAndFlights.replace("\n", "").split(":");
                String passengerInfo = passengerAndFlightsArray[0];

                // If a passenger have not ordered any flights, check the next passenger
                // instead.
                if (passengerHaveNotOrderedAnyFlights(passengerAndFlightsArray[1])) {
                    passengerAndFlightsMap.put(passengerInfo,
                            passengerAndFlightsArray[1]);
                    continue;
                }

                if (foundPassengerMatch(passenger, passengerAndFlightsArray, filename)) {
                    String flights = "";
                    String allFlightsInfo = passengerAndFlightsArray[1].substring(1);
                    String[] allFlightsInfoArray = allFlightsInfo.split(";");

                    for (String flightInfo : allFlightsInfoArray) {
                        String[] flightInfoArray = flightInfo.split("\\.");

                        // If there is a match between the selectedRemoveFlight and the flight found in
                        // the file it will not be included in the updated file.
                        if (foundFlightMatch(flightInfoArray[0], flightInfoArray[1], flightInfoArray[2],
                                flightInfoArray[3],
                                flightInfoArray[4], selectedRemoveFlight)) {
                            continue;
                        } else {
                            flights += flightInfoArray[0] + "." + flightInfoArray[1] + "."
                                    + flightInfoArray[2] + "." + flightInfoArray[3] + "."
                                    + flightInfoArray[4] + ";";
                        }
                    }
                    passengerAndFlightsMap.put(passengerInfo,
                            flights);
                } else {
                    passengerAndFlightsMap.put(passengerInfo,
                            passengerAndFlightsArray[1]);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        try (FileWriter fileWriter = new FileWriter(new File(generateFilePath(filename)));) {
            for (Map.Entry<String, String> passengerAndFlights : passengerAndFlightsMap.entrySet()) {
                // If the first flight on the line has been removed the ": " must be written
                // insted of ":"
                if (passengerAndFlights.getKey().equals(passenger.getFirstName() + ";" + passenger.getSecondName() + ";"
                        + passenger.getNumber() + ";" + passenger.getEmail())) {
                    fileWriter.write(passengerAndFlights.getKey() + ": " + passengerAndFlights.getValue() + "\n");
                } else {
                    fileWriter.write(passengerAndFlights.getKey() + ":" + passengerAndFlights.getValue() + "\n");
                }
            }
            fileWriter.flush();
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<City> readCities(String filename) throws FileNotFoundException {

        List<City> cities = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(generateFilePath(filename)));) {
            while (scanner.hasNextLine()) {
                String cityInfo = scanner.nextLine();
                String[] cityInfoArray = getCityInfoArray(cityInfo);
                City city = new City(cityInfoArray[0], Double.parseDouble(cityInfoArray[1]),
                        Double.parseDouble(cityInfoArray[2]));
                cities.add(city);
            }
        } catch (Exception e) {
            throw e;
        }
        return cities;
    }

    private boolean foundFlightMatch(String departure, String destination, String date, String seatPosition,
            String price,
            FlightData selectedFlight) throws NumberFormatException, FileNotFoundException {

        LocalDate localDate = LocalDate.of(Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(5, 7)),
                Integer.parseInt(date.substring(8)));

        FlightData potentialFlightMatch = new FlightData(new Flight(departure, destination, localDate, false),
                seatPosition);

        if (potentialFlightMatch.equals(selectedFlight)) {
            return true;
        }
        return false;
    }

    private boolean foundPassengerMatch(Passenger passenger, String[] passengerAndFlightsInfoArray, String filename)
            throws FileNotFoundException {
        String passengerInfo = passengerAndFlightsInfoArray[0];
        String[] passengerInfoArray = getPassengerInfoArray(passengerInfo);
        Passenger potenialPassengerMatch = new Passenger(passengerInfoArray[0], passengerInfoArray[1],
                passengerInfoArray[2], passengerInfoArray[3]);

        if (potenialPassengerMatch.equals(passenger)) {
            return true;
        }
        return false;

    }

    private boolean passengerHaveNotOrderedAnyFlights(String allFlightsInfo) {
        // Checks if the passenger have ordered any flights
        String flightsInfo = allFlightsInfo;
        String[] flightsInfoArray = flightsInfo.split(";");
        if (flightsInfoArray[0].equals(" ")) {
            return true;
        }
        return false;
    }

    private String[] getPassengerInfoArray(String passengerInfo) {
        // Splits the line in order to get an array on the format [firstName,
        // secondName, number, email]
        return passengerInfo.split(";");
    }

    private String[] getFlightInfoArray(String flightInfo) {
        // Splits the line in order to get an array on the format [departure,
        // destination, date, seatPosition, price]
        return flightInfo.split("\\.");
    }

    private String[] getCityInfoArray(String cityInfo) {
        // Splits the line in order to get an array on the format [city, latitude,
        // longitude]
        return cityInfo.split(";");
    }
}
