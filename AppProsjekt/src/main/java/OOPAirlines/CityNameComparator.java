package OOPAirlines;

import java.util.Comparator;

public class CityNameComparator implements Comparator<City> {

    @Override
    public int compare(City l1, City l2) {
        return l1.getCity().compareTo(l2.getCity());
    }

}
