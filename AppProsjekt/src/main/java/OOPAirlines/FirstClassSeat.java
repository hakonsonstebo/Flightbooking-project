package OOPAirlines;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FirstClassSeat extends PassengerSeat {

    private static final int defaultPrice = 500;
    private boolean discountAvailable;

    public FirstClassSeat(String seatPosition, Flight flight) {
        super(seatPosition, flight);
        super.price = getPrice();
        this.checkFirstClassSeatPosition(seatPosition);
    }

    public int getPrice() {
        return calculatePrice();
    }

    @Override
    public void alertAboutDiscount() {
        this.discountAvailable = true;
    }

    private int calculatePrice() {
        double distance = getFlight().getDistance();
        LocalDate date = getFlight().getDepartureDate();

        int price = defaultPrice;
        if (distance > 1000) {
            price += (Math.rint(distance / 1000)) * 75;
        }
        if (ChronoUnit.DAYS.between(LocalDate.now(), date) > 100) {
            price -= 50;
        }
        if (discountAvailable) {
            price -= 100;
        }
        return price;
    }

    private void checkFirstClassSeatPosition(String seatPosition) {
        int num = Integer.parseInt(seatPosition.substring(0, seatPosition.length() - 1));
        if (num <= 0 || num >= 6) {
            throw new IllegalArgumentException("First class seats have row numbers from 1 to 5");
        }
    }
}
