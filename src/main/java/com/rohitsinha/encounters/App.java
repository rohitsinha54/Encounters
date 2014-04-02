package com.rohitsinha.encounters;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The main class for Encounters application which find encounters between users from their geographic location
 *
 * @author Rohit Sinha
 */
public class App {

	public static final String DELIMITER = "|";
	private final static Logger LOGGER = Logger.getLogger(App.class.getName());
	private static final int FAILURE = -1;
	private static final double ENCOUNTER_DISTANCE = .15;    // minimum distance between two users in kms
	private static final int ACTIVE_USER_DURATION = 6;        // duration to keep an user active
	private static final int ENCOUNTER_INTERVAL = 24;        // interval for generating new encounters
	private static final int TIMESTAMP_TO_HOURS = 3600;
	private static final int FIELDS_IN_LOGS = 4;
	private static final int USERNAME_INDEX = 0;
	private static final int LATITUDE_INDEX = 2;
	private static final int LONGITUDE_INDEX = 3;
	private static final int TIMESTAMP_INDEX = 1;

	private final HashMap<String, User> appUsers;    //map from username -> User object for quick access
	private BufferedWriter outputFile;    // to store encounters

	/**
	 * Parametrized constructor
	 *
	 * @param outputFilePath: filepath where the encounters should be written
	 * @throws IOException: if the named file exists but is a directory rather than a regular file, does not exist but
	 *                      cannot be created, or cannot be opened for any other reason
	 */
	public App(String outputFilePath) throws IOException {

		this.appUsers = new HashMap<String, User>();
		this.outputFile = new BufferedWriter(new FileWriter(outputFilePath));
	}

	public static void main(String[] args) {

		if (args.length != 2) {
			LOGGER.severe("Error: Invalid number of arguments");
			printUsage();
			System.exit(FAILURE);
		}

		App encounterApp = null;
		try {
			encounterApp = new App(args[1]);
			encounterApp.processLogs(args[0]);
			LOGGER.info("All encounters written to: " + args[1]);
		} catch (FileNotFoundException fne) {
			LOGGER.severe("Caught FileNotFoundException: Output file location not found. Make sure that the exists and" +
					" an output file can be created.");
			System.exit(FAILURE);
		} catch (RuntimeException rte) {
			LOGGER.severe("Caught RuntimeException:" + rte.getMessage());
			System.exit(FAILURE);
		} catch (IOException ioe) {
			LOGGER.severe("Caught IOException: Unable to write to output file. Please make sure that the file " +
					"can be written");
			System.exit(FAILURE);
		} finally {
			if (encounterApp != null)
				encounterApp.closeStream(encounterApp.outputFile);
		}
	}

	/**
	 * Function to print the  usage of the application from the commandline
	 */
	private static void printUsage() {
		System.out.println("arguments: [input filepath] [output filepath]");
		System.out.println("input filepath: \t\t path to the file containing list of geographic points for users");
		System.out.println("input filepath: \t\t path to the file where the encounters will be written");
	}

	/**
	 * Function to read the log file line by line doing basic check for existence of all fields to process them
	 *
	 * @param filepath: the path of the input log file
	 * @throws java.lang.RuntimeException: If the number of fields in the current record line is unexpected
	 */
	private void processLogs(String filepath) {
		BufferedReader input = null;
		try {
			if (checkValidFile(filepath)) {
				input = new BufferedReader(new FileReader(filepath));
				while (input.ready()) {
					String[] curLine = input.readLine().trim().split("\\|");
					if (curLine.length != FIELDS_IN_LOGS || curLine == null) {
						throw new RuntimeException("Malformed log file. Please ensure the logs have all the data in " +
								"every" +
								" line");
					}
					processRecord(curLine);
				}
			} else {
				throw new RuntimeException("Invalid log file. Please ensure that the log file exists and is not " +
						"empty");
			}
		} catch (FileNotFoundException e) {
			LOGGER.severe("Caught FileNotFoundException: Unable to find specified log file. Please make sure it " +
					"exists");
			System.exit(FAILURE);
		} catch (IOException e) {
			LOGGER.severe("Caught IOException: Unable to read the specified file");
			System.exit(FAILURE);
		} finally {
			closeStream(input);
		}
	}

