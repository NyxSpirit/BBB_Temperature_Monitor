package client;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A Frame that shows the help contents associated with the T.R.U. Client.
 * @author Nick Ames
 *
 */
public class HelpPanel extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/*
	 * Text Files Containing Help Contents
	 */
	private final String CLIENT = "help/client.txt";
	private final String CLIENT_SETUP = "help/clientsetup.txt";
	private final String CUST_VIEW = "help/customview.txt";
	private final String GET_START = "help/gettingstarted.txt";
	private final String KNOWN_ISS = "help/knownissues.txt";
	private final String REAL_T = "help/realtime.txt";
	private final String SERVER = "help/server.txt";
	private final String SERVER_SETUP = "help/serversetup.txt";
	private final String SETTINGS = "help/settings.txt";

	private final int NUM_FILES = 9;  // Max number of files
	private final int MAX_LINES = 50; // max number of lines in each file

	private String fileList[][] = new String[NUM_FILES][MAX_LINES];  // The array to hold files and lines
	
	private JTextArea textArea;      // File content held within component

	/**
	 * Creates a new HelpPanel
	 */
	public HelpPanel() {
		super();
		setTitle("T.R.U. Help");
		setBounds(100, 100, 460, 395);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		textArea = new JTextArea();
		initialize();
	}

	/**
	 * Setup the graphical environment
	 */
	private void initialize() {
		
		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setMargin(new Insets(0, 5, 0, 5));
		scrollPane_1.setViewportView(textArea);

		final JList<String> list = new JList<String>();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				switch (list.getSelectedValue()) {
				case "Getting Started":
					setTextArea(0);
					break;
				case "-Server Setup":
					setTextArea(1);
					break;
				case "-Client Setup":
					setTextArea(2);
					break;
				case "Server":
					setTextArea(3);
					break;
				case "Client":
					setTextArea(4);
					break;
				case "-Real-Time":
					setTextArea(5);
					break;
				case "-Custom View":
					setTextArea(6);
					break;
				case "-Settings":
					setTextArea(7);
					break;
				case "Known Issues":
					setTextArea(8);
					break;
				}
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndices(new int[] {0});
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Getting Started", "-Server Setup", "-Client Setup", "Server", "Client", "-Real-Time", "-Custom View", "-Settings", "Known Issues"};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		splitPane.setLeftComponent(list);
	}

	/**
	 * Sets the currently selected help contents
	 * @param f - the index of the help file to show
	 */
	private void setTextArea(int f) {
		textArea.setText("");
		for (int i = 0; i < MAX_LINES; i++) {
			textArea.append(fileList[f][i]);
		}
	}

	/**
	 * Reads in the files to an array.
	 */
	public void fileSetup() {
		InputStreamReader fr = null;
		BufferedReader br = null;
		InputStream url = null;
		String[] files = {GET_START, SERVER_SETUP, CLIENT_SETUP, SERVER, CLIENT, REAL_T, CUST_VIEW, SETTINGS, KNOWN_ISS};

		try {
			for (int i = 0; i < NUM_FILES; i++) {
				// Setup for reading				
				url = getClass().getResourceAsStream(files[i]);
				fr = new InputStreamReader(url);
				br = new BufferedReader(fr);

				// Read in the file to the array
				for (int j = 0; j < MAX_LINES; j++) {
					if (br.ready()) {
						this.fileList[i][j] = br.readLine() + System.lineSeparator();
					} else {
						this.fileList[i][j] = null;
					}
				}
			}
			setTextArea(0);
			br.close();
			fr.close();
		} catch (IOException e) {
		}
	}
}

