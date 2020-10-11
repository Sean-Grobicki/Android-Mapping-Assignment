package mmu.ac.uk.hackathon;

public class Station
{
    
    private String _stationName;
    private double _latitude;
    private double _longitude;
    private double _distance;
    
    /**
     * Constructs a station with the name latitude and longitude and sets distance in km from users current location.
     * @param stationName name of the station
     * @param latitude latitude of the station
     * @param longitude longitude of the station
     * @param distance distance from the users current location.
     */
    
    public Station(String stationName, double latitude, double longitude,double distance)
    {
        set_StationName(stationName);
        set_Latitude(latitude);
        set_Longitude(longitude);
        set_Distance(distance/1000);
    }
    
    /**
     * Getter for the station name variable from station.
     * @return _stationName
     */
    
    public String get_StationName()
    {
        return _stationName;
    }
    
    /**
     * Setter for the station name.
     * @param _stationName Name of the station
     */
    public void set_StationName(String _stationName)
    {
        this._stationName = _stationName;
    }
    
    /**
     * Getter for the stations latitude
     * @return latitude The latitude of the station
     */
    public double get_Latitude()
    {
        return _latitude;
    }
    
    /**
     * Sets the station name to that of the parameter.
     * @param _latitude latitude The latitude of the station
     */
    public void set_Latitude(double _latitude)
    {
        this._latitude = _latitude;
    }
    
    /**
     * Getter for the longitude of the station.
     * @return Longitude of the given station.
     */
    public double get_Longitude()
    {
        return _longitude;
    }
    
    /**
     * Sets the Longitude of the given station to the parameter.
     * @param _longitude Longitude of the given station
     */
    public void set_Longitude(double _longitude)
    {
        this._longitude = _longitude;
    }
    
    /**
     * Gets the distance of the user from the given station.
     * @return the distance of the user from the given station.
     */
    public double get_Distance()
    {
        return _distance;
    }
    
    /**
     * Sets the distance from the user to the station.
     * @param _distance the new distance of the user to the station
     */
    public void set_Distance(double _distance)
    {
        this._distance = _distance;
    }
}