	/**
	 * Function to check if the user exists in the application's user list. If found then update its last location and
	 * online time else add this user as a new user with relevant details
	 *
	 * @param curLine: the details of the current record from the log file
	 */
	private void processRecord(String[] curLine) {
		if (appUsers.containsKey(curLine[USERNAME_INDEX])) {
			User curUser = appUsers.get(curLine[USERNAME_INDEX]);
			curUser.updateLocation(new Location(Double.parseDouble(curLine[LATITUDE_INDEX]),
					Double.parseDouble(curLine[LONGITUDE_INDEX])), Long.parseLong(curLine[TIMESTAMP_INDEX]));

			checkEncounter(curUser);

		} else {
			User newUser = new User(curLine[USERNAME_INDEX], new Location(Double.parseDouble(curLine[LATITUDE_INDEX]),
					Double.parseDouble(curLine[LONGITUDE_INDEX])), Long.parseLong(curLine[TIMESTAMP_INDEX]));
			appUsers.put(newUser.getUsername(), newUser);

			LOGGER.info("Added new User: " + newUser.toString());

			checkEncounter(newUser);
		}
	}

	/**
	 * Check for encounter with all existing users in the system
	 *
	 * @param curUser: the current user from the log file
	 */
	private void checkEncounter(User curUser) {

		// check encounters for this new user location with all users in the list except itself
		for (Map.Entry<String, User> entry : appUsers.entrySet()) {
			User otherUser = entry.getValue();
			if (!otherUser.getUsername().equals(curUser.getUsername()))
				validateEncounter(otherUser, curUser);
		}
	}

	/**
	 * Function to validate an encounter with all condition
	 * <p/>
	 * Generate an encounter if the users are <= 150 meters apart.
	 * <p/>
	 * If a user has not generated a point for 6 hours, assume they are no longer active (don't generate any more
	 * encounters until they have a new point)
	 * <p/>
	 * Do not generate more than one encounter per 24 hours for any two users (if unclejoey and danny had an encounter
	 * at 5pm on a Tuesday, they could not have another encounter until 5pm on Wednesday)
	 *
	 * @param otherUser: the other user from existing user's list
	 * @param curUser:   the current user with whom encounters are being detected
	 */
	private void validateEncounter(User otherUser, User curUser) {
		// check for the user to be active in last 6 hours window
		if ((curUser.getLastOnline() - otherUser.getLastOnline()) / TIMESTAMP_TO_HOURS < ACTIVE_USER_DURATION) {
			// check for the distance between these two users to be less than equal to 150 meters
			double distBetweenUsers = otherUser.getLocation().distance(curUser.getLocation());
			if (distBetweenUsers <= ENCOUNTER_DISTANCE) {
				// check if there has been a previous encounter between these two users
				if (curUser.getEncounters().containsKey(otherUser.getUsername())) {
					long lastEncounterTime = curUser.getEncounters().get(otherUser.getUsername());
					// check for no previous encounter in last 24 hours
					if ((curUser.getLastOnline() - lastEncounterTime) / TIMESTAMP_TO_HOURS >= ENCOUNTER_INTERVAL) {
						recordEncounter(otherUser, curUser);
						curUser.getEncounters().put(otherUser.getUsername(), curUser.getLastOnline());
						otherUser.getEncounters().put(curUser.getUsername(), curUser.getLastOnline());
					}
				} else {
					// first encounter with between these users
					recordEncounter(otherUser, curUser);
					curUser.getEncounters().put(otherUser.getUsername(), curUser.getLastOnline());
					otherUser.getEncounters().put(curUser.getUsername(), curUser.getLastOnline());
				}
			}
		}
	}

	/**
	 * Function to record encounters in the output file
	 *
	 * @param otherUser: user1 involved in the encounter
	 * @param curUser:   user2 involved in the encounter
	 */
	private void recordEncounter(User otherUser, User curUser) {
		Encounter newEncounter = new Encounter(curUser, otherUser, curUser.getLastOnline());
		try {
			outputFile.write(newEncounter.toString());    // write this encounter to file
		} catch (IOException e) {
			LOGGER.severe("Caught IOException: Unable to write to output file. Please make sure that the file " +
					"can be written");
			System.exit(FAILURE);
		}
	}

	/**
	 * A simple check to see if file exists and is not empty
	 *
	 * @param filepath: the file path
	 * @return: boolean which is true if file exists and is not empty else false
	 */
	private boolean checkValidFile(String filepath) {
		File file = new File(filepath);
		return (file.exists() && file.length() != 0);
	}

	/**
	 * A function to close {@link java.io.Closeable} properly
	 *
	 * @param closeable: {@link java.io.Closeable} to be closed
	 */
	private void closeStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				LOGGER.severe("Caught IOException: While trying to close the file");
				System.exit(FAILURE);
			}
		}
	}
}
