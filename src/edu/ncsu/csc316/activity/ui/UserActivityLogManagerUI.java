package edu.ncsu.csc316.activity.ui;

import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.ncsu.csc316.activity.manager.ReportManager;

/**
 * Program handles a user's data of log entries. User can choose to display
 * activities by highest frequency, activities on a certain date, and activities
 * on a certain hour.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class UserActivityLogManagerUI {

	/**
	 * Main method contains functionality for UI with the user. Prompts user for
	 * certain inputs. Inputs are checked for validity depending on the request.
	 * User has the option of generating a report of most frequent activities,
	 * activities by date, and activity by hour.
	 * 
	 * @param args for command line arguments NOT used.
	 */
	public static void main(String[] args) {

		// Setup Scanner to read input.
		Scanner scnr = new Scanner(System.in);

		// ReportManager used for generating reports.
		ReportManager reportManager;
		String fileName;

		// Continue prompts until user correctly enters file.
		while (true) {
			// Begin user prompts.
			System.out.print("Specify an input file that contains user activity log data: ");
			fileName = scnr.next();

			// Quit condition in 1st step.
			if ("Q".equalsIgnoreCase(fileName)) {
				scnr.close();
				System.exit(0);
			}

			// Try to create a new ReportManager using user filename.
			try {
				reportManager = new ReportManager(fileName);
				break;	// get out of this loop.
			} catch (FileNotFoundException e) {
				System.out.println("\nFile does not exist or cannot be read.");
			}
		}

		// Continue prompts until user quits.
		while (true) {
			System.out.print("\nPlease select method to generate output report: \n" + "F/f (Frequency of activity)\n"
					+ "D/d (Date of activity)\n" + "H/h (Hour of activity)\n" + "Q/q (Quit)\n");

			// Get user selection.
			String input = scnr.next();

			// Select report generation method based on user choice.
			// Frequency report - 
			if ("F".equalsIgnoreCase(input)) {
				System.out.print("Please specify how many activities should appear in the report:");
				// System.out.println(reportManager.getTopUserActivitiesReport(scnr.nextInt())); - from standard part 2.
				
				// Part 3: measure runtime 
				long startTime = System.currentTimeMillis();	// start time.
				//System.out.println(reportManager.getTopUserActivitiesReport(10000000));	// 10,000,000 rows.
				reportManager.getTopUserActivitiesReport(10000000);
				long endTime = System.currentTimeMillis();	// end time.
				long duration = endTime - startTime;	// duration of purely the getTopUserActivitiesReport(10000000).
				System.out.printf("\n\nRuntime for last frequency report on file with path \"%s\" was %d milliseconds.\n", fileName, duration);
			} 
			// Date report - 
			else if ("D".equalsIgnoreCase(input)) {
				System.out.println("\nPlease enter a valid date in the format MM/DD/YYYY");
				System.out.println(reportManager.getDateReport(scnr.next()));
			} 
			// Hour report - 
			else if ("H".equalsIgnoreCase(input)) {
				System.out.print("\nPlease enter a valid hour between 0 (12AM) and 23 (11PM)");
				System.out.println(reportManager.getHourReport(scnr.nextInt()));
			} 
			// User quits program -
			else if ("Q".equalsIgnoreCase(input)) {
				break;
			}
		}

		// Close scanner and end program.
		scnr.close();
		System.exit(0);
	}
}