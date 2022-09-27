package OOPAirlines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IFileHandler {

        List<City> readCities(String filename) throws FileNotFoundException;

        void savePassenger(String passengersAndFlightsFilename, String activePassengerFilename, Passenger passenger)
                        throws FileNotFoundException, IOException;

        public Passenger readActivePassenger(String passengersAndFlightsFilename, String activePassengerFilename)
                        throws FileNotFoundException;

        void removeOrderedFlight(String filename, Passenger passenger, FlightData selectedRemoveFlight)
                        throws IOException;

        void addOrderedFlights(String filename, Passenger passenger, List<FlightData> flightsOrdered)
                        throws FileNotFoundException, IOException;

        public List<FlightData> readAPassengersOrderedFlights(String filename, Passenger passenger)
                        throws FileNotFoundException;

        Map<Passenger, List<FlightData>> readPassengersAndFlights(String filename)
                        throws FileNotFoundException, ArrayIndexOutOfBoundsException;

}