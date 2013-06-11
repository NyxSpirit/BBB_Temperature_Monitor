package client;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The Setting Panel contains the information for configuring the GUI and server process.
 * @author Nick Ames
 *
 */
public class SettingPanel extends JPanel {

	/*
	 * Sensor Reading Rates
	 */
	private static final long SEC_1 = 1000;
	private static final long SEC_10 = 10000;
	private static final long SEC_30 = 30000;
	private static final long MIN_1 = 100000;
	private static final long MIN_10 = 1000000;

	/*
	 * Degrees
	 */
	private static final String F = "F";
	private static final String C = "C";

	private static final Rectangle DIM = new Rectangle(0, 21, 260, 199);

	private static final long serialVersionUID = 1L;
	
	private String[] fonts;         // Contains all fonts available to the user
	private JTextField txtIP;       // The IP Address to use to connect to the server
	private JTextField txtPortNum;  // The port number to connect to

	private long dataWriteRate;     // In milliseconds
	private String fontName;        // Name of the selected font
	private int fontSize;           // Size of font
	private String degree;          // Either C or F

	private int portNum;            // Port Number
	private String ipAddress;       // IP Address

	/**
	 * Creates a new SettingPanel.
	 */
	public SettingPanel() {
		super();
		this.setBounds(DIM);
		this.setLayout(null);
		this.dataWriteRate = SEC_1;
		this.degree = F;
		this.portNum = 61223;
		this.ipAddress = "127.0.0.1";  // IP for the server
		initialize();
	}

