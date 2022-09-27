package OOPAirlines;

import java.io.FileNotFoundException;
import java.time.LocalDate;

public class FlightData {
    private String departure;
    private String destination;
    private String date;
    private String time;
    private String price;
    private String seatPosition;
    private Flight flight;
    private PassengerSeat passengerSeat;
    private Passenger passenger;
    private double travelTimeDouble;
    private int priceInt;

    public FlightData(Flight flight, String seatPosition) throws FileNotFoundException {
        // Constructor were the price is not important.
        this.flight = flight;
        this.departure = flight.getDeparture().getCity().substring(0,
                1).toUpperCase()
                + flight.getDeparture().getCity().substring(1);

        this.destination = flight.getDestination().getCity().substring(0,
                1).toUpperCase()
                + flight.getDestination().getCity().substring(1);

        this.date = flight.getDepartureDate().toString();

        PassengerSeat.checkSeatPosition(seatPosition);
        this.seatPosition = seatPosition;
    }

    public FlightData(Flight flight, Passenger passenger, String seatPosition, BookingSystem bookingSystem)
            throws FileNotFoundException {

        this.flight = flight;

        PassengerSeat.checkSeatPosition(seatPosition);
        this.seatPosition = seatPosition.strip().toUpperCase();

        this.passengerSeat = getPassengerSeat(flight, seatPosition.strip().toUpperCase(), passenger, bookingSystem);
        this.price = Integer.toString(passengerSeat.getPrice());
        this.priceInt = passengerSeat.getPrice();

        this.departure = flight.getDeparture().getCity().substring(0, 1).toUpperCase()
                + flight.getDeparture().getCity().substring(1);

        this.destination = flight.getDestination().getCity().substring(0, 1).toUpperCase()
                + flight.getDestination().getCity().substring(1);

        this.date = flight.getDepartureDate().toString();

        this.time = String.format("%.1f", flight.getTravelTime());
        this.travelTimeDouble = flight.getTravelTime();

        this.passenger = passenger;

    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPrice() {
        return price;
    }

    public String getSeatPosition() {
        return seatPosition;
    }

    public Flight getFlight() {
        return flight;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public double getTravelTimeDouble() {
        return travelTimeDouble;
    }

    public int getPriceInt() {
        return priceInt;
    }

    public String getDeparture() {
        return departure;
    }

    public LocalDate getDateLocalDate() {
        if (this.flight != null) {
            return this.flight.getDepartureDate();
        } else {
            throw new NullPointerException("The flight must be set in order to get date");
        }
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private PassengerSeat getPassengerSeat(Flight flight, String seatPosition, Passenger passenger,
            BookingSystem bookingSystem)
            throws FileNotFoundException {
        passengerSeat = flight.getPassengerSeats().get(seatPosition.strip().toUpperCase());
        if (DiscountAvailable(passenger, bookingSystem)) {
            if (passengerSeat instanceof BusinessClassSeat || passengerSeat instanceof FirstClassSeat) {
                passengerSeat.alertAboutDiscount();
            }
        }
        return passengerSeat;
    }

    private boolean DiscountAvailable(Passenger passenger, BookingSystem bookingSystem) throws FileNotFoundException {
        if (bookingSystem.getFlightCount(passenger) >= 5) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((departure == null) ? 0 : departure.hashCode());
        result = prime * result + ((destination == null) ? 0 : destination.hashCode());
        result = prime * result + ((seatPosition == null) ? 0 : seatPosition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlightData other = (FlightData) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (departure == null) {
            if (other.departure != null)
                return false;
        } else if (!departure.equals(other.departure))
            return false;
        if (destination == null) {
            if (other.destination != null)
                return false;
        } else if (!destination.equals(other.destination))
            return false;
        if (seatPosition == null) {
            if (other.seatPosition != null)
                return false;
        } else if (!seatPosition.equals(other.seatPosition))
            return false;
        return true;
    }

}
