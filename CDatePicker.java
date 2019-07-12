package edi.bvf.com.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CDatePicker {

	private JTextField mtday[];
	private JLabel wkday[]; 
	private JLabel rt, lt;
	private JSpinner year, months; 
	private Cursor hndCur;
	
	private final int dom[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	private final String monthText[] = { 
			"January", "February", "March", "April", "May", "June", 
			"July", "August", "September", "October","November", "December" };
	
	private final String dayText[] = { 
			"Sun", "Mom", "Tue", "Wed", "Thr", "Fri", "Sat" };
	
	public CDatePicker() { }
	
	public class CyclingSpinnerListModel extends SpinnerListModel {
		private static final long serialVersionUID = -5361568550595232698L;	
		Object firstValue, lastValue;
	    
	    public CyclingSpinnerListModel(Object[] values) {
	        super(values);
	        firstValue = values[0];
	        lastValue = values[values.length - 1];
	    }

	    public Object getNextValue() {
	        Object value = super.getNextValue();
	        if (value == null) {
	            value = firstValue;
	            year.setValue(((Integer) year.getValue()).intValue() + 1); 	 
	        }
	        return value;
	    }

	    public Object getPreviousValue() {
	        Object value = super.getPreviousValue();
	        if (value == null) {
	            value = lastValue;
	            year.setValue(((Integer) year.getValue()).intValue() - 1);
	        }
	        return value;
	    }
	}
	

	public void getDatePickerDialog(final JFrame parent, final JTextField returnDate) {
	 
		final JDialog frame = new JDialog(parent, "Select Date");
		
		hndCur = new Cursor(12);
		final String yymmdd = new SimpleDateFormat("yyyyMMdd").format(new Date());
		final int yy = Integer.parseInt(yymmdd.substring(0, 4));
		final int mm = Integer.parseInt(yymmdd.substring(4, 6))-1;
		final int dd = Integer.parseInt(yymmdd.substring(6, 8));
				
				 
	    JPanel center = new JPanel(new CFlLayout(4, 4));

	    year = new JSpinner(new SpinnerNumberModel(1000, 1000,9999, 1));
	    year.setValue(new Integer(yy));
	    year.setEditor(new JSpinner.NumberEditor(year, "####"));
	    year.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent e) {
	    		repaintDate( ((Integer) year.getValue()).intValue(),
	    				Arrays.asList(monthText).indexOf((String) months.getValue()),  dd);			
	    	}
	    });

	    months = new JSpinner(new CyclingSpinnerListModel(monthText));		
	    months.setEditor(new JSpinner.ListEditor(months));
	    months.setValue(monthText[mm]);
	    months.setPreferredSize(new Dimension(100, 20));    
	    months.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent e) {	    		
	    		repaintDate( ((Integer) year.getValue()).intValue(),
	    				Arrays.asList(monthText).indexOf((String) months.getValue()),  dd);	
	    	}
	    });


		lt = new JLabel("<<");
		lt.setForeground(Color.BLUE);
		lt.setCursor(hndCur);
		lt.addMouseListener( new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent me) {}
			@Override
			public void mousePressed(MouseEvent me)  {}
			@Override
			public void mouseEntered(MouseEvent me) {}
			@Override
			public void mouseExited(MouseEvent me) {}	
			@Override
			public void mouseClicked(MouseEvent me) {updateMonth(-1);}
		});
		
		rt = new JLabel(">>"); 
		rt.setForeground(Color.BLUE);
		rt.setCursor(hndCur);
		rt.addMouseListener( new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent me) {}
			@Override
			public void mousePressed(MouseEvent me)  {}
			@Override
			public void mouseEntered(MouseEvent me) {}
			@Override
			public void mouseExited(MouseEvent me) {}	
			@Override
			public void mouseClicked(MouseEvent me) {updateMonth(+1);}
		});
		
		wkday= new JLabel[7];
		for (int d = 0; d < 7; d++) {
			wkday[d] = new JLabel();
			wkday[d].setText(dayText[d]);
			wkday[d].setForeground(Color.BLUE);
			wkday[d].setHorizontalAlignment(JLabel.CENTER);	
			center.add(wkday[d], new CFlParm( (d == 0) ? true : false, d + 1));
		}

		mtday = new JTextField[42];
		for (int d = 0; d < 42; d++) {
			mtday[d] = new JTextField(2);
			mtday[d].setEditable(false);
			mtday[d].setBackground(Color.white);
			mtday[d].setHorizontalAlignment(JTextField.CENTER);
			mtday[d].setCursor(hndCur);
			mtday[d].setName(Integer.toString(d));
			final int tab = (d % 7);
			center.add(mtday[d], new CFlParm( (tab == 0) ? true : false, tab + 1));			
			mtday[d].addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					final int n = Integer.parseInt(e.getComponent().getName()); 
					returnDate.setText(String.format(
							new SimpleDateFormat("yyyy-MM-dd").format(
							new GregorianCalendar(
									((Integer) year.getValue()),
									(Arrays.asList(monthText).indexOf((String) months.getValue())),
									(Integer.parseInt(mtday[n].getText()))).getTime() ) ));
					
					frame.dispose();
				}
			});
		}
		
		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
		north.add(lt);
		north.add(months);
		north.add(year);
		north.add(rt);

		JPanel main = new JPanel(new BorderLayout());
		main.add(BorderLayout.NORTH, north);
		main.add(BorderLayout.CENTER, center);
		frame.getContentPane().add(main);
		frame.pack();
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Point p = returnDate.getLocationOnScreen();
		frame.setLocation(p.x - 160, p.y + 30);
		repaintDate(yy, mm, dd);
		frame.setVisible(true);
	}

	
	protected void updateMonth(int x) {	
		int dd = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()).substring(6, 8));
		int newMM = Arrays.asList(monthText).indexOf((String) months.getValue()) + x;
		int yy = ((Integer) year.getValue());
		if(newMM > 11) {
			newMM = 0;
			yy++;		
		} else if(newMM < 0) {
			newMM = 11;
			yy--;		
		}
		year.setValue(yy);
		months.setValue(monthText[newMM]);
		repaintDate(yy, newMM, dd);			
	}
	
	
	protected void repaintDate(int yy, int mm, int dd) {		
		GregorianCalendar calendar = new GregorianCalendar(yy, mm, 1);
		int leadGap = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int daysInMonth = dom[mm];
		if (calendar.isLeapYear(calendar.get(Calendar.YEAR)) && mm == 1) daysInMonth++;
		
		for (int d = 1; d <= 42; d++) {
			mtday[d - 1].setBackground( (d - leadGap == dd) ? SystemColor.gray : SystemColor.white);
			mtday[d - 1].setForeground( (d - leadGap == dd) ? Color.white : Color.black);	
			mtday[d - 1].setText( (d > leadGap && d <= daysInMonth + leadGap) ? Integer.toString(d - leadGap) : null);
			mtday[d - 1].setVisible( (d > leadGap && d <= daysInMonth + leadGap) ? true : false);
			
		}
	}
	
}
