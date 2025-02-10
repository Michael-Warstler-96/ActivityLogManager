package edu.ncsu.csc316.dsa.data;

/**
 * Class is a simplified version of a LogEntry. An Activity contains a frequency
 * of appearances in a LogEntry List. It also contains a unique String
 * identifier/description. Activity's compareTo method is used for sorting via
 * frequency and alphabetically.
 * 
 * @author Michael Warstler (mwwarstl)
 */
public class Activity implements Comparable<Activity> {

	/** Frequency of a particular Activity */
	private int frequency;
	/** Activity description in format of "frequency: action resource\n" */
	private String description;

	/**
	 * Constructs an Activity object. Similar to a LogEntry but contains information
	 * on frequency of appearances in the LogEntry list. Also contains a unique
	 * description built from the LogEntry's action and resource.
	 * 
	 * @param freq        is frequency of Activity in a LogEntry list.
	 * @param description is LogEntry's action + resource together in unique String.
	 */
	public Activity(int freq, String description) {
		frequency = freq;
		StringBuilder builder = new StringBuilder(String.valueOf(frequency));
		builder.append(": ");
		builder.append(description);
		setDescription(builder.toString());
	}

	/**
	 * Gets the frequency field.
	 * 
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Gets the description field.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description field.
	 * 
	 * @param description is description string of activity.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Used in NaturalOrder-->Compare to compare 2 Activities frequency and
	 * description. Used to sort Activities in descending frequency and ascending
	 * alphabetical description.
	 * 
	 * @param other is other Activity to compare to this one.
	 * @return is -1 if this Activity comes before the other, 0 if they are equal,
	 *         and 1 if this comes after the other.
	 */
	@Override
	public int compareTo(Activity other) {
		// If frequencies match
		if (frequency == other.getFrequency()) {
			// Utilize standard String.compareTo to sort alphabetically.
			return description.compareToIgnoreCase(other.getDescription()); 
		}
		// Otherwise, frequencies don't match
		else {
			// Return -1 if this frequency is bigger than other, and should be ordered
			// first.
			return frequency > other.getFrequency() ? -1 : 1;
		}
	}
}