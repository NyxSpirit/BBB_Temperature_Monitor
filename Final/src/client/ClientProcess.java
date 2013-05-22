package client;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import org.jfree.ui.RefineryUtilities;

public class ClientProcess implements Runnable {

	private static final String IP = "131.191.106.216";

	private Socket kkSocket = null;
	private PrintWriter pw = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private Thread client = null;
	private boolean closed;
	private boolean real;
	
	private RTGraph graph;

	private String ip = "131.191.106.216"; /// Put the ip InetAddress.getByName(ip)
	private int port;

	public ClientProcess(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			out.newLine();
			String input = "";

			// Opens chart
			graph = new RTGraph("Real-Time Data Graph");
			graph.setPreferredSize(new Dimension(600, 500));
			graph.setDefaultCloseOperation(RTGraph.HIDE_ON_CLOSE);
			graph.pack();
			RefineryUtilities.centerFrameOnScreen(graph);
			graph.setVisible(false);
			graph.start();

			// Loop for getting data from server
			while (!closed) {
				input = in.readLine();
				
				if (input == null) {
					break;
				}

				if (input.equals("b")) {



				} else {
					try {
						float test = Float.parseFloat(input);
						String data = new DecimalFormat("0.00").format(test);
						graph.setTempValue(Float.parseFloat(data));
						System.out.println(input);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
			disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	public void connect() throws UnknownHostException, IOException {
		kkSocket = new Socket(InetAddress.getByName(ip), port);
		in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(kkSocket.getOutputStream()));
		sendFile(kkSocket);
		kkSocket.getOutputStream().flush();
		this.closed = false;
	}

	public void disconnect() throws IOException {
		// Stream is a holding tank, need to send the text, create a line, and then flush the stream
		// Can send data to the server by this method
		this.closed = true;
		in.close();
		kkSocket.close();
	}

	public void sendFile(Socket client) throws IOException {

		String iniFile = "temp.ini";
		File file = new File(iniFile);
		byte[] bArray = new byte[(int) file.length()];
		FileInputStream fis = null;
		fis = new FileInputStream(file);
		// Sending a file
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());

		// Send the file size
		System.out.println((int) file.length());
		bos.write((int) file.length());
		bos.flush();

		if (in.readLine().equals("1")) {
			System.out.println("1");
			try {
				bis.read(bArray, 0, bArray.length);
				bos.write(bArray, 0, bArray.length);
				bos.flush();

				// File sent
			} catch (IOException ex) {
				// Do exception handling
			}
		}
	}

	public boolean isReal() {
		return real;
	}

	public void setReal(boolean real) {
		graph.setVisible(real);
	}
	
	
}
