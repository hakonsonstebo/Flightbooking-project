package OOPAirlines;

import java.util.Comparator;

public class FlightTimeComparator implements Comparator<FlightData> {
    private boolean ascending;

    public FlightTimeComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(FlightData flight1, FlightData flight2) {
        if (ascending) {
            if (flight1.getTravelTimeDouble() < flight2.getTravelTimeDouble()) {
                return -1;
            } else if (flight1.getTravelTimeDouble() > flight2.getTravelTimeDouble()) {
                return 1;
            }
        } else {
            if (flight1.getTravelTimeDouble() < flight2.getTravelTimeDouble()) {
                return 1;
            } else if (flight1.getTravelTimeDouble() > flight2.getTravelTimeDouble()) {
                return -1;
            }
        }
        return 0;
    }

}
