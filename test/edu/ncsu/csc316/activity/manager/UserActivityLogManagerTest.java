package edu.ncsu.csc316.activity.manager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import edu.ncsu.csc316.activity.data.LogEntry;
import edu.ncsu.csc316.activity.dsa.DSAFactory;
import edu.ncsu.csc316.activity.dsa.DataStructure;
import edu.ncsu.csc316.dsa.list.List;
import edu.ncsu.csc316.dsa.map.Map;

/**
 * Tests the UserActivityLogManager Class.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class UserActivityLogManagerTest {

	/*
	 * For clarity, input file looks like this:
	 */
//	USERNAME, TIMESTAMP, ACTION, RESOURCE
//	labyrum, 04/06/2013 07:30:42PM, call, office OV04392
//	labyrum, 02/27/2020 03:04:53AM, call, office OV04392
//	mwwarstl, 12/13/2019 06:40:48AM, register, HL3 Code 691
//	mwwarstl, 02/22/2016 11:09:46PM, register, HL3 Code 691
//	mwwarstl, 02/27/2020 07:18:42AM, notify, HL6 Code 783		
//	mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691
//	labyrum, 02/19/2017 06:16:58PM, register, HL3 Code 691
//	mwwarstl, 01/04/2016 12:44:52PM,  register, HL3 Code 691
//	labyrum, 11/08/2016 10:43:29AM, register, HL3 Code 691
//	mwwarstl, 07/06/2015 04:17:06PM, register, HL3 Code 691
//	labyrum, 12/18/2017 03:02:54AM, register, HL3 Code 691
//	labyrum, 09/11/2016 09:14:44PM, register, HL3 Code 691	
//	labyrum, 04/15/2017 09:14:59PM, notify, HL6 Code 783
//	labyrum, 10/06/2016 04:58:44AM, register, HL3 Code 691
//	labyrum, 01/23/2017 12:05:22AM, register, HL3 Code 691
//	labyrum, 09/12/2023 00:00:15AM, unmerge, notification NX1115
//	labyrum, 01/24/2024 00:16:27AM, view, HL7 Code 422

	/**
	 * Test the getTopActivities method.
	 */
	@Test
	public void testGetTopActivities() {
		// The UserActivityLogManager Used for Tests
		UserActivityLogManager logManager;

		try {
			logManager = new UserActivityLogManager("input/records.txt");

			// Create list for sorted activities by frequency.
			DSAFactory.setListType(DataStructure.ARRAYBASEDLIST); // Specify your default list data structure type
			List<String> sortedFrequencyList = DSAFactory.getIndexedList();

			// Generate sorted frequency list. 0 activities reported.
			sortedFrequencyList = logManager.getTopActivities(0);
			assertTrue(sortedFrequencyList.isEmpty());
			assertEquals(0, sortedFrequencyList.size());

			// Create new list. Sort 3 activities
			sortedFrequencyList = DSAFactory.getIndexedList();
			sortedFrequencyList = logManager.getTopActivities(3);
			assertFalse(sortedFrequencyList.isEmpty());
			assertEquals(3, sortedFrequencyList.size());

			// Sorted list should be in following order (frequency: action resource)
			assertEquals("11: register HL3 Code 691", sortedFrequencyList.get(0));
			assertEquals("2: call office OV04392", sortedFrequencyList.get(1));
			assertEquals("2: notify HL6 Code 783", sortedFrequencyList.get(2));

			// Confirm all Entries appear in sorted list when number of entries given to
			// getTopActivities exceeds number of unique entries.
			sortedFrequencyList = DSAFactory.getIndexedList();
			sortedFrequencyList = logManager.getTopActivities(15);
			assertFalse(sortedFrequencyList.isEmpty());
			assertEquals(5, sortedFrequencyList.size());
			assertEquals("11: register HL3 Code 691", sortedFrequencyList.get(0));
			assertEquals("2: call office OV04392", sortedFrequencyList.get(1));
			assertEquals("2: notify HL6 Code 783", sortedFrequencyList.get(2));
			assertEquals("1: unmerge notification NX1115", sortedFrequencyList.get(3));
			assertEquals("1: view HL7 Code 422", sortedFrequencyList.get(4));

		} catch (FileNotFoundException e) {
			fail("File was not found or could not be read.");
		}
	}

	/**
	 * Test the getEntriesByDate method.
	 */
	@Test
	public void testGetEntriesByDate() {
		// The UserActivityLogManager Used for Tests
		UserActivityLogManager logManager;

		try {
			logManager = new UserActivityLogManager("input/records.txt");

			// Create Map of entries by date.
			Map<String, List<LogEntry>> dateMap = logManager.getEntriesByDate();
			assertNotNull(dateMap);
			assertNotEquals(0, dateMap.size());

			// There are 3 entries with matching date. The size of the map should condense
			// these into 1 list. (original input file is 17 entries. Date map should have
			// size of
			// 15 since 15,16,17 all have same date.
			// labyrum, 02/27/2020 03:04:53AM, call, office OV04392
			// mwwarstl, 02/27/2020 07:18:42AM, notify, HL6 Code 783
			// mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691
			assertEquals(15, dateMap.size());
			
			// Map is not sorted, but can confirm some values are there.
			assertEquals(3, dateMap.get("02/27/2020").size());	// 3 entries on 02/27/2020 date.
			assertEquals(null, dateMap.get("01/01/1952"));	// no entries had this date.

		} catch (FileNotFoundException e) {
			fail("File was not found or could not be read.");
		}
	}

	/**
	 * Test the getEntriesByHour method.
	 */
	@Test
	public void testGetEntriesByHour() {
		// The UserActivityLogManager Used for Tests
		UserActivityLogManager logManager;

		try {
			logManager = new UserActivityLogManager("input/records.txt");

			// Create Map of entries by hour.
			Map<Integer, List<LogEntry>> hourMap = logManager.getEntriesByHour();
			assertNotNull(hourMap);
			assertNotEquals(0, hourMap.size());

			// There are 5 entries with matching hour. The size of the map should condense
			// these into 1 list. (original input file is 17 entries. Hour Map should have
			// size of 14.)
			// mwwarstl, 12/13/2019 09:40:48PM, register, HL3 Code 691
			// labyrum, 09/11/2016 09:14:44PM, register, HL3 Code 691
			// labyrum, 04/15/2017 09:14:59PM, notify, HL6 Code 783
			//
			// and these 2 match at 5pm.
			// labyrum, 02/27/2020 05:30:50PM, call, office OV04392
			// mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691
			assertEquals(14, hourMap.size());
			
			// Map is not sorted, but can confirm some values are there.
			assertEquals(3, hourMap.get(21).size());	// 3 entries at 9pm.
			assertEquals(2, hourMap.get(17).size());	// 2 entries at 5pm.
			assertEquals(null, hourMap.get(8));	// no entries at hour 8.

		} catch (FileNotFoundException e) {
			fail("File was not found or could not be read.");
		}
	}
}