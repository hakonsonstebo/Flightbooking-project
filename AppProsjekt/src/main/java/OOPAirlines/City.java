package OOPAirlines;

import java.util.List;

public class City {
    private double latitude;
    private double longitude;
    private String city;

    public City(String city, double latitude, double longitude) {
        if (city == null) {
            throw new IllegalArgumentException("City must be set");
        }
        if (!isValidCity(city)) {
            throw new IllegalArgumentException("OOPAirlines does not provide a flight from: " + city);
        }
        if (!isValidCooridnates(latitude, longitude)) {
            throw new IllegalArgumentException("This is not valid cooridinates");
        }
        this.city = city.toLowerCase();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private boolean isValidCooridnates(double latitude, double longitude) {
        if ((latitude < 90 && latitude > -90) && longitude < 180 && (longitude > -180)) {
            return true;
        }
        return false;
    }

    private boolean isValidCity(String city) {
        List<String> citiesAvilable = List.of("oslo", "new york", "canberra", "brasilia", "london", "paris");
        if (citiesAvilable.contains(city.toLowerCase())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        City other = (City) obj;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        return true;
    }
}
