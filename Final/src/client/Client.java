package client;
import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class Client {

	private JFrame frame;
	private ClientProcess cp;

	private Thread client;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frame.setVisible(true);
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
		frame = new JFrame();
		frame.setBounds(100, 100, 304, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 288, 262);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("T.R.U. Temperature Client");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 268, 59);
		panel.add(lblNewLabel);

		final JLabel lblStatus = new JLabel("Disconnected");
		lblStatus.setForeground(Color.RED);
		lblStatus.setBounds(129, 96, 81, 14);
		panel.add(lblStatus);

		final JButton btnConnect = new JButton("Connect");
		final JButton btnDisconnect = new JButton("Disconnect");
		final JButton btnReal = new JButton("Real-Time");
		

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnConnect.getText().equals("Connect")) {
					cp = new ClientProcess("localhost", 61223);
					client = new Thread(cp);
					try {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						cp.connect();
						client.start();
						btnConnect.setVisible(false);
						btnDisconnect.setVisible(true);
						lblStatus.setForeground(Color.GREEN);
						lblStatus.setText("Connected");
						btnReal.setEnabled(true);
						frame.repaint();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		});
		btnConnect.setBounds(84, 120, 116, 23);
		panel.add(btnConnect);
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					cp.disconnect();
					btnDisconnect.setVisible(false);
					btnConnect.setVisible(true);
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Disconnected");
					btnReal.setEnabled(false);
					frame.repaint();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		btnDisconnect.setBounds(84, 120, 116, 23);
		btnDisconnect.setVisible(false);
		panel.add(btnDisconnect);
		
		btnReal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cp.setReal(true);
			}
		});
		btnReal.setEnabled(false);
		btnReal.setBounds(84, 154, 116, 23);
		panel.add(btnReal);

		JLabel lblNewLabel_1 = new JLabel("Status :");
		lblNewLabel_1.setBounds(84, 96, 52, 14);
		panel.add(lblNewLabel_1);

		JButton btnNewButton = new JButton("Open Log");
		btnNewButton.setBounds(84, 188, 116, 23);
		panel.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Exit");
		btnNewButton_1.setBounds(84, 222, 116, 23);
		panel.add(btnNewButton_1);
	}
}
