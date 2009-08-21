package goocalsync;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class Constants {

	  public static final String GOOCALSYNC_DB_FILE = "GooCalSync.nsf";
	  public static final String NOTES_CALENDAR_VIEW = "Calendar";
	  public static final String GOOCALSYNC_LOG_VIEW = "LogView";
	  
	  // Big or Small
	  public static final int BIG = 1;
	  public static final int SMALL = 2;
	  public static final int EQUAL = 3;
	  
	  // Sync direction
	  public static final String BI_DIRECTION = "1";
	  public static final String NOTES_TO_GOOGLE = "Notes";
	  public static final String GOOGLE_TO_NOTES = "Google";
	  
	  // Appointment type
	  public static final int MEETING = 3;
	  public static final int NORMAL_EVENT = 0;
	  public static final int ALL_DAY_EVENT = 2;
	  public static final int ANNIVERSARY = 1;
	  public static final int REMINDER = 4;
	  
	  // Frequency type of recurrence calendar entry.
	  public static final int FREQ_NONE   = 0;
	  public static final int FREQ_DAILY   = 1;
	  public static final int FREQ_WEEKLY  = 2;
	  public static final int FREQ_MONTHLY = 3;
	  public static final int FREQ_YEARLY  = 4;
	  public static final int FREQ_OTHER   = 99;
	  
	  // Day of recurrence calendar entry.
	  public static final int BYDAY_MO     = MONDAY;
	  public static final int BYDAY_TU     = TUESDAY;
	  public static final int BYDAY_WE     = WEDNESDAY;
	  public static final int BYDAY_TH     = THURSDAY;
	  public static final int BYDAY_FR     = FRIDAY;
	  public static final int BYDAY_SA     = SATURDAY;
	  public static final int BYDAY_SU     = SUNDAY;
	  
}
