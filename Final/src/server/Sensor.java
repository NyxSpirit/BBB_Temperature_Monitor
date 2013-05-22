package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Sensor implements Runnable {
	
	private final Queue<String> queue;  // Pointer to ConccurentLinkedQueue<String>
	private final ExecutorService ex;   // Used to create a worker thread to send client realtime data
	
	private final BufferedWriter bf; // For realtime temp data flow
	private int sleep;               // How often the sensor data is read
	private boolean realtime;        // If the sensor is in realtime mode
	private double temp;             // Holds the volatile temperature data
	
	private boolean stop;
	
	private boolean readError;       // Used for the sensor read, True if there is an error
	
	/**
	 * Creates a new Sensor Thread that will read data from the temperature sensor on the 
	 * Beagle Bone Black.
	 * <br><br>
	 * The sensor data will be saved into a ConccurentLinkedQueue in order to allow for
	 * easier management of data between the Sensor thread and the FileWriter Thread.
	 * <br><br>
	 * @param queue - The ConcurrentLinkedQueue<String> for temperature data
	 * @param bf - BufferedWriter on the output stream of the client socket
	 * @param sleep - Time in milliseconds for the sensor to read
	 * @param realtime - boolean if data should be directly sent to client or not
	 */
	public Sensor(Queue<String> queue, BufferedWriter bf, int sleep, boolean realtime) {
		this.bf = bf;
		this.sleep = sleep;
		this.realtime = realtime;
		this.readError = false;
		this.queue = queue;
		this.stop = false;
		this.ex = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * Method gets the sensor data as a temperature
	 */
	public void run() {
		while (!stop) {
			try {
				// Read the sensor data, put into queue, and send if realtime
				readSensor();
				Thread.sleep(sleep);
			} catch (InterruptedException e1) {
				/*
				 * handle 
				 */
			}
		}
		// Clean up the thread
		ex.shutdown();
		
		// Write remaining queue to file
		synchronized (queue) {
            queue.notifyAll();
        }
		
		System.out.println("Sensor stopped");
	}
	
	/**
	 * Reads the data from the temperature sensor.
	 * <br><br>
	 * The value obtained from the sensor is sent to a worker thread through
	 * the java executor service where it is sent to the client as a String.
	 * <br><br>
	 * The data is formatted as a String object and placed into a ConcurrentLinkedQueue.
	 * <br><br>
	 * Finally, a notifyAll() command is done on the queue, waking up the file writing thread
	 * to attempt to write to a file on the server side.
	 */
	private void readSensor() {
		
		int rangeMin = 68;
		int rangeMax = 74;
		// Get the data
		Random r = new Random();
		double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		
		// Send to client if real-time
		if (realtime) {
			ex.execute(new ClientSender(bf, randomValue));
		}
		
		// Format data with time-stamp
		String data = new DecimalFormat("0.00").format(randomValue);
		
		// Create proper readout and put it in the queue
		queue.add(new Readout(randomValue).toString());
		
		// Don't allow other threads to manipulate data while it is being written to file
		synchronized (queue) {
            queue.notifyAll();
        }	
	}

	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public boolean isRealtime() {
		return realtime;
	}

	public void setRealtime(boolean realtime) {
		this.realtime = realtime;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
