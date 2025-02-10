package edu.ncsu.csc316.dsa.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests the Activity class.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class ActivityTest {

	/**
	 * Test the compareTo method in Activity.
	 */
	@Test
	public void testCompareTo() {

		// Create Activity
		Activity one = new Activity(10, "Activity one");
		assertEquals(10, one.getFrequency());
		assertEquals("10: Activity one", one.getDescription());

		// Create Activity where frequency of "one" is larger than that of "two"
		Activity two = new Activity(2, "Activity two");

		assertEquals(-1, one.compareTo(two));

		// Create Activity where frequency of "one" is smaller than that of another.
		Activity three = new Activity(30, "Activity three");
		assertEquals(1, one.compareTo(three));

		// Confirm that when frequencies equal, order goes based on alphabetical
		// description.
		Activity sameFrequency = new Activity(10, "Same frequency"); // goes after one.
		assertTrue(one.compareTo(sameFrequency) < 0);
	}
}
