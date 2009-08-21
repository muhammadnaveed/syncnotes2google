package goocalsync;

import java.util.Comparator;

import com.google.gdata.data.calendar.CalendarEventEntry;

public class CalendarComparator implements Comparator<CalendarEventEntry> {
	
	 public int compare(CalendarEventEntry o1, CalendarEventEntry o2) {
		 String s1 = o1.getTimes().get(0).getStartTime().toString();
		 String s2 = o2.getTimes().get(0).getStartTime().toString();
		 return s1.compareTo(s2);
	 }
	 
}
