package goocalsync;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.EventEntry;
import com.google.gdata.data.extensions.OriginalEvent;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.ServiceException;

import de.bea.domingo.DViewEntry;

public class GoogleCalendarDAO implements BaseDAO {
	private CalendarEventEntry workEntry = null;
	private List<CalendarEventEntry> workFeedList = null;
	private int counter = 0;
	private String calendarAddress = null;
	private URL postURL;
	private String eventURL;

	public String getCalendarAddress() {
		if (calendarAddress == null) {
			Settings settings = Factory.getSettings();
			calendarAddress = settings.getGoogleAccountName();
			CalendarService myService = Factory.getCalendarService();
			// Send the request and print the response
			URL feedUrl;
			try {
				feedUrl = new URL("http://www.google.com/calendar/feeds/default/owncalendars/full");
				CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);
				for (int i = 0; i < resultFeed.getEntries().size(); i++) {
					CalendarEntry entry = resultFeed.getEntries().get(i);
					if (entry.getTitle().getPlainText().equals(settings.getCalendarName())) {
						calendarAddress = entry.getId().substring(entry.getId().lastIndexOf('/') + 1);
						break;
					}
				}
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			} catch (ServiceException e) {
			}
		}
		return calendarAddress;
	}

	public String insert(BaseDoc bd) {
		EventEntry myEntry = new EventEntry();

		myEntry.setTitle(new PlainTextConstruct(bd.getTitle()));
		myEntry.setContent(new PlainTextConstruct(bd.getContent()));

		Calendar startDateTime = bd.getStartDateTime();
		DateTime startTime = new DateTime(startDateTime.getTime(), startDateTime.getTimeZone());
		DateTime endTime = new DateTime(bd.getEndDateTime().getTime(), bd.getEndDateTime().getTimeZone());
		if (bd.getApptype() == Constants.ALL_DAY_EVENT || bd.getApptype() == Constants.ANNIVERSARY) {

			// 144000000 is not one day. So comment these lines.
			// startTime.setValue(startTime.getValue()+144000000); // 144000000 is one day as a long
			// value.
			// endTime.setValue(endTime.getValue()+144000000); // All day event must be specified as
			// left that add 1 day.

			// if GMT+5 up, then add 1 day.
			// These steps are for all day event specification on Google.
			if (startTime.getTzShift() >= 300) {
				startTime.setValue(startTime.getValue() + 86400000); // 86400000 is one day as a
				// long value.
				endTime.setValue(endTime.getValue() + 172800000); // All day event must be specified
				// as left that add 1 day.
			} else {
				endTime.setValue(endTime.getValue() + 86400000);
			}

			startTime.setDateOnly(true);
			endTime.setDateOnly(true);
		}

		Where location = GooCalUtil.createWhere(bd.getLocation());
		myEntry.addLocation(location);

		// set reccurence
		if (bd.getRecur().getFrequency() != Constants.FREQ_NONE) {
			Recurrence recur = new Recurrence();
			recur.setValue(createRecurStr(bd));
			myEntry.setRecurrence(recur);
		}else {
			When eventTimes = new When();
			eventTimes.setStartTime(startTime);
			eventTimes.setEndTime(endTime);
			myEntry.addTime(eventTimes);
		}

		try {
			CalendarService cs = Factory.getCalendarService();
			EventEntry insertedEntry = cs.insert(getPostURL(), myEntry);
			String id = insertedEntry.getId();
			return id.replace(getEventURL(), "");
		} catch (Exception e) {
			Factory.getLog().error("Calendar entry being handled ...");
			bd.printError();
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		}
		return null;
	}

	public void update(BaseDoc bd) {

		try {
			workEntry.setTitle(new PlainTextConstruct(bd.getTitle()));
			workEntry.setContent(new PlainTextConstruct(bd.getContent()));

			if (bd.getRecur().getFrequency() != Constants.FREQ_NONE) {
				// set reccurence
				Recurrence recur = new Recurrence();
				recur.setValue(createRecurStr(bd));
				workEntry.setRecurrence(recur);
			} else {
				// set start and end date&time
				When when = new When();
				if (workEntry.getRecurrence() == null) {
					when = workEntry.getTimes().get(0);
				} else {
					Recurrence recur = workEntry.getRecurrence();
					workEntry.removeExtension(recur);
					workEntry.addTime(when);
				}
				if (bd.getApptype() == Constants.ALL_DAY_EVENT || bd.getApptype() == Constants.ANNIVERSARY) {
					DateTime startTime = new DateTime(bd.getStartDateTime().getTime(), bd.getStartDateTime().getTimeZone());
					DateTime endTime = new DateTime(bd.getEndDateTime().getTime(), bd.getEndDateTime().getTimeZone());

					// 144000000 is not one day. So comment these lines.
					// startTime.setValue(startTime.getValue()+144000000); // 144000000 is one day
					// as a long value.
					// endTime.setValue(endTime.getValue()+144000000); // All day event must be
					// specified as left that add 1 day.

					// if GMT+5 up, then add 1 day.
					// These steps are for all day event specification on Google.
					if (startTime.getTzShift() >= 300) {
						startTime.setValue(startTime.getValue() + 86400000); // 86400000 is one day
						// as a long value.
						endTime.setValue(endTime.getValue() + 172800000); // All day event must be
						// specified as left
						// that add 1 day.
					} else {
						endTime.setValue(endTime.getValue() + 86400000);
					}

					startTime.setDateOnly(true);
					endTime.setDateOnly(true);
					when.setStartTime(startTime);
					when.setEndTime(endTime);
				} else {
					DateTime startTime = new DateTime(bd.getStartDateTime().getTime(), bd.getStartDateTime().getTimeZone());
					DateTime endTime = new DateTime(bd.getEndDateTime().getTime(), bd.getEndDateTime().getTimeZone());
				}
			}

			URL editUrl = new URL(workEntry.getEditLink().getHref());
			Where location = GooCalUtil.createWhere(bd.getLocation());
			workEntry.getLocations().get(0).setLabel(bd.getLocation());
			workEntry.getLocations().get(0).setRel(bd.getLocation());
			workEntry.getLocations().get(0).setValueString(bd.getLocation());
			workEntry.addLocation(location);

			CalendarEventEntry updatedEntry = (CalendarEventEntry) Factory.getCalendarService().update(editUrl, workEntry);

		} catch (MalformedURLException e) {
			Factory.getLog().error("Calendar entry being handled ...");
			bd.printError();
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		} catch (Exception e) {
			Factory.getLog().error("Calendar entry being handled ...");
			bd.printError();
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		}
	}

	public BaseDoc select(String id) {

		Settings mySets = Factory.getSettings();
		URL entryUrl;
		workEntry = null;
		try {
			entryUrl = new URL("http://www.google.com/calendar/feeds/" + getCalendarAddress() + "/private/full/" + id);
			CalendarService cs = Factory.getCalendarService();
			workEntry = (CalendarEventEntry) cs.getEntry(entryUrl, CalendarEventEntry.class);
			if (workEntry == null) {
				return null;
			} else {
				return convEntry(workEntry);
			}
		} catch (ResourceNotFoundException e) {
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		} catch (ServiceException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		}
		return null;
	}

	/*
	 * This method deletes Google calendar entry specified by iCal UID.
	 * 
	 * Parameters: id : iCal UID on Google calendar. ex) dklaowklsdfj3osdfj
	 * 
	 * created by Junya Terada on 2009/05/07
	 */
	public void delete(String id) {

		Settings mySets = Factory.getSettings();
		URL entryUrl;
		workEntry = null;
		try {
			CalendarService cs = Factory.getCalendarService();
			entryUrl = new URL("http://www.google.com/calendar/feeds/" + getCalendarAddress() + "/private/full/" + id);
			CalendarEventEntry cee = (CalendarEventEntry) cs.getEntry(entryUrl, CalendarEventEntry.class);
			cee.delete();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		} catch (ServiceException e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		}

	}

	/*
	 * This method returns the first Google calendar entry after date provided by a parameter.
	 * 
	 * Parameters: syncStartDate : from which calendar entries are synchronized. ex) 2009/05/07
	 * 
	 * Return values: BaseDoc object in which each first entry values are stored.
	 * 
	 * created by Junya Terada on 2009/05/07 updated by Muneyuki Ohkawa on 2009/06/03
	 */
	public BaseDoc getFirstEntry() {

		Settings mySets = Factory.getSettings();
		URL entryUrl;
		workEntry = null;
		try {
			entryUrl = new URL("http://www.google.com/calendar/feeds/" + getCalendarAddress() + "/private/full/");
			CalendarService cs = Factory.getCalendarService();

			// Set query parameters to specify
			// Date from which GooCalSync start synchronize calendar entries.
			// Calendar entry order to be sorted starttime and ascending.
			// maximun number of entries received. 65535 is set so that all entries could be
			// retrieved actually.
			// recurrence calendar entry retrieved as single entry with recurrence information.
			CalendarQuery myQuery = new CalendarQuery(entryUrl);
			Calendar sdt = Factory.getSettings().getSyncStartDate();

			myQuery.setMinimumStartTime(new DateTime(sdt.getTime(), sdt.getTimeZone()));
			myQuery.setStringCustomParameter("orderby", "starttime");
			myQuery.setStringCustomParameter("sortorder", "ascending");
			myQuery.setMaxResults(65535);
			myQuery.setStringCustomParameter("singleevents", "false");

			CalendarEventFeed workFeed = (CalendarEventFeed) cs.query(myQuery, CalendarEventFeed.class);
			counter = 0;

			if (workFeed == null) {
				return null;
			} else {
				workFeedList = workFeed.getEntries();
				if (workFeedList.size() > counter) {

					// Commented by M.Ohkawa.
					// This sort logic could not handle recurrence entries.
					// Sorting should be treated by Google API.
					// Collections.sort(workFeedList,new CalendarComparator());

					workEntry = workFeedList.get(counter);
					counter++;
					return convEntry(workEntry);
				} else {
					return null;
				}
			}
		} catch (ResourceNotFoundException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		}
		return null;

	}

	/*
	 * This method returns the next Google calendar entry after an workEntry which works as a
	 * pointer to an entry on calendar feed.
	 * 
	 * Return values: BaseDoc object in which each next entry values are stored.
	 * 
	 * created by Junya Terada on 2009/05/07
	 */
	public BaseDoc getNextEntry() {

		if (workFeedList == null || workFeedList.size() <= counter) {
			return null;
		} else {
			workEntry = workFeedList.get(counter);
			counter++;
			if (workEntry != null) {
				return convEntry(workEntry);
			}
		}
		return null;

	}

	private BaseDoc convEntry(CalendarEventEntry entry) {
		BaseDoc bd = new BaseDoc();
		bd.setTitle(entry.getTitle().getPlainText());
		bd.setContent(entry.getPlainTextContent());
		bd.setId(entry.getId().replace(getEventURL(), ""));
		bd.setRefId(IDTable.getNotesUNID(bd.getId()));
		
		bd.setLocation(entry.getLocations().get(0).getValueString());
		Calendar u = Calendar.getInstance();
		u.setTimeInMillis(entry.getUpdated().getValue());
		// u.setTimeZone(value)(value)()(entry.getUpdated().getValue());
		bd.setLastUpdated(u);

		Recurrence recur = entry.getRecurrence();
		OriginalEvent oe = entry.getOriginalEvent(); // which means RECURRENCE-ID is there.
		if (recur == null && oe == null) {
			DateTime sdt = entry.getTimes().get(0).getStartTime();
			DateTime edt = entry.getTimes().get(0).getEndTime();
			if (sdt.isDateOnly() == true) {
				bd.setStartDateTime(getCalendar(sdt));
				bd.setEndDateTime(getCalendar(edt));
				bd.setApptype(Constants.ALL_DAY_EVENT);
			} else {
				bd.setStartDateTime(getCalendar(sdt));
				bd.setEndDateTime(getCalendar(edt));
				if (sdt.compareTo(edt) == 0) {
					bd.setApptype(Constants.REMINDER);
				} else {
					bd.setApptype(Constants.NORMAL_EVENT);
				}
			}
		} else {
			// 
			// analyzeRecurrence(bd, recur.getValue());
			BaseRecur br = new BaseRecur();
			bd.setRecur(br);
		}

		return bd;

	}

	private Calendar getCalendar(DateTime sdt) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
		Calendar c = Calendar.getInstance();
		if (sdt.getTzShift() == null) {
			c.setTimeInMillis(sdt.getValue());
		} else {
			c.setTimeInMillis(sdt.getValue() + sdt.getTzShift() * 60 * 1000);
		}
		return c;
	}

	/*
	 * This method analyzes iCal recurrence pattern like below to set parameters on BaseDoc received
	 * as input parameters.
	 * 
	 * ** Normal Weekly pattern *** DTSTART;TZID=Asia/Tokyo:20090519T140000
	 * DTEND;TZID=Asia/Tokyo:20090519T150000 RRULE:FREQ=WEEKLY;BYDAY=TU;WKST=MO
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090520T113000 DTEND;TZID=Asia/Tokyo:20090520T123000
	 * RRULE:FREQ=WEEKLY;WKST=MO;UNTIL=20090630T023000Z
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090519T100000 DTEND;TZID=Asia/Tokyo:20090519T110000
	 * RRULE:FREQ=WEEKLY;WKST=MO;UNTIL=20090610T010000Z;INTERVAL=3;BYDAY=TU,WE
	 * 
	 * DTSTART;VALUE=DATE:20090518 DTEND;VALUE=DATE:20090519
	 * RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR;WKST=MO
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090519T123000 DTEND;TZID=Asia/Tokyo:20090519T133000
	 * RRULE:FREQ=WEEKLY;BYDAY=TH;WKST=MO
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090521T210000 DTEND;TZID=Asia/Tokyo:20090521T220000
	 * RRULE:FREQ=WEEKLY;BYDAY=MO,WE,FR;WKST=MO
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090519T103000 DTEND;TZID=Asia/Tokyo:20090519T113000
	 * RRULE:FREQ=WEEKLY;WKST=MO;UNTIL=20090825T013000Z;BYDAY=TU
	 * EXDATE;TZID=Asia/Tokyo:20090811T103000 EXDATE;TZID=Asia/Tokyo:20090602T103000
	 * EXDATE;TZID=Asia/Tokyo:20090714T103000
	 * 
	 * DTSTART;VALUE=DATE:20090521 DTEND;VALUE=DATE:20090522
	 * RRULE:FREQ=MONTHLY;WKST=MO;UNTIL=20090820;BYDAY=3TH
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090519T143000 DTEND;TZID=Asia/Tokyo:20090519T153000
	 * RRULE:FREQ=MONTHLY;BYDAY=3TU;WKST=MO
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090519T123000 DTEND;TZID=Asia/Tokyo:20090519T133000
	 * RRULE:FREQ=MONTHLY;WKST=MO;BYMONTHDAY=19
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090519T133000 DTEND;TZID=Asia/Tokyo:20090519T143000
	 * RRULE:FREQ=YEARLY;UNTIL=20140519T043000Z;WKST=MO
	 * 
	 * DTSTART;VALUE=DATE:20090519 DTEND;VALUE=DATE:20090520 RRULE:FREQ=DAILY;UNTIL=20090521;WKST=MO
	 * 
	 * DTSTART;VALUE=DATE:20090519 DTEND;VALUE=DATE:20090520 RRULE:FREQ=DAILY;UNTIL=20090521;WKST=MO
	 * EXDATE;VALUE=DATE:20090520
	 * 
	 * DTSTART;TZID=Asia/Tokyo:20090512T090000 DTEND;TZID=Asia/Tokyo:20090512T100000
	 * RDATE;VALUE=PERIOD:20090511T170000/20090511T180000,20090518T170000/20090518
	 * T180000,20090525T170000/20090525T180000,20090601T170000/20090601T180000,200
	 * 90608T170000/20090608T180000
	 * 
	 * 
	 * 
	 * private void analyzeRecurrence(BaseDoc bd, String recurrenceValue) {
	 * 
	 * BaseRecur recur = Factory.getBaseRecur(); recur.setFrequency(Constants.FREQ_OTHER);
	 * recur.setInterval(0); recur.setUntil("");
	 * 
	 * String[] recurrenceValueList = recurrenceValue.split("\n"); // extract each line to String
	 * array.
	 * 
	 * String[] recurrenceValueDataList = null; for(String recurrenceValueData :
	 * recurrenceValueList) { recurrenceValueDataList =
	 * recurrenceValueData.split(":");//�l�ƃL�[�𕪂��� String key =
	 * (recurrenceValueDataList[0]).split(";")[0];//����ɃL�[���������o�� String value =
	 * recurrenceValueDataList[1]; if(key.equals("DTSTART") &&
	 * ((recurrenceValueDataList[0]).split(";").length >=2)) { dtStart = convertToDateTime(value); }
	 * if(key.equals("DTEND") && ((recurrenceValueDataList[0]).split(";").length >=2)) { dtEnd =
	 * convertToDateTime(value); }
	 * 
	 * if(key.equals("RRULE")) { for(String valuedata : value.split(";")) { String tempvaluekey =
	 * valuedata.split("=")[0]; String tempvaluedata = valuedata.split("=")[1];
	 * 
	 * if(tempvaluekey.equals("FREQ")) { this.freq = tempvaluedata; }
	 * if(tempvaluekey.equals("INTERVAL")) { this.interval = Integer.parseInt(tempvaluedata); }
	 * if(tempvaluekey.equals("BYDAY")) { this.byday = tempvaluedata.split(","); }
	 * if(tempvaluekey.equals("BYMONTHDAY")) { this.bymonthday = Integer.parseInt(tempvaluedata); }
	 * } } }
	 * 
	 * }
	 */

	private final static SimpleDateFormat DATE = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

	/*
	 * This method returns iCal recurrence value like below.
	 * 
	 * "DTSTART;TZID=Asia/Tokyo:20090520T113000\r\n" + "DTEND;TZID=Asia/Tokyo:20090520T123000\r\n" +
	 * "RRULE:FREQ=WEEKLY;WKST=MO;UNTIL=20090630T023000Z"
	 */
	private String createRecurStr(BaseDoc bd) {
		// Start
		String rs = "DTSTART;";

		// Start timezone
		Calendar sdt = bd.getStartDateTime();
		rs = rs + "TZID=" + TimeZone.getAvailableIDs(sdt.getTimeZone().getRawOffset())[0] + ":";

		// Start date&time
		String dt = null;
		if (bd.getApptype() == Constants.ALL_DAY_EVENT || bd.getApptype() == Constants.ANNIVERSARY) {
			dt = DATE.format(sdt.getTime());
		} else {
			dt = DATE_TIME.format(sdt.getTime());
		}
		rs = rs + dt + "\r\n";

		// End
		rs = rs + "DTEND;";

		// End timezone
		Calendar edt = bd.getEndDateTime();
		rs = rs + "TZID=" + TimeZone.getAvailableIDs(edt.getTimeZone().getRawOffset())[0] + ":";

		// Enbd date&time
		if (bd.getApptype() == Constants.ALL_DAY_EVENT || bd.getApptype() == Constants.ANNIVERSARY) {
			dt = DATE.format(edt.getTime());
		} else {
			dt = DATE_TIME.format(edt.getTime());
		}
		rs = rs + dt + "\r\n";

		// Recurrence rule
		if (bd.getRecur().getFrequency() == Constants.FREQ_OTHER) {
			rs = rs + "RDATE;VALUE=PERIOD:";
			long delta = edt.getTimeInMillis() - sdt.getTimeInMillis();
			for (Calendar rSdt : bd.getRecur().getRdate()) {
				Calendar rEdt = Calendar.getInstance();
				rEdt.setTimeInMillis(rSdt.getTimeInMillis() + delta);
				Object sSdt;
				String sEdt;
				if (bd.getApptype() == Constants.ALL_DAY_EVENT || bd.getApptype() == Constants.ANNIVERSARY) {
					sSdt = DATE.format(rSdt.getTime());
					sEdt = DATE.format(rEdt.getTime());
				} else {
					sSdt = DATE_TIME.format(rSdt.getTime());
					sEdt = DATE_TIME.format(rEdt.getTime());
				}
				rs = rs + sSdt + "/" + sEdt + ",";
			}
			rs = rs.substring(0, rs.length() - 1);
		} else {
			rs = rs + "RRULE:";
			switch (bd.getRecur().getFrequency()) {
			case Constants.FREQ_DAILY:
				rs = rs + "FREQ=DAILY;";
				break;
			case Constants.FREQ_WEEKLY:
				rs = rs + "FREQ=WEEKLY;WKST=MO;";
				if (bd.getRecur().getInterval() == 2) {
					rs = rs + "INTERVAL=2;";
				}
				break;
			case Constants.FREQ_MONTHLY:
				rs = rs + "FREQ=MONTHLY;";
				break;
			case Constants.FREQ_YEARLY:
				rs = rs + "FREQ=YEARLY;";
				break;
			}
			dt = DATE_TIME.format(bd.getRecur().getUntil().getTime());
			rs = rs + "UNTIL=" + dt + "Z";
		}
		return rs;
	}

	private URL getPostURL() {
		if (postURL == null) {
			try {
				postURL = new URL("http://www.google.com/calendar/feeds/" + getCalendarAddress() + "/private/full");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				GooCalUtil.logStackTrace(e);
				System.exit(-1);
			}
		}
		return postURL;
	}

	private String getEventURL() {
		if (eventURL == null) {
			Settings mySets = Factory.getSettings();
			eventURL = "http://www.google.com/calendar/feeds/" + getCalendarAddress() + "/events/";
			eventURL = eventURL.replace("@", "%40");
		}
		return eventURL;
	}

	@Override
	public String getDirection() {
		return Constants.GOOGLE_TO_NOTES;
	}
}
