package com.googlecode.syncnotes2google;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import lotus.domino.NotesException;

import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.googlecode.syncnotes2google.dao.BaseDoc;

import de.bea.domingo.util.GregorianDateTime;
import de.bea.domingo.util.GregorianDateTimeRange;

public class GooCalUtil {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");
	private static Calendar SyncStartTime = null;

	public static String adjustXStoDST(String dt) {
		return dt;
	}

	public static void adjustBaseDocToDST(BaseDoc bd) {

//		bd.setStartDateTime(adjustXStoDST(bd.getStartDateTime()));
//		bd.setEndDateTime(adjustXStoDST(bd.getEndDateTime()));
//
//		BaseRecur br = bd.getRecur();
//		if (!br.getUntil().equals("")) {
//			br.setUntil(adjustXStoDST(br.getUntil()));
//		}
//		if (br.getRdate() != null) {
//			for (int i = 0; i < br.getRdate().length; i++) {
//				br.getRdate()[i] = adjustXStoDST(br.getRdate()[i]);
//			}
//		}

	}

	/**
	 * This method is used in order to prevent different behavior of
	 * getItemValueString method depending on Lotus Notes version. Consult
	 * following technote.
	 * 
	 * Title: GetItemValueString method of document class returns null instead
	 * of empty string Doc #: 1103014 URL:
	 * http://www.ibm.com/support/docview.wss?rs=899&uid=swg21103014
	 */
	public static String convNull(String s) {
		if (s == null) {
			return "";
		} else {
			return s;
		}
	}

	public static void logStackTrace(Exception e) {
		java.lang.StackTraceElement[] stack = e.getStackTrace();
		Factory.getLog().error(e.getClass().getName() + ": " + e.getMessage());
		for (int i = 0; i < stack.length; i++)
			Factory.getLog().error("\tat " + stack[i]);
	}

	public static void setSyncStartTime() {
		SyncStartTime = GooCalUtil.getNow();
	}

	public static Calendar getSyncStartTime() {
		return SyncStartTime;
	}

	public static Calendar getNow() {
		return Calendar.getInstance();
	}

	public static int compareGoogleDateTime(String sa, String sb) {

		com.google.gdata.data.DateTime dta = com.google.gdata.data.DateTime
				.parseDateTime(sa);
		com.google.gdata.data.DateTime dtb = com.google.gdata.data.DateTime
				.parseDateTime(sb);
		if (dta.getValue() - dtb.getValue() > 0) {
			return Constants.BIG;
		} else if (dta.getValue() - dtb.getValue() < 0) {
			return Constants.SMALL;
		} else {
			return Constants.EQUAL;
		}

	}

	// Convert xs:date format(ex. "2009-05-20T12:00:00+09:00") to DateTime of
	// Notes
	public static GregorianDateTime convXStoNotesDateTime(String org) {
		// This line does not work properly in Notes 6.5
		// lotus.domino.DateTime dt =
		// Factory.getNotesSession().createDateTime(org.substring(5, 7) +
		// Factory.getInternational().getDateSep() + org.substring(8, 10) +
		// Factory.getInternational().getDateSep() + org.substring(0,
		// 4)+" "+org.substring(11, 19));

		int year = Integer.parseInt(org.substring(0, 4));
		int month = Integer.parseInt(org.substring(5, 7));
		int date = Integer.parseInt(org.substring(8, 10));
		int hour = Integer.parseInt(org.substring(11, 13));
		int min = Integer.parseInt(org.substring(14, 16));
		int sec = Integer.parseInt(org.substring(17, 19));
//		cal.set(year, month - 1, date, hour, min, sec); // month of
		// java.util.Calendar is
		// 0 to 11

		String tzStr = org.substring(19, 22);
		if (tzStr.charAt(0) == '+') {
			tzStr = tzStr.substring(1, 3);
		}
		int tzOffset = Integer.parseInt(tzStr) * 60 * 60 * 1000;
		TimeZone tz = TimeZone.getTimeZone(java.util.TimeZone
				.getAvailableIDs(tzOffset)[0]);
		return new GregorianDateTime(year,month,date,hour,min,sec,tz);
	}

