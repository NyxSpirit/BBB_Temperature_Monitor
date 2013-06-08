package client;
import java.awt.Color;
import java.awt.Cursor;
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

public class Client {

	private static final Rectangle DIM = new Rectangle(0, 21, 260, 199);
	private static final String IP = "131.191.106.216";

	private JButton btnMain1;
	private JButton btnCustHome;
	private JButton btnCustGraph;

	private Thread client;

	private JFrame frmTruClient;
	private ClientProcess cp;
	private JPanel realPanel;

	private SettingPanel settingPanel;

	private CustomViewPanel customPanel;
	
	private String degree;
	
	private String ipAddress;
	private int portNum;
	
	private long sensorSleepRate;

	/**
	 * Launch the application.
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
	 * Create the application.
	 */
	public Client() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
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
		
		// Connect to the server button
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnConnect.getText().equals("Connect")) {
					cp = new ClientProcess(settingPanel.getIpAddress(), getPortNum(), txtTemp, settingPanel.getDegree());
					client = new Thread(cp);
					File file = createSettingsFile();
					cp.setInFile(file);
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
						System.out.println(e.getMessage());
					} finally {
						frmTruClient.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		});
		btnConnect.setBounds(101, 56, 116, 23);
		panel.add(btnConnect);
		
		// Disconnect from the server button
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
					System.out.println(e.getMessage());
				} finally {
					frmTruClient.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		btnDisconnect.setBounds(101, 56, 116, 23);
		btnDisconnect.setVisible(false);
		panel.add(btnDisconnect);
		
		// Change to the real-time view panel
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
		
		// Go to the home panel from the custom screen
		btnCustHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(true);
				customPanel.setVisible(false);
			}
		});
		
		// Go to the custom view panel from the home panel
		btnCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				customPanel.setVisible(true);
				panel.setVisible(false);
			}
		});
		btnCustom.setBounds(101, 121, 116, 23);
		panel.add(btnCustom);
		
		// Exit the program and disconnect
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
		
		// Home button from the settings panel
		// IMPORTANT:
		// This button will dictate the settings and affect different things
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
		
		// Custom Graphing 
		btnCustGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				final int[] times = customPanel.getDates();
				
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

		JMenuItem mHow = new JMenuItem("How To");
		mnNewMenu_2.add(mHow);

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

	protected File createSettingsFile() {
		File inFile = new File("config.ini");
		FileWriter fo = null;
		BufferedWriter bw = null;
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

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public long getSensorSleepRate() {
		return sensorSleepRate;
	}

	public void setSensorSleepRate(long l) {
		this.sensorSleepRate = l;
	}
}
