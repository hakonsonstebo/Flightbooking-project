package OOPAirlines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PassengerSeat {
    private String seatPosition;
    private Flight flight;
    protected int price;

    public PassengerSeat(String seatPosition, Flight flight) {
        checkSeatPosition(seatPosition);
        this.seatPosition = seatPosition.strip().toUpperCase();
        this.flight = flight;
    }

    public static void checkSeatPosition(String seatPosition) {
        if (seatPosition == null) {
            throw new IllegalArgumentException(
                    "Must type in a seat position on the format fom '1A' to '30F' in order to search flights");
        }

        Pattern pattern = Pattern.compile("^(\\d{1,2})([A-F])$");
        Matcher matcher = pattern.matcher(seatPosition.strip().toUpperCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Not a valid seat number. Must be on the form '1A' to '30F' in order to search flights");
        }

        int num = Integer.parseInt(seatPosition.strip().substring(0, seatPosition.strip().length() - 1));
        if (num <= 0 || num > 30) {
            throw new IllegalArgumentException(
                    "Must type in a seat position on the format fom '1A' to '30F' in order to search flights");
        }

    }

    public abstract void alertAboutDiscount();

    public int getPrice() {
        return price;
    }

    public String getSeatPosition() {
        return seatPosition;
    }

    public Flight getFlight() {
        return this.flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

}
