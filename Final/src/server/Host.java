package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Queue;


public class Host implements Runnable  {

	private final static String READY_TO_RECEIVE = "1";

	private final Queue<String> queue;  // Holds the temp data
	private final Socket clientSocket;  // Socket for the client connection

	private boolean reset;              // If client changes config data

	private InputStream in = null;      // Client's input data stream
	private OutputStream out = null;    // Client's output data stream
	private BufferedReader br = null;   // Buffered reader to allow easier reading through streams
	private BufferedWriter bf = null;   // same

	private int sleep;
	private boolean realtime;

	private boolean stop;      // Used to stop thread execution
	private boolean clientDC;  // True if unable to write to client

	// Classes to be made into threads
	private Sensor se = null;
	private FileWrite fw = null;

	// Threads for reading, write to file, etc.
	private Thread sensor = null;
	private Thread writer = null;

	/**
	 * Creates a new Host. The host is responsible for controlling the 
	 * flow of the sensor and file writing threads. Once connected to the client,
	 * the host creates 2 worker threads: sensor and file writer. 
	 * These threads read the sensor data and either write them to a file or send them to the socket.
	 * <br><br>
	 * The host thread will continually check for updates such as readTime changes,
	 * real-time versus file retrieval, etc.
	 * @param tempQueue - The concurrent queue for holding temp data
	 * @param clientSocket - The socketed connection to the client
	 * @throws IOException If the connection was not made to the client
	 */
	public Host(Queue<String> tempQueue, Socket clientSocket) throws IOException {
		this.queue = tempQueue;
		this.clientSocket = clientSocket;
		initialize();
	}

	/**
	 * Controls the flow of the threads.
	 * <br><br>
	 * Starts the sensor thread and the file write threads while continually checking
	 * for updates and/or disconnect/resets.
	 * <br><br>
	 * These two threads are dependent upon each other since they both
	 * work with the queue that holds the temperature data.
	 */
	public void run() {
		try {
			sensor.start();
			writer.start();
			while (!stop) {

				// Check if client has changed things (realtime, file access)
				if (reset) {
					se.setRealtime(realtime);
					se.setSleep(sleep);
				}
				Thread.sleep(500);
			}
		} catch (InterruptedException e1) {
			sensor.interrupt();
			writer.interrupt();
		}
		System.out.println("Host stopped");
		// Stop the threads
		fw.setStop(true);
		se.setStop(true);
	}

	/**
	 * Initializes the host process and begins the execution of the worker threads.
	 * <br><br>
	 * The two worker threads involve reading sensor data and writing that data to a file.
	 * <br><br>
	 * Streams are opened to the client in order to send real-time data.
	 * @throws IOException If unable to open streams to client
	 */
	private void initialize() throws IOException {

		// Open and set the streams to the client
		in = clientSocket.getInputStream();
		out = clientSocket.getOutputStream();
		br = new BufferedReader(new InputStreamReader(in));   // Converts bytes to characters
		bf = new BufferedWriter(new OutputStreamWriter(out)); // Writing to client

		// For file handling
		byte[] aByte = new byte[1];
		int bytesRead;
		String outFile = "tempout.ini";

		// Get the config values
		// Parse the file and use appropriate values

		// Send ready to client for config data
		bf.write(READY_TO_RECEIVE);
		bf.newLine();
		bf.flush();
		
		int fileSize = in.read();
		
		// Get the file from the client
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			fos = new FileOutputStream(outFile);
			bos = new BufferedOutputStream(fos);
			bytesRead = in.read(aByte, 0, aByte.length);
			int count = 1;
			do {
				baos.write(aByte);
				bytesRead = in.read(aByte);
				count++;
			} while (count < fileSize);

			bos.write(baos.toByteArray());
			bos.flush();
			bos.close();
			System.out.println("Wrote File");
		} catch (IOException ex) {
			// Do exception handling
		}

		// TODO: Test Values Remove Later
		sleep = 1000;
		realtime = true;

		// Create the instance of the sensor
		se = new Sensor(queue, bf, sleep, realtime);
		sensor = new Thread(se, "Sensor");

		// Create the instance of the file writer
		fw = new FileWrite(queue);
		writer = new Thread(fw, "File Writer");

		this.stop = false;
	}

	/**
	 * The value of reset is tested to ensure updates take effect.
	 * <br><br>
	 * Set <b>true</b> if updates have occured.
	 * @param reset - boolean
	 */
	public void setReset(boolean reset) {
		this.reset = reset;
	}

	/**
	 * Returns the flag indicating if the host has been reset.
	 * @return Reset flag of host
	 */
	public boolean isReset(){
		return reset;
	}

	/**
	 * Returns the sleep time associated with sensor reading.
	 * @return Sleep time in milliseconds
	 */
	public int getSleep() {
		return sleep;
	}

	/**
	 * Set the sleep time in milliseconds for the sensor reading.
	 * @param sleep - int
	 */
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	/**
	 * Returns <br>true</b> if data is read from the sensor and sent to the client
	 * in a real-time format.
	 * @return Real-time flag
	 */
	public boolean isRealtime() {
		return realtime;
	}

	/**
	 * Sets the sensor to real-time, allowing data to be sent directly to the client
	 * as it is read.
	 * @param realtime - boolean
	 */
	public void setRealtime(boolean realtime) {
		this.realtime = realtime;
	}

	/**
	 * Returns the sensor data as a Queue object containing Strings.
	 * @return Queue
	 */
	public Queue<String> getQueue() {
		return queue;
	}

	/**
	 * Returns <b>true</b> if the host has been stopped.
	 * @return boolean
	 */
	public boolean isStop() {
		return stop;
	}

	/**
	 * Stops the execution of the thread and worker threads.
	 * @param stop - boolean
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}
}