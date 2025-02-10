package edu.ncsu.csc316.activity.manager;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;

/**
 * Tests the ReportManager Class.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class ReportManagerTest {

	/*
	 * For clarity, input file looks like this:
	 */
//	USERNAME, TIMESTAMP, ACTION, RESOURCE
//	labyrum, 04/06/2013 07:30:42PM, call, office OV04392
//	labyrum, 11/02/2014 03:04:53AM, call, office OV04392
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
	 * Test the getTopUserActivitiesReport method.
	 */
	@Test
	public void testGetTopUserActivitiesReport() {
		// The UserActivityLogManager Used for Tests
		ReportManager reportManager;

		try {
			reportManager = new ReportManager("input/records.txt");

			// Confirm error message.
			assertEquals("Please enter a number > 0\n", reportManager.getTopUserActivitiesReport(-1));

			// Check report for number requested for report is < total unique frequencies.
			String report = reportManager.getTopUserActivitiesReport(3);
			assertEquals(
					"Top User Activities Report [\n   11: register HL3 Code 691\n   2: call office OV04392\n   2: notify HL6 Code 783\n]\n",
					report);

			// Confirm all Entries appear in report when number of entries given to
			// getTopActivities exceeds number of unique entries.
			report = reportManager.getTopUserActivitiesReport(15);
			assertEquals(
					"Top User Activities Report [\n   11: register HL3 Code 691\n   2: call office OV04392\n   2: notify HL6 Code 783\n   1: unmerge notification NX1115\n   1: view HL7 Code 422\n]\n",
					report);

		} catch (FileNotFoundException e) {
			fail("File was not found or could not be read.");
		}
	}

	/**
	 * Test the getDateReport method.
	 */
	@Test
	public void testGetDateReport() {
		// The UserActivityLogManager Used for Tests
		ReportManager reportManager;

		try {
			reportManager = new ReportManager("input/records.txt");

			// Test exception for formating
			assertEquals("Please enter a valid date in the format MM/DD/YYYY",
					reportManager.getDateReport("January 1, 1990"));

			// Create log with one date.
			assertEquals(
					"Activities recorded on 09/12/2023 [\n   labyrum, 09/12/2023 01:00:15AM, unmerge, notification NX1115\n]\n",
					reportManager.getDateReport("09/12/2023"));

			// Search for date that does not appear in list.
			assertEquals("No activities were recorded on 01/01/1990", reportManager.getDateReport("01/01/1990"));

			// Get date report with matching dates. Check proper order.
			// mwwarstl, 02/27/2020 07:18:42AM, notify, HL6 Code 783
			// labyrum, 02/27/2020 05:30:50PM, call, office OV04392
			// mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691
			assertEquals(
					"Activities recorded on 02/27/2020 [\n   mwwarstl, 02/27/2020 07:18:42AM, notify, HL6 Code 783\n   labyrum, 02/27/2020 05:30:50PM, call, office OV04392\n   mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691\n]\n",
					reportManager.getDateReport("02/27/2020"));

		} catch (FileNotFoundException e) {
			fail("File was not found or could not be read.");
		}
	}

	/**
	 * Test the getHourReport method.
	 */
	@Test
	public void testGetHourReport() {
		// The UserActivityLogManager Used for Tests
		ReportManager reportManager;

		try {
			reportManager = new ReportManager("input/records.txt");
			
			// Test invalid hours.
			assertEquals("Please enter a valid hour between 0 (12AM) and 23 (11PM)\n", reportManager.getHourReport(-5));
			assertEquals("Please enter a valid hour between 0 (12AM) and 23 (11PM)\n", reportManager.getHourReport(24));
			
			// Get hour report with hour not found (8am)
			assertEquals("No activities were recorded during hour 8", reportManager.getHourReport(8));

			// Get hour report with 1 activity (4pm)
			assertEquals(
					"Activities recorded during hour 16 [\n   mwwarstl, 07/06/2015 04:17:06PM, register, HL3 Code 691\n]\n",
					reportManager.getHourReport(16));

			// Get hour report with 3 activities (9pm) Confirm time ordering.
			// labyrum, 09/11/2016 09:14:44PM, register, HL3 Code 691
			// labyrum, 04/15/2017 09:14:59PM, notify, HL6 Code 783
			// mwwarstl, 12/13/2019 09:40:48PM, register, HL3 Code 691
			assertEquals(
					"Activities recorded during hour 21 [\n   labyrum, 09/11/2016 09:14:44PM, register, HL3 Code 691\n   labyrum, 04/15/2017 09:14:59PM, notify, HL6 Code 783\n   mwwarstl, 12/13/2019 09:40:48PM, register, HL3 Code 691\n]\n",
					reportManager.getHourReport(21));

			// Get hour report that involves alphabetical sorting (5pm).
			// labyrum, 02/27/2020 05:30:50PM, call, office OV04392
			// mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691
			assertEquals(
					"Activities recorded during hour 17 [\n   labyrum, 02/27/2020 05:30:50PM, call, office OV04392\n   mwwarstl, 02/27/2020 05:30:50PM, register, HL3 Code 691\n]\n",
					reportManager.getHourReport(17));

		} catch (FileNotFoundException e) {
			fail("File was not found or could not be read.");
		}
	}
}