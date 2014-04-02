package com.rohitsinha.encounters;

import java.util.HashMap;

/**
 * Class for users of the application.
 *
 * @author Rohit Sinha
 */
public class User {

	private final String username;
	// Encounters of the user with other users. Key: other user's username Value: encounters time
	private final HashMap<String, Long> encounters;
	private Location location;
	private long lastOnline;

	public User(String username, Location location, long lastOnline) {
		this.username = username;
		this.location = location;
		this.lastOnline = lastOnline;
		encounters = new HashMap<String, Long>();
	}

	public HashMap<String, Long> getEncounters() {
		return encounters;
	}

	public String getUsername() {
		return username;
	}

	public Location getLocation() {
		return location;
	}

	void setLocation(Location location) {
		this.location = location;
	}

	public long getLastOnline() {
		return lastOnline;
	}

	void setLastOnline(long lastOnline) {
		this.lastOnline = lastOnline;
	}

	/**
	 * Function to update the location of a user and also updates the last online time
	 *
	 * @param newLocation: the new location details
	 * @param timestamp:   the unix timestamp when the location was logged
	 */
	public void updateLocation(Location newLocation, long timestamp) {
		setLocation(newLocation);
		setLastOnline(timestamp);
	}

	@Override
	public String toString() {
		return username + App.DELIMITER + location + App.DELIMITER + lastOnline;
	}
}
