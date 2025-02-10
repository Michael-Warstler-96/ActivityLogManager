package edu.ncsu.csc316.activity.manager;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import edu.ncsu.csc316.activity.data.LogEntry;
import edu.ncsu.csc316.activity.dsa.Algorithm;
import edu.ncsu.csc316.activity.dsa.DSAFactory;
import edu.ncsu.csc316.activity.dsa.DataStructure;
import edu.ncsu.csc316.dsa.list.List;
import edu.ncsu.csc316.dsa.sorter.Sorter;

/**
 * Class handles construction of report strings for ActivityLogManager program.
 * Class uses UserActivityLogManager class to create reports of entries based on
 * frequency, date, and hour.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class ReportManager {

	/** UserActivityLogManager used to handle log entries */
	private UserActivityLogManager activityLogManager;
	/** Used for parsing user choice of date for DateReport */
	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	/** DateTimeFormatter to get the final date/time in the correct format */
	private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ssa");
	/** Indention used for reports */
	private static final String INDENT = "   ";

	/**
	 * Constructs a ReportManager using the parameter filename location and the
	 * default map type of a SkipList due to avg O(logn) put/get/remove behavior.
	 * 
	 * @param pathToFile is string representation of filename containing log
	 *                   entries.
	 * @throws FileNotFoundException if file cannot be located or has error during
	 *                               reading.
	 */
	public ReportManager(String pathToFile) throws FileNotFoundException {
		this(pathToFile, DataStructure.LINEARPROBINGHASHMAP);
	}

	/**
	 * Constructs a ReportManager using the parameter location for a file of log
	 * entries. List type is set to ArrayBasedList due to removal of objects not
	 * being used. Merge sort and Counting sort algorithms are used for sorting due
	 * to best O performances. Map type is set to parameter type (default is
	 * SkipList).
	 * 
	 * @param pathToFile is string representation of filename containing log
	 *                   entries.
	 * @param mapType    is Map ADT type to use.
	 * @throws FileNotFoundException if file cannot be located or has error during
	 *                               reading.
	 */
	public ReportManager(String pathToFile, DataStructure mapType) throws FileNotFoundException {
		activityLogManager = new UserActivityLogManager(pathToFile, mapType);

		// Specify default List, sorters, and map type.
		DSAFactory.setListType(DataStructure.ARRAYBASEDLIST);
		DSAFactory.setComparisonSorterType(Algorithm.MERGESORT);
		DSAFactory.setNonComparisonSorterType(Algorithm.COUNTING_SORT);
		DSAFactory.setMapType(mapType);
	}

	/**
	 * Gets a report of the most commonly performed user activities. User specifies
	 * how many activities should appear in the report. Output report shows
	 * activities ordered in descending order by frequency. Activities with the same
	 * frequency are ordered alphabetically in ascending order.
	 * 
	 * If a user enters number <= 0, then user is reprompted to enter number > 0. If
	 * a user enters a number greater than the number of unique activities in the
	 * input file, then the report will contain all of the unique user activities
	 * from the file.
	 * 
	 * If the input file doesn't contain any log entries, then user is notified.
	 * 
	 * @param number is how many activities should appear in the report.
	 * @return output string listing the most commonly performed user activities or
	 *         error message if input is less than 0.
	 */
	public String getTopUserActivitiesReport(int number) {
		// Check for invalid number entered.
		if (number <= 0) {
			return "Please enter a number > 0\n";
		}

		// Get list of top activities from the log.
		List<String> topActivities = activityLogManager.getTopActivities(number);

		// Build report string from the list of top activities.
		StringBuilder builder = new StringBuilder("Top User Activities Report [\n");
		for (int i = 0; i < topActivities.size(); i++) {
			builder.append(INDENT);
			builder.append(topActivities.get(i));
			builder.append("\n");
		}
		builder.append("]\n"); // tail end of report.

		// Convert to string and return.
		return builder.toString();
	}

	/**
	 * Gets a report of log entries for a specific date. Entries ordered in
	 * chronological order (ascending order based on date/time). Entries on same
	 * date/time are sorted in ascending alphabetical order based on activity
	 * description.
	 * 
	 * @param date to sort entries by.
	 * @return is string of DateReport or message stating that no activities were
	 *         recorded if date not found.
	 */
	public String getDateReport(String date) {
		try {
			LocalDate.parse(date, dateFormat);
		} catch (DateTimeParseException e) {
			return "Please enter a valid date in the format MM/DD/YYYY";
		}
		// Get the LogEntry list of matching dates from the UserActivityLogManager NOTE
		// THAT THIS LIST IS PROBABLY NOT IN CHRONOLOGICAL ORDER NOR ALPHABETICAL.
		List<LogEntry> matchingDateList = activityLogManager.getEntriesByDate().get(date);

		// Null list indicates no matching date found.
		if (matchingDateList == null) {
			return "No activities were recorded on " + date;
		}
		// Otherwise, append entries to builder.
		else {
			// Sort the list HERE. This allows sorting just one list with a matching date,
			// instead of every list.
			Sorter<LogEntry> s = DSAFactory.getComparisonSorter(null);
			// Convert list into an array of same size.
			LogEntry[] matchingDateArray = new LogEntry[matchingDateList.size()];
			// Move LogEntries from list into Array.
			for (int i = 0; i < matchingDateList.size(); i++) {
				matchingDateArray[i] = matchingDateList.get(i);
			}
			// Sort the array.
			s.sort(matchingDateArray);

			// Start to build string for output report.
			StringBuilder builder = new StringBuilder("Activities recorded on ");
			builder.append(date);
			builder.append(" [\n");

			// Add all entries with matching date
			for (int i = 0; i < matchingDateArray.length; i++) {
				builder.append(INDENT);
				builder.append(matchingDateArray[i].getUsername());
				builder.append(", ");
				builder.append(dateTimeFormat.format(matchingDateArray[i].getTimestamp()));
				builder.append(", ");
				builder.append(matchingDateArray[i].getAction());
				builder.append(", ");
				builder.append(matchingDateArray[i].getResource());
				builder.append("\n");
			}
			builder.append("]\n");
			return builder.toString();
		}
	}

	/**
	 * Gets a report of log entries for a specific hour of the day. Entries are
	 * ordered in ascending order based on date/time. If multiple entries have same
	 * date/time, then they are sorted alphabetical order based on description.
	 * 
	 * @param hour to create report with.
	 * @return is hour report or message stating that no activities were recorded if
	 *         no activities found on hour, or error message if input is less than 0
	 *         or greater than 23.
	 */
	public String getHourReport(int hour) {
		// Check for invalid hour
		if (hour < 0 || hour > 23) {
			return "Please enter a valid hour between 0 (12AM) and 23 (11PM)\n";
		}

		// Get list of entries with a matching hour. Not in alphabetical order.
		List<LogEntry> matchingHourList = activityLogManager.getEntriesByHour().get(hour);

		// Null list indicates no matching date found.
		if (matchingHourList == null) {
			return "No activities were recorded during hour " + hour;
		}
		// Otherwise, append entries to builder.
		else {
			// Sort the list HERE. This allows sorting just one list with a matching hour,
			// instead of every list.
			Sorter<LogEntry> s = DSAFactory.getComparisonSorter(null);
			// Convert list into an array of same size.
			LogEntry[] matchingHourArray = new LogEntry[matchingHourList.size()];
			// Move LogEntries from list into Array.
			for (int i = 0; i < matchingHourList.size(); i++) {
				matchingHourArray[i] = matchingHourList.get(i);
			}
			// Sort the array.
			s.sort(matchingHourArray);

			// Start to build string for output report.
			StringBuilder builder = new StringBuilder("Activities recorded during hour ");
			builder.append(hour);
			builder.append(" [\n");

			// Add all entries with matching hour.
			for (int i = 0; i < matchingHourList.size(); i++) {
				builder.append(INDENT);
				builder.append(matchingHourArray[i].getUsername());
				builder.append(", ");
				builder.append(dateTimeFormat.format(matchingHourArray[i].getTimestamp()));
				builder.append(", ");
				builder.append(matchingHourArray[i].getAction());
				builder.append(", ");
				builder.append(matchingHourArray[i].getResource());
				builder.append("\n");
			}
			builder.append("]\n");
			return builder.toString();
		}
	}
}