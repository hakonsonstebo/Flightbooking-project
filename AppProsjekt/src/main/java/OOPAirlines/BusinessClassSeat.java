package OOPAirlines;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BusinessClassSeat extends PassengerSeat {
    private static final int defaultPrice = 250;
    private boolean discountAvailable;

    public BusinessClassSeat(String seatPosition, Flight flight) {
        super(seatPosition, flight);
        super.price = getPrice();
        this.checkBusinessSeatPosition(seatPosition);
    }

    @Override
    public void alertAboutDiscount() {
        this.discountAvailable = true;
    }

    public int getPrice() {
        return calculatePrice();
    }

    private int calculatePrice() {
        double distance = getFlight().getDistance();
        LocalDate date = getFlight().getDepartureDate();

        int price = defaultPrice;
        if (distance > 1000) {
            price += (Math.rint(distance / 1000)) * 50;
        }
        if (ChronoUnit.DAYS.between(LocalDate.now(), date) > 100) {
            price -= 50;
        }
        if (discountAvailable) {
            price -= 100;
        }
        return price;
    }

    private void checkBusinessSeatPosition(String seatPosition) {
        int num = Integer.parseInt(seatPosition.substring(0, seatPosition.length() - 1));
        if (num <= 5 || num >= 16) {
            throw new IllegalArgumentException("Business class seats have row numbers from 6 to 15");
        }
    }
}
