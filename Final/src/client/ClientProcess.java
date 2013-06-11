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

/**
 * The ClientProcess class is a Runnable class that continually interacts with the server over a socket connection.
 * <br><br>
 * The ClientProcess creates a connection to the server, sends configuration data, receives real-time temperature data, and acquires historical data from the server.
 * 
 * @author Nick Ames
 *
 */
public class ClientProcess implements Runnable {

	private Socket serverSocket = null;     // Socket for connection to the server
	private BufferedReader in = null;       // Allows easy reading over socket input stream
	private BufferedWriter out = null;      // Allows easy writing to socket output stream
	private boolean closed;                 // Tests whether the connection to the server has been closed
	private boolean real;                   // Tests whether data should be real-time over streams (sent to server)

	private String degree;  // The degree to convert temperature values to (sent as F)
	private File inFile;    // The file to be sent to the server (config file)

	private JTextField txtTemp;   // Easy pointer to realPanel's JTextField
	private RTGraph graph;        // The real-time dynamic graph object
	
	private String ip;            // IP Address of the server
	private int port;             // The port number to connect to the server
	private String dateTimeData;  // The dateTimeData to be sent/received from server ***IN DEVELOPMENT
	
	/**
	 * Creates a new ClientProcess.
	 * <br><br>
	 * The ClientProcess will maintain and control interaction between the client and the server over a port number and ip address.
	 * It will also interact with the realtime panel to allow easy real-time data to be shown.
	 * @param ip - IP Address of the server as a String
	 * @param port - Port number of the server (int)
	 * @param txtTemp - JTextField pointer to realPanel
	 * @param degree - The degree to begin reading data (String)
	 */
	public ClientProcess(String ip, int port, JTextField txtTemp, String degree) {
		this.ip = ip;
		this.port = port;
		this.txtTemp = txtTemp;
		this.degree = degree;
		this.dateTimeData = "";
	}
	
	/**
	 * The continual process that allows interaction and real-time data between client and server.
	 */
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
				
				// If the server sends historical date/time data
				if (input.equals("d")) {
					String dt = in.readLine();
					graphCustom(dt);
					
					// If heartbeat, ignore, server is ensuring connection
				} else if (input.equals("b")) {
				} else {
					
					// If no commands, must be real-time data
					try {
						float test = Float.parseFloat(input);

						// Check for degree
						if (getDegree().equals("C")) {
							test = convertToC(test);
						}
						
						// Set the JTextField on the realPanel to real-time data
						String data = new DecimalFormat("0.00").format(test);
						txtTemp.setText(data);
						graph.setTempValue(Float.parseFloat(data));
					} catch (Exception e) {
					}
				}
			}
			
			// If connection end, begin disconnect process
			disconnect();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Converts F to C.
	 * @param test - Degree in fahrenheit
	 * @return - Degrees in Celsius
	 */
	private float convertToC(float test) {
		return (float) ((test - 32.0) * (5.0 / 9.0));
	}
	
	/**
	 * Attempts to connect to the server through the given IP Address and Port Number.
	 * <br><br>
	 * Once connected, the configuration file is sent.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException {
		serverSocket = new Socket(InetAddress.getByName(ip), port);
		in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
		sendFile(serverSocket);
		serverSocket.getOutputStream().flush();
		this.closed = false;
	}
	
	/**
	 * Disconnects from the server in a safe way, terminating the thread and closing the socket.
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		// Stream is a holding tank, need to send the text, create a line, and then flush the stream
		// Can send data to the server by this method
		this.closed = true;
		serverSocket.shutdownOutput();
		serverSocket.shutdownInput();
		serverSocket.close();
	}
	
	/**
	 * Sends the configuration file to the server as a byte array.
	 * @param client - The socket currently connected to the server
	 * @throws IOException
	 */
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
			bos.write((int) file.length());
			bos.flush();

			// If the server is ready for the file
			if (in.readLine().equals("1")) {
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
	
	/**
	 * Tests if the data should be real-time from the server.
	 * @return - true if data is being read real-time
	 */
	public boolean isReal() {
		return real;
	}

	/**
	 * Sets the real-time read (sent to server)
	 * @param real - boolean
	 */
	public void setReal(boolean real) {
		graph.setVisible(real);
	}

	/**
	 * The degree is either F or C
	 * @return - Current degree as a String
	 */
	public String getDegree() {
		return degree;
	}

	/**
	 * Sets the measurement for degrees
	 * @param degree - String (F or C)
	 */
	public void setDegree(String degree) {
		this.degree = degree;
	}
	
	// IN DEVELOPMENT
	public void getTempHistory(int[] times) throws IOException, InterruptedException {
		
		String dateTime = "";
		for (int i = 0; i < times.length; i++) {
			dateTime += String.valueOf(times[i]) + ",";
		}
		//System.out.println(dateTime);
		out.write("Request Temp History");
		out.newLine();
		out.flush();
		out.write(dateTime);
		out.newLine();
		out.flush();
	}
	
	// IN DEVELOPMENT
	@SuppressWarnings("unused")
	private void setDateTimeData(String s) {
		this.dateTimeData = s;
	}
	
	// IN DEVELOPMENT
	public String getDateTimeData() {
		return this.dateTimeData;
	}
	
	// IN DEVELOPMENT
	public void graphCustom(String data) {
		//System.out.println(data);
		CustomViewGraph cGraph = new CustomViewGraph(null);
		cGraph.pack();
		cGraph.setVisible(true);
	}

	/**
	 * Gets the File object that holds the configuration file.
	 * @return - File
	 */
	public File getInFile() {
		return inFile;
	}

	/**
	 * Set the configuration file to be sent to the server.
	 * @param inFile - File
	 */
	public void setInFile(File inFile) {
		this.inFile = inFile;
	}
	
	/**
	 * Set the rate at which the sensor should read on the server.
	 * @param rate - sleep in milliseconds
	 */
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
