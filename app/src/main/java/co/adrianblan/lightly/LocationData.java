package co.adrianblan.lightly;

/**
 * Stores the data of the name of the location, and the position.
 */
public class LocationData {

    private String regionName; // Region name is roughly the largest nearby city
    private String country;

    private double lat;
    private double lon;

    public LocationData() {}

    public LocationData (String regionName, String country, double lat, double lon) {
        this.regionName = regionName;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    /** Returns a LocationData object that is mocked to central Stockholm */
    public static LocationData getDummyLocationData() {
        LocationData dummyLocationData = new LocationData();

        dummyLocationData.setRegionName("Unknown");
        dummyLocationData.setRegionName("Sweden");
        dummyLocationData.setLat(59.32);
        dummyLocationData.setLon(18.07);

        return dummyLocationData;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}