package client;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Main class for the client side of the T.R.U. Temperature Reading Unit.
 * <br><br>
 * The client side is responsible for connecting to the server over a socket using either local network or the internet.
 * <br><br>
 * The Server side of the T.R.U. needs to have an open port in order for the client to connect.
 * <br><br>
 * The client uses a GUI environment to allow the user to interact with the server and see data in graphical and textual means.
 * 
 * @author Nick Ames
 *
 */
public class Client {

	private static final Rectangle DIM = new Rectangle(0, 21, 260, 199);  // Dimensions for the panels

	private JButton btnMain1;      // JButtons to interact with panels
	private JButton btnCustHome;   // ''
	private JButton btnCustGraph;  // ''

	private Thread client;         // The client will be on a thread in order to receive data and manipulate graphs

	private JFrame frmTruClient;   // The frame for the GUI
	private ClientProcess cp;      // The process that will interact with the server over the socket
	private JPanel realPanel;      // The real-time data panel

	private SettingPanel settingPanel;    // The panel that contains the setting and config info
	private HelpPanel helpPanel;          // Panel containing the help contents
	private CustomViewPanel customPanel;  // Panel for creating a custom graph
	
	private String degree;         // The temp in degrees (F or C)
	
	private String ipAddress;      // IP Address for the server
	private int portNum;           // Port number of the server
	
	private long sensorSleepRate;  // The rate at which the data should be read (sent to server)
	
	/**
	 * Main class for client. Start the GUI and allow user to interact.
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frmTruClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creates a new instance of a Client class. Sets up a graphical environment and the ability to connect to a server.
	 */
	public Client() {
		initialize();
	}

