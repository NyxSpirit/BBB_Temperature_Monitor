package client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;

public class RTGraph extends JFrame {

	private static final long serialVersionUID = -5588698846999765446L;
	private static final String TITLE = "Real-Time Temperature";
    private static final float MINMAX = 150;
    private static final int COUNT = 2 * 60;
    private static final int SLOW = 500;
    
    private static DynamicTimeSeriesCollection dataset = null;
    private JFreeChart chart = null;
    private JTextField txtData = null;
    private JLabel lblData = null;
    private JPanel btnPanel = null;
    private JButton btnExit = null;
    
    private Timer timer;
	private float tempValue;

    public RTGraph(final String title) {
        super(title);
        initialize();
    }
    
    private void initialize() {
    	
    	// Create the Dynamic Data Set for Graphing
    	dataset = new DynamicTimeSeriesCollection(1, COUNT, new Second());
        dataset.setTimeBase(new Second(new Date()));
        dataset.addValue(0, 0, 0);
        
        // Add the dataset to the chart
        chart = createChart(dataset);
        chart.removeLegend();
        
        // Field for Temp Values
        txtData = new JTextField(4);
        txtData.setEditable(false);
        lblData = new JLabel("Temp in F: ");
        
        // JButton to exit graph
        btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
        });
        
        // Organize the chart and panel for readout
        this.add(new ChartPanel(chart), BorderLayout.NORTH);
        btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(lblData);
        btnPanel.add(txtData);
        btnPanel.add(btnExit);
        this.add(btnPanel, BorderLayout.CENTER);
        
        // Timer for updating values
        timer = new Timer(SLOW, new ActionListener() {

            float[] newData = new float[1];

            public void actionPerformed(ActionEvent e) {
                newData[0] = getTempValue();
                txtData.setText(String.valueOf(newData[0]));
                dataset.advanceTime();
                dataset.appendData(newData);
            }
        });
    }
    
    /**
     * Returns the temperature value for real-time
     * @return
     */
    public float getTempValue() {
		return this.tempValue;
	}
    
    /**
     * Set the real-time temperature value.
     * @param tempValue - float
     */
    public void setTempValue(float tempValue){
    	this.tempValue = tempValue;
    }
    
    /**
     * Create the TimeSeriesChart from JFreeChart lib.
     * @param dataset - XYDataset
     * @return JFreeChart
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            TITLE, "Time (hh:mm:ss)", "Temperature (F)", dataset, true, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(-MINMAX, MINMAX);
        return result;
    }

    public void start() {
        timer.start();
    }
}