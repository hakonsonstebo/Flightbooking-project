package OOPAirlines;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EconomyClassSeat extends PassengerSeat {
    private static final int defaultPrice = 150;
    private boolean discountAvailable;

    public EconomyClassSeat(String seatPosition, Flight flight) {
        super(seatPosition, flight);
        // price field is set to protected in superclass
        super.price = getPrice();
        this.checkEconomySeatPosition(seatPosition);
    }

    public int getPrice() {
        return calculatePrice();
    }

    @Override
    public void alertAboutDiscount() {
        // Economy class will never get a discount in this program, but by inherting
        // this method
        // this will be easy to change in an other version of the app
        this.discountAvailable = false;
    }

    private int calculatePrice() {
        double distance = getFlight().getDistance();
        LocalDate date = getFlight().getDepartureDate();

        int price = defaultPrice;
        if (distance > 1000) {
            price += (Math.rint(distance / 1000)) * 25;
        }
        if (ChronoUnit.DAYS.between(LocalDate.now(), date) > 100) {
            price -= 50;
        }
        return price;

    }

    private void checkEconomySeatPosition(String seatPosition) {
        int num = Integer.parseInt(seatPosition.substring(0, seatPosition.length() - 1));
        if (num <= 15 || num >= 31) {
            throw new IllegalArgumentException("Economy class seats have row numbers from 16 to 30");
        }
    }

}