	/**
	 * Initializes the components and actions for the components of the GUI.
	 */
	private void initialize() {
		
		///////////////////////////////////////////////////////////////////////
		// Application Frame
		///////////////////////////////////////////////////////////////////////
		
		frmTruClient = new JFrame();
		frmTruClient.setResizable(false);
		frmTruClient.setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource("/org/jfree/chart/gorilla.jpg")));
		frmTruClient.setTitle("T.R.U. Client | V0.1.0");
		frmTruClient.setBounds(100, 100, 266, 248);
		frmTruClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTruClient.getContentPane().setLayout(null);
		
		///////////////////////////////////////////////////////////////////////
		// Main Panel
		///////////////////////////////////////////////////////////////////////
		
		final JPanel panel = new JPanel();
		panel.setBounds(0, 21, 260, 199);
		frmTruClient.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Status :");
		lblNewLabel_1.setBounds(101, 31, 52, 14);
		panel.add(lblNewLabel_1);
		
		final JButton btnCustom = new JButton("Custom View");
		btnCustom.setEnabled(false);
		
		final JButton btnConnect = new JButton("Connect");
		final JButton btnDisconnect = new JButton("Disconnect");
		final JButton btnReal = new JButton("Real-Time");

		///////////////////////////////////////////////////////////////////////
		// Settings
		///////////////////////////////////////////////////////////////////////
		
		settingPanel = new SettingPanel();
		settingPanel.setVisible(false);
		frmTruClient.getContentPane().add(settingPanel);

		JLabel lblNewLabel1 = new JLabel("T.R.U.");
		lblNewLabel1.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel1.setBounds(10, 57, 81, 80);
		panel.add(lblNewLabel1);

		final JLabel lblStatus = new JLabel("Disconnected");
		lblStatus.setForeground(Color.RED);
		lblStatus.setBounds(146, 31, 81, 14);
		panel.add(lblStatus);
		
		JButton btnHome = new JButton("Home");
		btnHome.setBounds(101, 170, 68, 27);
		settingPanel.add(btnHome);
		
		this.ipAddress = settingPanel.getIpAddress();
		this.portNum = settingPanel.getPortNum();
		this.setDegree(settingPanel.getDegree());
		
		///////////////////////////////////////////////////////////////////
		// Real Time Panel 
		///////////////////////////////////////////////////////////////////
		
		realPanel = new JPanel();
		realPanel.setBounds(DIM);
		realPanel.setVisible(false);
		realPanel.setLayout(null);
		frmTruClient.getContentPane().add(realPanel);

		final JLabel lblTemp = new JLabel("Current Temperature");
		lblTemp.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTemp.setHorizontalAlignment(SwingConstants.CENTER);
		lblTemp.setBounds(10, 11, 230, 49);
		realPanel.add(lblTemp);

		final JTextField txtTemp = new JTextField();
		txtTemp.setBounds(71, 71, 70, 20);
		txtTemp.setFont(new Font(settingPanel.getFontName(), Font.PLAIN, settingPanel.getFontSize()));
		txtTemp.setEditable(false);
		realPanel.add(txtTemp);
		txtTemp.setColumns(10);

		final JLabel lblTempD = new JLabel("F");
		lblTempD.setBounds(151, 71, 20, 14);
		realPanel.add(lblTempD);
		
		/**
		 * Return to home from the Real-Time panel
		 */
		btnMain1 = new JButton("Main Menu");
		btnMain1.addActionListener(new ActionListener() {
			// Show other JPanel
			public void actionPerformed(ActionEvent e) {
				realPanel.setVisible(false);
				panel.setVisible(true);
			}
		});
		btnMain1.setBounds(71, 138, 100, 23);
		realPanel.add(btnMain1);
		
		/**
		 * Graph the Real-Time data from the realTime panel
		 */
		JButton btnRealGraph = new JButton("Graph");
		btnRealGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cp.setReal(true);
			}
		});
		btnRealGraph.setBounds(71, 108, 100, 23);
		realPanel.add(btnRealGraph);

		///////////////////////////////////////////////////////////////////////
		// Custom View Panel
		///////////////////////////////////////////////////////////////////////
		customPanel = new CustomViewPanel();

		btnCustHome = new JButton("Home");
		btnCustHome.setBounds(146, 148, 94, 23);
		customPanel.add(btnCustHome);

		btnCustGraph = new JButton("Graph");
		btnCustGraph.setBounds(10, 148, 121, 23);
		customPanel.add(btnCustGraph);

		customPanel.setVisible(false);
		frmTruClient.getContentPane().add(customPanel);

		
		///////////////////////////////////////////////////////////////////////
		// Action Listeners
		///////////////////////////////////////////////////////////////////////
		
		/**
		 * Connect to the server.
		 */
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// If the client is not connected, connect to the server
				if (btnConnect.getText().equals("Connect")) {
					
					// Create a new client process that will interact with the server (opens the connection, gets data, etc.)
					cp = new ClientProcess(settingPanel.getIpAddress(), getPortNum(), txtTemp, settingPanel.getDegree());
					client = new Thread(cp);
					
					// Setup the settings file to be transferred
					File file = createSettingsFile();
					cp.setInFile(file);
					
					// Try to connect to the server
					try {
						frmTruClient.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						cp.connect();
						client.start();
						btnConnect.setVisible(false);
						btnDisconnect.setVisible(true);
						lblStatus.setForeground(Color.GREEN);
						lblStatus.setText("Connected");
						btnReal.setEnabled(true);
						btnCustom.setEnabled(true);
						frmTruClient.repaint();
					} catch (Exception e) {
						if (e instanceof UnknownHostException || e instanceof IOException){
							JOptionPane.showMessageDialog(null, "Unable to connect to the host server. Ensure the server is available and the I.P. Address and port number are correct.");
						}
					} finally {
						frmTruClient.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		});
		btnConnect.setBounds(101, 56, 116, 23);
		panel.add(btnConnect);
		
		/**
		 * Disconnect from the server.
		 */
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					frmTruClient.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					cp.disconnect();
					btnDisconnect.setVisible(false);
					btnConnect.setVisible(true);
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Disconnected");
					btnReal.setEnabled(false);
					btnCustom.setEnabled(false);
					frmTruClient.repaint();
				} catch (Exception e) {
				} finally {
					frmTruClient.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		btnDisconnect.setBounds(101, 56, 116, 23);
		btnDisconnect.setVisible(false);
		panel.add(btnDisconnect);
		
		/**
		 * Change to the realTime panel from the homePanel
		 */
		btnReal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//cp.setReal(true);  FOR THE GRAPH
				panel.setVisible(false);
				realPanel.setVisible(true);
			}
		});
		btnReal.setEnabled(false);
		btnReal.setBounds(101, 90, 116, 23);
		panel.add(btnReal);
		
		/**
		 * Go back to the home panel from the customView panel
		 */
		btnCustHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(true);
				customPanel.setVisible(false);
			}
		});
		
		/**
		 * Go to the customView panel from the home panel
		 */
		btnCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				customPanel.setVisible(true);
				panel.setVisible(false);
			}
		});
		btnCustom.setBounds(101, 121, 116, 23);
		panel.add(btnCustom);
		
		/**
		 * Exit the program through a button on the home screen. Calls the disconnect method if connected to the server.
		 */
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnDisconnect.isVisible()){
					try {
						cp.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
		btnExit.setBounds(101, 155, 116, 23);
		panel.add(btnExit);
		
		/**
		 * Return home from settings. This will change the server, real-time data, and server read rate.
		 */
		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(true);
				settingPanel.setVisible(false);
				
				// Adjust Font Real-Time
				txtTemp.setFont(new Font(settingPanel.getFontName(), Font.PLAIN, settingPanel.getFontSize()));
				lblTempD.setFont(new Font(settingPanel.getFontName(), Font.PLAIN, settingPanel.getFontSize()));
				
				// Change the readout of the temp data
				lblTempD.setText(settingPanel.getDegree());
				
				// Tell client process to convert data received
				setDegree(settingPanel.getDegree());
				if (cp != null) {
					cp.setDegree(getDegree());
				}
				
				// Set the IP Address and port number
				if (!(ipAddress.equals(settingPanel.getIpAddress())) || portNum != settingPanel.getPortNum()) {
					setIpAddress(settingPanel.getIpAddress());
					setPortNum(settingPanel.getPortNum());
					JOptionPane.showMessageDialog(null, "Please disconnect from the server to apply port number or IP Address change.");
				}
				
				// Set the sleep time for the sensor on the server
				if (!(getSensorSleepRate() == settingPanel.getDataWriteRate())) {
					setSensorSleepRate(settingPanel.getDataWriteRate());
					cp.setSensorSleep(getSensorSleepRate());
				}
			}
		});
		
		/**
		 * Create a custom graph from temp history on the server. Gathers info for history from user then sends to server.
		 * It then receives that data and creates a new custom graph.
		 */
		btnCustGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// Holds the times the user selected
				final int[] times = customPanel.getDates();
				
				// Ensure the times are valid, then send to cp to get history
				if (validDates(times)) {
					try {
						cp.getTempHistory(times);
						cp.getDateTimeData();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				} else {
					JOptionPane.showMessageDialog(null, "Please select valid to/from dates and times in order to graph.");
				}
			}
		});
		
		////////////////////////////////////////////////////////////////////
		// Persistent Menu Bar
		////////////////////////////////////////////////////////////////////

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 260, 21);
		frmTruClient.getContentPane().add(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		/**
		 * Exit the program.
		 */
		JMenuItem mExit = new JMenuItem("Exit");
		mExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnDisconnect.isVisible()){
					try {
						cp.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
		mnNewMenu.add(mExit);

		JMenu mnNewMenu_1 = new JMenu("Client");
		menuBar.add(mnNewMenu_1);
		
		/**
		 * Opens the Settings page where the user can adjust certain settings.
		 */
		JMenuItem mPref = new JMenuItem("Settings");
		mPref.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingPanel.setVisible(true);
				panel.setVisible(false);
				realPanel.setVisible(false);
			}
		});
		mnNewMenu_1.add(mPref);

		JMenu mnNewMenu_2 = new JMenu("Help");
		menuBar.add(mnNewMenu_2);
		
		/**
		 * Opens the help frame.
		 */
		JMenuItem mHow = new JMenuItem("How To");
		mHow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				helpPanel = new HelpPanel();
				helpPanel.fileSetup();
				helpPanel.setPreferredSize(new Dimension(460, 395));
				helpPanel.pack();
				helpPanel.setVisible(true);
			}
		});
		mnNewMenu_2.add(mHow);
		
		/**
		 * Shows the current version and authors.
		 */
		JMenuItem mAbout = new JMenuItem("About");
		mAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmTruClient, "Temperature Reading Unit - Client Version 0.1.0" + System.lineSeparator() 
						+ "Nicholas Ames" + System.lineSeparator() + "Bryan Johnston");
			}
		});
		mnNewMenu_2.add(mAbout);
		
		setSensorSleepRate(settingPanel.getDataWriteRate());
	}
	
	/**
	 * Creates a configuration file to be sent to the server. The configuration file will tell the server at what rate to read data.
	 * @return File - the file to be sent to the server
	 */
	protected File createSettingsFile() {
		File inFile = new File("config.ini");
		FileWriter fo = null;
		BufferedWriter bw = null;
		
		// Get the write rate
		setSensorSleepRate(settingPanel.getDataWriteRate());
		
		try {
			fo = new FileWriter(inFile);
			bw = new BufferedWriter(fo);
			
			// write the settings
			bw.write("+realtime:true");
			bw.newLine();
			bw.flush();
			bw.write("+sleep:"+settingPanel.getDataWriteRate());
			bw.newLine();
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return inFile;
	}
	
	/**
	 * Checks the user selection of valid dates for a custom graph.
	 * <br><br>
	 * i.e. The user cannot specify a beginning date/time greater than the ending date/time.
	 * <br><br>
	 * The array is in the order:
	 * <ul>
	 * <li>Start Hour</li>
	 * <li>Start Minute</li>
	 * <li>End Hour</li>
	 * <li>End Minute</li>
	 * <li>Start Day</li>
	 * <li>Start Year</li>
	 * <li>End Day</li>
	 * <li>End Year</li>
	 * </ul>
	 * @param times - integer array containing the dates and times
	 * @return - <b>true</b> if valid date/time
	 */
	protected boolean validDates(final int[] times) {
		
		int startHour, startMin, endHour, endMin, startDay, startYear, endDay, endYear;
		
		startHour = times[0];
		startMin = times[1];
		endHour = times[2];
		endMin = times[3];
		startDay = times[4];
		startYear = times[5];
		endDay = times[6];
		endYear = times[7];
		
		if (endYear >= startYear) {
			if (endDay >= startDay) {
				if (endHour >= startHour) {
					if (endMin >= startMin) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the current degree (F or C)
	 * @return - degree as a String
	 */
	public String getDegree() {
		return degree;
	}
	
	/**
	 * Sets the current degree measure (F or C)
	 * @param degree - String (F or C)
	 */
	public void setDegree(String degree) {
		this.degree = degree;
	}
	
	
	/**
	 * Returns the current IP Address of the server
	 * @return - IP Address of the server as a String
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * Sets the IP Address to connect to a server
	 * @param ipAddress - String
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Gets the current port number.
	 * @return - Port number as an int
	 */
	public int getPortNum() {
		return portNum;
	}
	
	/**
	 * Sets a port number to connect to the server
	 * @param portNum - int
	 */
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
	
	/**
	 * Get the rate at which the sensor should sleep before reading another value.
	 * @return - Rate in milliseconds (long)
	 */
	public long getSensorSleepRate() {
		return sensorSleepRate;
	}
	
	/**
	 * Set the rate at which the server should read temperature data.
	 * @param l - long milliseconds
	 */
	public void setSensorSleepRate(long l) {
		this.sensorSleepRate = l;
	}
}
