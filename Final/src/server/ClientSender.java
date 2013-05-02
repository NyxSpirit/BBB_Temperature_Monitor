package server;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * The ClientSender class is used to send real-time temperature data to the client
 * over a BufferedWriter connection. This data is read from the sensor process and
 * an Executor creates a new instance of ClientSender every time the sensor reads a new
 * value. The temperature value sent over the stream is a pure number and not a Readout object.
 * 
 * @author Nick Ames
 *
 */
public class ClientSender implements Runnable  {
	
	private final BufferedWriter bf;
	private final double data;
	
	/**
	 * Creates a new process that sends data to the client through a
	 * BufferedWriter connecting to the client.
	 * <br><br>
	 * This should be used with an executor service as the job is small
	 * and should be continually performed without altering currently running
	 * processes.
	 * @param bf - BufferedWriter (stream to the client)
	 * @param data - double (the temperature)
	 */
	public ClientSender(BufferedWriter bf, double data) {
		this.bf = bf;
		this.data = data;
	}
	
	/**
	 * Sends the data to the client.
	 */
	public void run()  {
		try {
			bf.write(String.valueOf(data));
			bf.newLine();
			bf.flush();
		} catch (IOException e1) {
			/*
			 * Handle if unable to write to stream
			 */
		}
	}
}
