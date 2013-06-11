package server;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Heart Beat to ensure client is connected to the server.
 * @author Nick Ames
 *
 */
public class Heartbeat implements Runnable {
	
	private BufferedWriter bw;  // Writes b to test if connected
	
	/**
	 * Create a new heart beat to test client connectivity
	 * @param bw
	 */
	public Heartbeat(BufferedWriter bw) {
		this.bw = bw;
	}
	
	public void run() {
		try {
			bw.write("b");
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
				throw new RuntimeException();
		}
	}
}
