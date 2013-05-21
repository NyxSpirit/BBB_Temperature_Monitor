package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server implements Runnable {

	private static int MAX_CLIENT_TIMEOUT = 20000;   // Timeout 20 seconds
	private static Queue<String> tempQueue = null;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private Host host = null;
	private static Thread server = null;
	private Thread hostThread = null;
	private boolean disconnected = false;

	private ExecutorService ex = null;  // Will be used for Heartbeat

	private InputStream in = null;      // Client's input data stream
	private OutputStream out = null;    // Client's output data stream
	private BufferedReader br = null;   // Buffered reader to allow easier reading through streams
	private BufferedWriter bf = null;   // same

	public static void main(String[] args) throws IOException {
		server = new Thread(new Server(), "Server-Run");
		server.run();
		System.out.println("closed");
	}


	// Send data to client, listen for updates, etc.
	public void run() {
		try {
			serverSocket = new ServerSocket(61223);
			while (true) {
				disconnected = false;
				System.out.println("Waiting for client"); //TODO: Remove later
				// Wait for a client to connect
				clientSocket = serverSocket.accept();
				System.out.println("Client Connected");
				// Set a max timeout for the read() operation
				clientSocket.setSoTimeout(MAX_CLIENT_TIMEOUT);

				// Create the concurrent queue
				tempQueue = new ConcurrentLinkedQueue<String>();

				// Set up the in/out streams
				in = clientSocket.getInputStream();
				out = clientSocket.getOutputStream();
				br = new BufferedReader(new InputStreamReader(in));
				bf = new BufferedWriter(new OutputStreamWriter(out));

				/*
				 * Create an executor that will check for client d/c
				 */
				ex = Executors.newSingleThreadExecutor();  // Creates the executor service that will control 1 thread
				Future<?> f = null;                        // The Future will attempt to get a null, will get the exception if d/c


				// Create a new host process
				host  = new Host(tempQueue, clientSocket);
				hostThread = new Thread(host, "Host-Process");

				// Will hold the commands from the client
				String command;

				/*
				 * The flow of data and thread execution on the particular host
				 */
				while (!disconnected) {
					/*
					 * Make sure client still connected
					 */
					try {
						f = ex.submit(new Heartbeat(bf));

						// The get() will attempt to get an object, but nothing returned.
						// This will ensure the exception from the Heartbeat Runnable is caught in Server
						f.get();

						// If the host process hasn't been started then start it
						if (!hostThread.isAlive()) {
							hostThread.start();
						}

						/*
						 * When the stream contains data then grab it and process it as a command
						 */
						while (br.ready()) {
							// Get the command
							command = br.readLine();
							System.out.println(command);

							// Do the command and remember to host.setReset(true) when updating sleep/reads/real-time
							//TODO: Write the proper command thing here, do it in a separate method easier handling

							/*
							 * Client sending the disconnect request to the server. 
							 * Must shut down the threads and ensure no data lost.
							 */
							if (command.equals("disconnect")) {
								disconnected = true;
								clientSocket.close();
								host.setStop(true);
								ex.shutdown();
								System.out.println("D/C");
							}
						}
						Thread.sleep(1000);

						/*
						 * Catching an overall Exception.
						 * Can do a more specific handling later.
						 * 
						 * This will catch the exception and end the processes
						 * in a safe fashion.
						 */
					} catch (Exception e) {
						disconnected = true;
						host.setStop(true);
						clientSocket.close();
						System.out.println("D/C");
						ex.shutdown();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
