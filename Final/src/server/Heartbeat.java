package server;

import java.io.BufferedWriter;
import java.io.IOException;

public class Heartbeat implements Runnable {
	
	private BufferedWriter bw;
	
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
