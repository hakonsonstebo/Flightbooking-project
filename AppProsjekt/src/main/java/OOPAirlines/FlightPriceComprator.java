package OOPAirlines;

import java.util.Comparator;

public class FlightPriceComprator implements Comparator<FlightData> {
    private boolean ascending;

    public FlightPriceComprator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(FlightData flightData1, FlightData flightData2) {
        if (ascending) {
            return Integer.parseInt(flightData1.getPrice()) - Integer.parseInt(flightData2.getPrice());
        } else {
            return Integer.parseInt(flightData2.getPrice()) - Integer.parseInt(flightData1.getPrice());
        }

    }

}
