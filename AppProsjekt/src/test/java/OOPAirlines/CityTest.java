package OOPAirlines;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CityTest {

    private City city;

    private void checkCityState(String cityName, double latitude, double longitude) {
        Assertions.assertEquals(cityName, city.getCity());
        Assertions.assertEquals(latitude, city.getLatitude());
        Assertions.assertEquals(longitude, city.getLongitude());
    }

    private void checkInvalidConstructor(String city, double latitude, double longitude) {
        assertThrows(IllegalArgumentException.class, () -> {
            new City(city, latitude, longitude);
        });
    }

    @Test
    void testConstructor() {
        city = new City("London", 51.509865, -0.118092);
        checkCityState("london", 51.509865, -0.118092);
        city = new City("Paris", 48.864716, 2.349014);
        checkCityState("paris", 48.864716, 2.349014);
        checkInvalidConstructor("nrrr", 43.55555, 3.434333);
        checkInvalidConstructor(null, 43.55555, 3.434333);
    }

}
