package com.rohitsinha.encounters;

/**
 * Class for encounters between users
 *
 * @author Rohit Sinha
 */
public class Encounter {

	private final User user1;
	private final User user2;
	private final long time;

	public Encounter(User usr1, User usr2, long time) {
		// put users in lexicographic order
		if (usr1.getUsername().compareTo(usr2.getUsername()) < 0) {
			this.user1 = usr1;
			this.user2 = usr2;
		} else {
			this.user1 = usr2;
			this.user2 = usr1;
		}
		this.time = time;
	}

	@Override
	public String toString() {
		return String.valueOf(time) + App.DELIMITER + user1.getUsername() + App.DELIMITER + user1.getLocation() + App
				.DELIMITER + user2.getUsername() + App.DELIMITER + user2.getLocation() + System.getProperty("line" +
				".separator");
	}
}
