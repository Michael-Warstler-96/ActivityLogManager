package edu.ncsu.csc316.activity.manager;

import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import edu.ncsu.csc316.activity.data.LogEntry;
import edu.ncsu.csc316.activity.dsa.Algorithm;
import edu.ncsu.csc316.activity.dsa.DSAFactory;
import edu.ncsu.csc316.activity.dsa.DataStructure;
import edu.ncsu.csc316.activity.io.LogEntryReader;
import edu.ncsu.csc316.dsa.data.Activity;
import edu.ncsu.csc316.dsa.list.List;
import edu.ncsu.csc316.dsa.map.Map;
import edu.ncsu.csc316.dsa.map.Map.Entry;
import edu.ncsu.csc316.dsa.sorter.Sorter;

/**
 * Class builds maps and lists of entries based on frequency of activity, date
 * of activity, or hour of activity. Activities are initially logged through the
 * LogEntryReader.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class UserActivityLogManager {

	/** List of log entries */
	private List<LogEntry> logList;
	/** Time formatter for Month/Day/Year formatting. */
	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	/** Time formatter for hours:minutes:seconds. */
	private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ssa");

	/**
	 * Constructs a UserActivityLogManager using the parameter location for a file
	 * of log entries. Default map data structure type is set to a SkipList due to
	 * AVG O(logn) put/get/remove.
	 * 
	 * @param pathToFile is string representation of filename containing log
	 *                   entries.
	 * @throws FileNotFoundException if file cannot be located or has error during
	 *                               reading.
	 */
	public UserActivityLogManager(String pathToFile) throws FileNotFoundException {
		this(pathToFile, DataStructure.LINEARPROBINGHASHMAP);
	}

	/**
	 * Constructs a UserActivityLogManager using the parameter location for a file
	 * of log entries. List type is set to ArrayBasedList due to removal of objects
	 * not being used. Merge sort and Counting sort algorithms are used for sorting
	 * due to best O performances. Map type is set to parameter type (default is
	 * SkipList).
	 * 
	 * @param pathToFile is string representation of filename containing log
	 *                   entries.
	 * @param mapType    is Map ADT type to use.
	 * @throws FileNotFoundException if file cannot be located or has error during
	 *                               reading.
	 */
	public UserActivityLogManager(String pathToFile, DataStructure mapType) throws FileNotFoundException {
		DSAFactory.setListType(DataStructure.ARRAYBASEDLIST); // Specify your default list data structure type
		DSAFactory.setComparisonSorterType(Algorithm.MERGESORT); // Specify your default comparison sorter type
		DSAFactory.setNonComparisonSorterType(Algorithm.COUNTING_SORT); // Specify you default non-comparison sorter
		DSAFactory.setMapType(mapType);

		// Load entries from file path.
		logList = LogEntryReader.loadLogEntries(pathToFile);
	}

	/**
	 * Returns a List of the most frequently performed N user activities in the
	 * input log entry file.
	 * 
	 * @param number is number of activities that should appear in the list
	 *               (eventually report).
	 * @return List of most frequently performed N user activities in input file or
	 *         empty list if log contains no entries.
	 */
	public List<String> getTopActivities(int number) {
		// Create map to store Activities.
		// Key = String of activity = action resource\n
		// Value = frequency of that activity.
		Map<String, Integer> frequencyMap = DSAFactory.getMap(null);

		// Go through log entries and track frequencies of entries with same activity.
		for (int i = 0; i < logList.size(); i++) {
			// Create String for activity action + resource.
			StringBuilder builder = new StringBuilder(logList.get(i).getAction());
			builder.append(" ");
			builder.append(logList.get(i).getResource());
			//builder.append("\n");
			String activity = builder.toString();

			// Always put activity in map if map is empty.
			if (frequencyMap.isEmpty()) {
				// Put activity (action resource) key with frequency of 1 into the map.
				frequencyMap.put(activity, 1);
			}
			// If map is not empty...
			else {
				// Get the frequency of the activity in the map.
				Integer activityFrequency = frequencyMap.get(activity);

				// Null Frequency indicates that current Activity is not in the map yet.
				if (activityFrequency == null) {
					frequencyMap.put(activity, 1); // place activity in map with frequency = 1.
				}
				// Otherwise Activity is currently in map with frequency >= 1.
				else {
					// Put activity back into map with 1 added to value, thus overwriting it.
					frequencyMap.put(activity, activityFrequency + 1);
				}
			}
		}

		// Unique activities with corresponding frequencies now in frequencyMap. Convert
		// to list of Strings sorted in proper format.
		return getMapAsSortedList(frequencyMap, number);
	}

	/**
	 * Converts the map holding (activity(string) - frequency) entries into a list.
	 * This is necessary to sort, and ultimately index until the user's specified
	 * number of activities are reported.
	 * 
	 * @param frequencyMap is map containing activity keys and frequency values.
	 * @param number       is the number of activities to be in final list.
	 * @return is a List of strings created from the frequencyMap.
	 */
	private List<String> getMapAsSortedList(Map<String, Integer> frequencyMap, int number) {
		// Get iterator to move through map entries.
		Iterator<Entry<String, Integer>> it = frequencyMap.entrySet().iterator();

		// Create array of Activities equal to size of the frequency map.
		Activity[] activities = new Activity[frequencyMap.size()];
		int i = 0;
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next(); // Get entry from map via iteration.
			// Create Activity based on latest entry and add to array.
			activities[i] = new Activity(entry.getValue(), entry.getKey());
			i++;
		}

		// Get Merge Sorter and sort array of Activities.
		Sorter<Activity> s = DSAFactory.getComparisonSorter(null);
		s.sort(activities);

		// Convert Activity array into List of Strings. Strings in list being Activity
		// descriptions.
		List<String> sortedFrequencyList = DSAFactory.getIndexedList();

		// Can only create list as long as number of unique entries. Check parameter to
		// avoid index out of bounds.
		int bounds = number > activities.length ? activities.length : number;

		// Place activities string representation into the return list.
		for (int j = 0; j < bounds; j++) {
			sortedFrequencyList.addLast(activities[j].getDescription());
		}
		return sortedFrequencyList;
	}

	/**
	 * Returns a Map that represents the List of log entries performed on each
	 * unique date. For the Map, the String key represents the date in the format
	 * MM/DD/YYYY.
	 * 
	 * @return is Map of log entries performed on each unique date. Returns an empty
	 *         map if the log contains no entries.
	 */
	public Map<String, List<LogEntry>> getEntriesByDate() {
		// Create Map of entries by date.
		Map<String, List<LogEntry>> dateMap = DSAFactory.getMap(null);

		// Move through log entry list and put unique dates into dateMap.
		for (int i = 0; i < logList.size(); i++) {
			LogEntry entry = logList.get(i);
			String dateKey = entry.getTimestamp().format(dateFormat); // get time in MM/DD/YYYY string.

			// Begin to put entries into dateMap.
			// If the key/date cannot be found.
			if (dateMap.isEmpty() || dateMap.get(dateKey) == null) {
				// Create list for matching dates and add current entry to it.
				List<LogEntry> matchingDateList = DSAFactory.getIndexedList();
				matchingDateList.addLast(entry);

				// Add to map.
				dateMap.put(dateKey, matchingDateList);
			}
			// Otherwise, there is currently a matching key/date in the map.
			else {
				dateMap.get(dateKey).addLast(entry);
			}
		}

		// Return the map of ( Date - List(entries of matching date) )
		return dateMap;
	}

	/**
	 * Returns a Map that represents the List of log entries performed during each
	 * hour of the day. For the Map, the Integer key represents the hour of the day
	 * (from 0-23, where 0=12AM-1AM; 1 = 1AM-2AM; etc.).
	 * 
	 * @return is map representing log entries performed during each hour of the day
	 *         or empty map if log contains no entries.
	 */
	public Map<Integer, List<LogEntry>> getEntriesByHour() {
		// Create Map of entries by time (hour)
		Map<Integer, List<LogEntry>> hourMap = DSAFactory.getMap(null);

		// Move through log entry list and put unique hours into hourMap.
		for (int i = 0; i < logList.size(); i++) {
			LogEntry entry = logList.get(i);
			String time = entry.getTimestamp().format(timeFormat); // get time in HH:mm:ssa.
			int hourKey = Integer.parseInt(time.substring(0, 2)); // convert first 2 characters of time to integer.

			// Begin to put entries into hourMap
			// If the key/hour cannot be found.
			if (hourMap.isEmpty() || hourMap.get(hourKey) == null) {
				// Create list for matching hours and add current entry to it.
				List<LogEntry> matchingHourList = DSAFactory.getIndexedList();
				matchingHourList.addLast(entry);

				// Add to map
				hourMap.put(hourKey, matchingHourList);
			}
			// Otherwise, there is currently a matching key/hour in the map.
			else {
				hourMap.get(hourKey).addLast(entry);
			}
		}

		// Return the map of Hour - List(entries of matching hour)
		return hourMap;
	}
}