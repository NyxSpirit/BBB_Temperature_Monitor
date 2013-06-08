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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import javax.swing.JTextField;

import org.jfree.ui.RefineryUtilities;

public class ClientProcess implements Runnable {

	//private static final String IP = "131.191.106.216";

	private Socket kkSocket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private boolean closed;
	private boolean real;

	private String degree;
	
	private File inFile;

	// Pointers for interactions between components in other classes
	private JTextField txtTemp;

	private RTGraph graph;

	private String ip = "131.191.106.216"; /// Put the ip InetAddress.getByName(ip)
	private int port;
	private String dateTimeData;

	public ClientProcess(String ip, int port, JTextField txtTemp, String degree) {
		this.ip = ip;
		this.port = port;
		this.txtTemp = txtTemp;
		this.degree = degree;
		this.dateTimeData = "";
	}

	public void run() {
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
					//break;
				}
				
				if (input.equals("d")) {
					String dt = in.readLine();
					graphCustom(dt);
				} else if (input.equals("b")) {
					
				} else {
					try {
						float test = Float.parseFloat(input);

						// Check for degree
						if (getDegree().equals("C")) {
							test = convertToC(test);
						}
						String data = new DecimalFormat("0.00").format(test);
						txtTemp.setText(data);
						graph.setTempValue(Float.parseFloat(data));
						//System.out.println(input);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
			disconnect();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {

		}
	}

	private float convertToC(float test) {
		return (float) ((test - 32.0) * (5.0 / 9.0));
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
		kkSocket.shutdownOutput();
		kkSocket.shutdownInput();
		kkSocket.close();
	}

	public void sendFile(Socket client) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileInputStream fis = null;

		try {
			File file = getInFile();
			byte[] bArray = new byte[(int) file.length()];
			fis = null;
			fis = new FileInputStream(file);
			// Sending a file
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(client.getOutputStream());

			// Send the file size
			System.out.println((int) file.length());
			bos.write((int) file.length());
			bos.flush();


			// If the server is ready for the file
			if (in.readLine().equals("1")) {
				System.out.println("1");

				bis.read(bArray, 0, bArray.length);
				bos.write(bArray, 0, bArray.length);
				bos.flush();
			} else {
				// Server refused file
			}
		} catch (IOException ex) {
			// Do exception handling
		} finally {
			bis.close();
			fis.close();
		}
	}

	public boolean isReal() {
		return real;
	}

	public void setReal(boolean real) {
		graph.setVisible(real);
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public void getTempHistory(int[] times) throws IOException, InterruptedException {
		
		String dateTime = "";
		for (int i = 0; i < times.length; i++) {
			dateTime += String.valueOf(times[i]) + ",";
		}
		System.out.println(dateTime);
		out.write("getData");
		out.newLine();
		out.flush();
		out.write(dateTime);
		out.newLine();
		out.flush();
	}
	
	private void setDateTimeData(String s) {
		this.dateTimeData = s;
	}
	
	public String getDateTimeData() {
		return this.dateTimeData;
	}
	
	public void graphCustom(String data) {
		System.out.println(data);
		CustomViewGraph cGraph = new CustomViewGraph(null);
		cGraph.pack();
		cGraph.setVisible(true);
	}

	public File getInFile() {
		return inFile;
	}

	public void setInFile(File inFile) {
		this.inFile = inFile;
	}
	
	public void setSensorSleep(long rate) {
		try {
			out.write("sr");
			out.newLine();
			out.flush();
			out.write(String.valueOf(rate));
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
