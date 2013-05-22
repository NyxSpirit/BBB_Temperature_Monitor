package client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import org.jfree.ui.ApplicationFrame;

public class RTGraph extends JFrame {

    private static final String TITLE = "Realtime Temperature";
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final float MINMAX = 150;
    private static final int COUNT = 2 * 60;
    private static final int FAST = 100;
    private static final int SLOW = FAST * 5;
    private static final Random random = new Random();
    private Timer timer;
	private float tempValue;

    public RTGraph(final String title) {
        super(title);
        final DynamicTimeSeriesCollection dataset =
            new DynamicTimeSeriesCollection(1, COUNT, new Second());
        dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 2013));
        dataset.addValue(0, 0, 0);
        JFreeChart chart = createChart(dataset);
        chart.removeLegend();

        final JButton run = new JButton(STOP);
        run.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (STOP.equals(cmd)) {
                    timer.stop();
                    run.setText(START);
                } else {
                    timer.start();
                    run.setText(STOP);
                }
            }
        });
        
        final JTextField txtData = new JTextField(4);
        txtData.setEditable(false);
        
        final JLabel lblData = new JLabel("Temp in F: ");
        
        final JComboBox combo = new JComboBox();
        combo.addItem("Fast");
        combo.addItem("Slow");
        combo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Fast".equals(combo.getSelectedItem())) {
                    timer.setDelay(FAST);
                } else {
                    timer.setDelay(SLOW);
                }
            }
        });

        this.add(new ChartPanel(chart), BorderLayout.NORTH);
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Color.WHITE);
       // btnPanel.add(run);
      //  btnPanel.add(combo);
        btnPanel.add(lblData);
        btnPanel.add(txtData);
        
        this.add(btnPanel, BorderLayout.CENTER);

        timer = new Timer(SLOW, new ActionListener() {

            float[] newData = new float[1];

            @Override
            public void actionPerformed(ActionEvent e) {
                newData[0] = getTempValue();
                txtData.setText(String.valueOf(newData[0]));
                dataset.advanceTime();
                dataset.appendData(newData);
            }
        });
    }

    public float getTempValue() {
		return this.tempValue;
	}
    
    public void setTempValue(float tempValue){
    	this.tempValue = tempValue;
    }

	private float randomValue() {
        return (float) (random.nextGaussian() * MINMAX / 3);
    }

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