package client;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXDatePicker;

public class CustomViewPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final Rectangle DIM = new Rectangle(0, 21, 260, 199);
	private static JSpinner spinTimeTo_1;
	private JXDatePicker jDateFrom;
	private JXDatePicker jDateTo;
	private JSpinner spinTimeFrom;
	@SuppressWarnings("unused")
	private JSpinner spinTimeTo;
	
	public CustomViewPanel() {
		super();
		setBounds(DIM);
		setLayout(null);

		JSeparator sepVert = new JSeparator();
		sepVert.setBounds(136, 0, 11, 132);
		sepVert.setOrientation(SwingConstants.VERTICAL);
		sepVert.setPreferredSize(new Dimension(3, 1));
		add(sepVert);

		JLabel lblTime = new JLabel("Time");
		lblTime.setBounds(136, 5, 114, 14);
		lblTime.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTime);

		JSeparator sepHorz = new JSeparator();
		sepHorz.setBounds(0, 24, 260, 5);
		sepHorz.setOrientation(SwingConstants.HORIZONTAL);
		sepHorz.setPreferredSize(new Dimension(1, 5));
		add(sepHorz);

		JLabel lblDateFrom = new JLabel("From");
		lblDateFrom.setHorizontalAlignment(SwingConstants.CENTER);
		lblDateFrom.setBounds(0, 34, 126, 14);
		add(lblDateFrom);

		JLabel lblTimeFrom = new JLabel("From");
		lblTimeFrom.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeFrom.setBounds(157, 34, 62, 14);
		add(lblTimeFrom);

		jDateFrom = new JXDatePicker(new Date());
		jDateFrom.setBounds(5, 53, 121, 22);
		add(jDateFrom);

		SpinnerDateModel model1 = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
		spinTimeFrom = new JSpinner(model1);
		spinTimeFrom.setBounds(157, 54, 72, 20);
		spinTimeFrom.setEditor(new JSpinner.DateEditor(spinTimeFrom, "h:mm a"));
		add(spinTimeFrom);

		JLabel lblDateTo = new JLabel("To");
		lblDateTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblDateTo.setBounds(0, 78, 126, 14);
		add(lblDateTo);

		jDateTo = new JXDatePicker(new Date());
		jDateTo.setBounds(5, 97, 121, 22);
		add(jDateTo);

		spinTimeTo = new JSpinner();
		SpinnerDateModel model2 = new SpinnerDateModel();
		model2.setCalendarField(Calendar.MINUTE);

		JLabel lblTimeTo = new JLabel("To");
		lblTimeTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeTo.setBounds(157, 78, 62, 14);
		add(lblTimeTo);
		spinTimeTo_1 = new JSpinner();
		spinTimeTo_1.setBounds(157, 98, 72, 20);
		spinTimeTo_1.setModel(model2);
		spinTimeTo_1.setEditor(new JSpinner.DateEditor(spinTimeTo_1, "h:mm a"));
		add(spinTimeTo_1);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(1, 5));
		separator.setOrientation(SwingConstants.HORIZONTAL);
		separator.setBounds(0, 132, 260, 5);
		add(separator);
		
		JLabel lblDate = new JLabel("Date");
		lblDate.setHorizontalAlignment(SwingConstants.CENTER);
		lblDate.setBounds(0, 5, 136, 14);
		add(lblDate);
	}

	/**
	 * @param spinFrom	A JSpinner object indicating the starting time in hours and minutes (12 hour clock).
	 * @param spinTo	A JSpinner object indicating the end time in hours and minutes (12 hour clock).
	 * @param datefrom	A JXDatePicker object indicating the starting date in day, month, year
	 * @param dateto	A JXDatePicker object indicating the ending date in day, month, year
	 * @return			A int array giving: <br>
	 *  starting hour (24 clock, two digits), starting minute (two digits) <br>
	 *  ending hour (24 clock, two digits), ending minute (two digits). <br>
	 *  starting day of the year (two digits), ending day of the year (two digits). <br>
	 *  starting year (two digits), ending year (two digits).
	 */
	private static int[] getPeriod(JSpinner spinFrom, JSpinner spinTo,
			JXDatePicker datefrom, JXDatePicker dateto)
	{
		boolean debug1 = false;
		boolean debug2 = false;
		boolean debug3 = false;
		boolean debug4 = false;
		
		int[] dates = new int[8];
		
		JSpinner spinTimeFrom = null;
		JSpinner spinTimeTo = null;
		JXDatePicker jDateFrom = null;
		JXDatePicker jDateTo = null;
		
		if (spinFrom instanceof JSpinner) {
			spinTimeFrom = spinFrom;
		}
		else {
			for (int i : dates) dates[i] = -1;
			return dates;
		}
		if (spinTo instanceof JSpinner) {
			spinTimeTo = spinTo;
		}
		else {
			for (int i : dates) dates[i] = -1;
			return dates;
		}
		if (datefrom instanceof JXDatePicker) {
			jDateFrom = datefrom;
		}
		else {
			for (int i : dates) dates[i] = -1;
			return dates;
		}
		if (dateto instanceof JXDatePicker) {
			jDateTo = dateto;
		}
		else {
			for (int i : dates) dates[i] = -1;
			return dates;
		}

		String dateFrom = "", dateTo = "", timeFrom = "", timeTo = "";

		timeFrom = new SimpleDateFormat("HH,mm").format((Date) spinTimeFrom.getValue());
		timeTo = new SimpleDateFormat("HH,mm").format((Date) spinTimeTo.getValue());
		// DD = day in year, so we don't need to know the month.
		dateFrom = new SimpleDateFormat("DD,yy").format(jDateFrom.getDate());
		dateTo = new SimpleDateFormat("DD,yy").format(jDateTo.getDate());

		Scanner scanner = new Scanner(timeFrom);
		scanner.useDelimiter(",");
		final int startHour = scanner.nextInt();
		final int startMin = scanner.nextInt();
		scanner.close();
		if (debug1) {
			System.out.println("startHour: " + startHour);
			System.out.println("startMin: " + startMin);	
		}

		scanner = new Scanner(timeTo);
		scanner.useDelimiter(",");
		final int endHour = scanner.nextInt();
		final int endMin = scanner.nextInt();
		scanner.close();
		if (debug2) {
			System.out.println("endHour: " + endHour);
			System.out.println("endMin: " + endMin);
		}

		scanner = new Scanner(dateFrom);
		scanner.useDelimiter(",");
		final int startDay = scanner.nextInt();
		final int startYear = scanner.nextInt();
		scanner.close();
		if (debug3) {
			System.out.println("startDay " + startDay);
			System.out.println("startYear " + startYear);
		}

		scanner = new Scanner(dateTo);
		scanner.useDelimiter(",");
		final int endDay = scanner.nextInt();
		final int endYear = scanner.nextInt();
		scanner.close();
		if (debug4) {
			System.out.println("endDay " + endDay);
			System.out.println("endYear " + endYear);
		}
		
		dates[0] = startHour;
		dates[1] = startMin;
		dates[2] = endHour;
		dates[3] = endMin;
		dates[4] = startDay;
		dates[5] = startYear;
		dates[6] = endDay;
		dates[7] = endYear;

		return dates;
	}

	public void showGraph() {
		// TODO Auto-generated method stub
		System.out.println("show");
	}
	
	public int[] getDates() {
		return getPeriod(spinTimeFrom, spinTimeTo_1, jDateFrom, jDateTo);
	}
}