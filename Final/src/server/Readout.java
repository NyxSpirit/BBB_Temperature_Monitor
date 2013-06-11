package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The temperature read from the sensor data must be associated with a date and time in order
 * for the temperature to have significance. The Readout object contains a String object that contains
 * the temperature data and associated date and time. The date and time is formatted using SimpleDateFormat
 * and the Calendar classes. The toString() method will return the properly formatted String object and should be
 * used to supply the queue that will be eventually wrote to the file.
 * <br><br>
 * <b>Example Output:</b>
 * <br>
 * &nbsp&nbsp&nbsp 70.94321664135062 100613_205319
 * 
 * @author Nick Ames
 *
 */
public class Readout {
	
	private double temp;
	
	private String readout;
	
	/**
	 * Creates a String object that contains the temperature data recorded as well as the 
	 * time and date of the recording. The temperature is truncated to three values and 
	 * the date is formatted <i>(INSERT HOW DATE IS FORMATTED HERE)</i>.
	 * @param temp - double
	 */
	public Readout(double temp) {
		this.setTemp(temp);
		setTime();
	}
	
	/**
	 * Sets the time associated with the recording.
	 */
	private void setTime() {
		String timeStamp = new SimpleDateFormat("ddMMyy_HHmmss").format(Calendar.getInstance().getTime());
		readout = temp + " " + timeStamp;
	}
	
	/**
	 * Returns the temperature stored in this instance of Readout.
	 * @return Temperature
	 */
	public double getTemp() {
		return temp;
	}
	
	/**
	 * Sets the temperature for formatting with a timestamp.
	 * @param temp - double
	 */
	public void setTemp(double temp) {
		this.temp = temp;
	}
	
	/**
	 * Returns the formatted temperature and date/time stamp for writing to the file.
	 */
	public String toString() {
		return readout;
	}

}