	public static String convNotesDateTimeToXS(GregorianDateTimeRange org) {
		return convNotesDateTimeToXS(org.getFrom());
	}
		public static String convNotesDateTimeToXS(Calendar org) {

		String orgYear, orgMonth, orgDate, orgTime, orgTZ;
//		org.
//		try {

			/*
			 * In case of DST, decrease by 1 hour. if (adjustDST && org.isDST())
			 * { org.adjustHour(-1); }
			 */

			DecimalFormat df = new DecimalFormat("0000");
			Calendar cal = org;

			orgYear = df.format(cal.get(Calendar.YEAR));
			df.applyPattern("00");
			orgMonth = df.format(cal.get(Calendar.MONTH) + 1);

			orgDate = df.format(cal.get(Calendar.DATE));

			// This code could not get 24 hour format on every locale,
			// especially Chinese.
			// orgTime = org.getTimeOnly();

			// orgTime = org.getTimeOnly();
			// if (orgTime.compareTo("") == 0) {
			// orgTime = "00:00:00";
			// } else {
			// set time to orgTime with 24 hours format.
			Calendar tcal = org;
			// tcal.setTime(org.toJavaDate());
			orgTime = df.format(tcal.get(Calendar.HOUR_OF_DAY)) + ":"
					+ df.format(tcal.get(Calendar.MINUTE)) + ":"
					+ df.format(tcal.get(Calendar.SECOND));
			// }

			df.applyPattern("+00;-00");
			TimeZone timeZone = org.getTimeZone();
			orgTZ = "";/*df.format(timeZone);
			if (orgTZ.compareTo("+00") == 0) {
				orgTZ = df.format(-(long) Factory.getInternational()
						.getTimeZone());
			}*/

			return orgYear + "-" + orgMonth + "-" + orgDate + "T" + orgTime
					+ orgTZ + ":00";

//		} catch (NotesException e) {
//			e.printStackTrace();
//			GooCalUtil.logStackTrace(e);
//			System.exit(-1);
//		}

//		return null;

	}

	/**
	 * �J�����_�[�̊J�n�A�I����?�B�When��?�?� DateTime startTime =
	 * GooCalUtil.convDateTime("2009/04/14 10:00"); DateTime endTime =
	 * GooCalUtil.convDateTime("2009/04/14 15:00"); When eventTimes =
	 * GooCalUtil.createWhen(startTime, endTime);
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static When createWhen(com.google.gdata.data.DateTime start,
			com.google.gdata.data.DateTime end) {
		When retWhen = new When();
		retWhen.setStartTime(start);
		retWhen.setEndTime(end);

		return retWhen;
	}

	/**
	 * �J�����_�[�̊J�n�A�I����?�B�When��?�?� When eventTimes =
	 * GooCalUtil.createWhen("2009/04/14 10:00", "2009/04/14 15:00");
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static When createWhen(String start, String end) {
		return createWhen(convDateTime(start), convDateTime(end));
	}

	/**
	 * �J�����_�[��Location��?�B�Where��?�?� Where eventLocation =
	 * GooCalUtil.createWhere("Location");
	 * 
	 * @param location
	 * @return
	 */
	public static Where createWhere(String location) {
		return new Where(location, location, location);
	}

	/**
	 * �J�����_�[�p��DateTime��?�
	 * 
	 * @param date
	 * @return
	 */
	public static com.google.gdata.data.DateTime convDateTime(String date) {
		com.google.gdata.data.DateTime dateTime = null;
		try {
			dateTime = new com.google.gdata.data.DateTime(dateFormat
					.parse(date));
			dateTime.setTzShift(new Integer(9));
		} catch (ParseException e) {
			new Exception("��t��?u2008/02/28 12:00�v�`���Ŏw�肵�Ă��������B");
			e.printStackTrace();
			GooCalUtil.logStackTrace(e);
			System.exit(-1);
		}
		return dateTime;
	}

	/**
	 * Google Calendar��DateTime��String�ɕϊ�
	 * 
	 * @param date
	 * @return
	 */
	public static String convDateTimeS(com.google.gdata.data.DateTime date) {
		date.setTzShift(new Integer(540));// 9����
		return date.toUiString();
	}
}