	private void initialize() {

		// Get all available fonts
		fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		// Settings Panel 
		final JTabbedPane tbSetting = new JTabbedPane(JTabbedPane.BOTTOM);
		tbSetting.setBounds(0, 0, 260, 163);
		this.add(tbSetting);

		///////////////////////////////////////////////////////
		// Real-time settings
		///////////////////////////////////////////////////////
		JPanel realP = new JPanel();
		tbSetting.addTab("Real-Time", null, realP, null);
		realP.setLayout(null);

		JLabel lblFontReal = new JLabel("Font Style");
		lblFontReal.setBounds(0, 17, 132, 17);
		lblFontReal.setHorizontalAlignment(SwingConstants.CENTER);
		lblFontReal.setFont(new Font("Tahoma", Font.PLAIN, 14));
		realP.add(lblFontReal);
		final JComboBox<String> cmbFonts = new JComboBox<String>(fonts);
		cmbFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFontName((String) cmbFonts.getSelectedItem());
			}
		});
		cmbFonts.setEditable(false);
		cmbFonts.setBounds(10, 45, 122, 20);
		cmbFonts.setSelectedIndex(171);
		realP.add(cmbFonts);

		JLabel lblFontSize = new JLabel("Font Size");
		lblFontSize.setBounds(0, 76, 132, 17);
		lblFontSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblFontSize.setFont(new Font("Tahoma", Font.PLAIN, 14));
		realP.add(lblFontSize);

		final JComboBox<String> cmbFSize = new JComboBox<String>();
		cmbFSize.setEditable(false);
		cmbFSize.setBounds(10, 104, 122, 20);
		cmbFSize.setModel(new DefaultComboBoxModel<String>(new String[] {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"}));
		cmbFSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFontSize(Integer.parseInt((String) cmbFSize.getSelectedItem()));
			}
		});
		cmbFSize.setSelectedIndex(7);
		realP.add(cmbFSize);

		JLabel lblTemperature = new JLabel("Temperature");
		lblTemperature.setBounds(142, 17, 113, 17);
		lblTemperature.setHorizontalAlignment(SwingConstants.CENTER);
		lblTemperature.setFont(new Font("Tahoma", Font.PLAIN, 14));
		realP.add(lblTemperature);

		JSeparator separator = new JSeparator();
		separator.setBounds(142, 0, 2, 135);
		separator.setOrientation(SwingConstants.VERTICAL);
		realP.add(separator);

		final JRadioButton btnF = new JRadioButton("Fahrenheit");
		btnF.setBounds(152, 44, 96, 23);
		final JRadioButton btnC = new JRadioButton("Celsius");
		btnC.setBounds(152, 76, 102, 23);

		btnF.setSelected(true);
		realP.add(btnF);
		realP.add(btnC);

		btnF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnC.setSelected(false);
				btnF.setSelected(true);
				setDegree(F);
			}
		});
		btnC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnF.setSelected(false);
				btnC.setSelected(true);
				setDegree(C);
			}
		});

		/////////////////////////////////////////////////////////
		// Connection
		/////////////////////////////////////////////////////////
		JPanel connectP = new JPanel();
		connectP.setLayout(null);
		tbSetting.addTab("Connection", null, connectP, null);

		JLabel lblip = new JLabel("Server IP Address:");
		lblip.setBounds(10, 11, 112, 14);
		connectP.add(lblip);

		txtIP = new JTextField();

		txtIP.setText(getIpAddress());
		txtIP.setBounds(120, 8, 125, 20);
		connectP.add(txtIP);
		txtIP.setColumns(15);
		txtIP.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
			}
			public void insertUpdate(DocumentEvent arg0) {
				setIpAddress(txtIP.getText());
			}
			public void removeUpdate(DocumentEvent arg0) {
				setIpAddress(txtIP.getText());
			}
		});

		JLabel lblPort = new JLabel("Server Port:");
		lblPort.setBounds(10, 36, 112, 14);
		connectP.add(lblPort);

		txtPortNum = new JTextField();
		txtPortNum.setText(String.valueOf(getPortNum()));
		txtPortNum.setBounds(120, 33, 86, 20);
		connectP.add(txtPortNum);
		txtPortNum.setColumns(5);
		txtPortNum.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
			}
			public void insertUpdate(DocumentEvent arg0) {
				try {
					setPortNum(Integer.parseInt(txtPortNum.getText()));
				}  catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Port number must contain non-negative integers ONLY. Example: 1111, 3465, 65432");
				}
			}
			public void removeUpdate(DocumentEvent arg0) {
				try {
					setPortNum(Integer.parseInt(txtPortNum.getText()));
				}  catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Port number must contain non-negative integers ONLY. Example: 1111, 3465, 65432");
				}				}
		});


		JLabel lblNewLabel = new JLabel("Get Temperature Data Every:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 61, 235, 25);
		connectP.add(lblNewLabel);

		// Check Boxes
		final JCheckBox c1S = new JCheckBox("1 s");
		c1S.setBounds(0, 93, 44, 23);
		connectP.add(c1S);

		final JCheckBox c10S = new JCheckBox("10 s");
		c1S.setSelected(true);
		c10S.setBounds(45, 93, 50, 23);
		connectP.add(c10S);

		final JCheckBox c30S = new JCheckBox("30 s");
		c30S.setBounds(97, 93, 50, 23);
		connectP.add(c30S);

		final JCheckBox c1M = new JCheckBox("1 m");
		c1M.setBounds(149, 93, 50, 23);
		connectP.add(c1M);

		final JCheckBox c10M = new JCheckBox("10 m");
		c10M.setBounds(195, 93, 60, 23);
		connectP.add(c10M);

		c1S.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c10S.setSelected(false);
				c30S.setSelected(false);
				c1M.setSelected(false);
				c10M.setSelected(false);
				c1S.setSelected(true);
				setDataWriteRate(SEC_1);
			}
		});
		c10S.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c1S.setSelected(false);
				c30S.setSelected(false);
				c1M.setSelected(false);
				c10M.setSelected(false);
				c10S.setSelected(true);
				setDataWriteRate(SEC_10);
			}
		});
		c30S.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c10S.setSelected(false);
				c1S.setSelected(false);
				c1M.setSelected(false);
				c10M.setSelected(false);
				c30S.setSelected(true);
				setDataWriteRate(SEC_30);
			}
		});
		c1M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c10S.setSelected(false);
				c30S.setSelected(false);
				c1S.setSelected(false);
				c10M.setSelected(false);
				c1M.setSelected(true);
				setDataWriteRate(MIN_1);
			}
		});
		c10M.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c10S.setSelected(false);
				c30S.setSelected(false);
				c1M.setSelected(false);
				c1S.setSelected(false);
				c10M.setSelected(true);
				setDataWriteRate(MIN_10);
			}
		});
		setDataWriteRate(SEC_1);
	}

	protected void setDataWriteRate(long rate) {
		this.dataWriteRate = rate;
	}

	public long getDataWriteRate() {
		return this.dataWriteRate;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}