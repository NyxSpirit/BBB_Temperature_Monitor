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
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import org.jfree.ui.RefineryUtilities;

public class Client implements Runnable {

	static Socket kkSocket = null;
	PrintWriter pw = null;
	static BufferedReader in = null;
	static BufferedWriter out = null;
	static Thread client = null;
	private static boolean closed;

	public static void main(String[] args) throws IOException {

		String ip = "131.191.106.216"; /// Put the ip InetAddress.getByName(ip)

		try {
			kkSocket = new Socket("localhost", 61223);
			in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(kkSocket.getOutputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: taranis.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: taranis.");
			System.exit(1);
		}

		sendFile(kkSocket);
		client = new Thread(new Client(), "Client");
		System.out.println("Created client thread");
		client.start();

		kkSocket.getOutputStream().flush();
		out.newLine();
		String input = "";

		// Opens chart
		RTGraph graph = new RTGraph("Real-Time Data Graph");
		graph.setPreferredSize(new Dimension(600, 500));
        graph.pack();
        RefineryUtilities.centerFrameOnScreen(graph);
        graph.setVisible(true);
        graph.start();
        
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

		// Stream is a holding tank, need to send the text, create a line, and then flush the stream
		// Can send data to the server by this method
		in.close();
		kkSocket.close();
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			InputStreamReader ir = new InputStreamReader(System.in);
			BufferedReader buff = new BufferedReader(ir);
			String input = "";
            
            // Loop for getting data from server
			while (!input.equals("disconnect")) {
				input = buff.readLine();
				System.out.println(input);
				out.write(input);
				out.newLine();
				out.flush();
			}
			//closed = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	public static void sendFile(Socket client) throws IOException {

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
}
