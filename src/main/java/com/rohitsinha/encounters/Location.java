package com.rohitsinha.encounters;

/**
 * Class for managing geographic locations
 *
 * @author Rohit Sinha
 */
public class Location {

	private static final double R = 6372.8; // In kilometers

	private final double latitude;
	private final double longitude;

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Function to calculate distance between two geographic coordinates using Haversine formula
	 * Reference:	http://en.wikipedia.org/wiki/Haversine_formula http://rosettacode.org/wiki/Haversine_formula
	 *
	 * @param obj: the other location to be compared with
	 * @return the distance between the location according to Haversine formula
	 */
	public double distance(Location obj) {

		double dLat = Math.toRadians(obj.latitude - latitude);
		double dLon = Math.toRadians(obj.longitude - longitude);
		double lat1 = Math.toRadians(latitude);
		double lat2 = Math.toRadians(obj.latitude);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1)
				* Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	@Override
	public String toString() {

		return String.valueOf(latitude) + App.DELIMITER + longitude;
	}
}
