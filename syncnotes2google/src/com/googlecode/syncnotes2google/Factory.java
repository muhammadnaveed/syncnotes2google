package com.googlecode.syncnotes2google;

import java.util.Calendar;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.AuthenticationException;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DNotesException;
import de.bea.domingo.DNotesFactory;
import de.bea.domingo.DNotesRuntimeException;
import de.bea.domingo.DSession;

public class Factory {

	private static Factory instance = null;
	private DSession notesSession = null;
	private Settings settings = null;
	private CalendarService calendarService = null;
	private DDatabase mailDatabase = null;

	private Factory() {
	}

	public void freeNotesObject() {
		if (mailDatabase != null) {
			mailDatabase = null;
		}
		if (notesSession != null) {
			notesSession = null;
		}
	}

	public static Factory getInstance() {
		if (instance == null) {
			instance = new Factory();
		}
		return instance;
	}

	public DSession getNotesSession() {
		if (notesSession == null) {
			try {
				// NotesThread.sinitThread();
				DNotesFactory factory = DNotesFactory.getInstance();
				notesSession = factory.getSession();
			} catch (DNotesRuntimeException e) {
				System.out.println("Cannot open Notes Session : " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return notesSession;
	}

	public Settings getSettings() {
		if (settings == null) {
			// try {
			// DNotesFactory factory =DNotesFactory.getInstance();
			// DSession session = factory.getSession();
			// DDatabase db = session.getDatabase(null, Constants.GOOCALSYNC_DB_FILE);
			// DView view = db.getView("ConfigurationView");
			// DDocument doc = view.getAllDocumentByKey(arg0, arg1);
			settings = new Settings();
			 settings.setGoogleAccountName("nnnn@gmail.com");
			 settings.setGooglePassword("****");
				        
			 // Notes File/Preferenses/Location Preferences.../Servers/'Home/mail server'
			 settings.setDominoServer("sever");
			 // Notes File/Preferenses/Location Preferences.../Mail/'Mail file'
			 settings.setMailDbFilePath("mail.nsf");
			
			 // Google calender name to sync with
			 settings.setCalendarName("Calendar");

			// settings.setSyncDirection( Constants.NOTES_TO_GOOGLE);
			settings.setSyncDirection(Constants.BI_DIRECTION);

			Calendar sdt = Calendar.getInstance();
			sdt.add(Calendar.DAY_OF_YEAR, -14);
			settings.setSyncStartDate(sdt);
			Calendar edt = Calendar.getInstance();
			edt.add(Calendar.MONTH, +3);
			settings.setSyncEndDate(edt);

			// convert lotus.domino.DateTime to xs:date format.
			// // DateTime stdt = (DateTime) doc.getItemValue("syncStartDate").get(0);
			// settings.setSyncStartDate(GooCalUtil.convNotesDateTimeToXS(stdt));

			// settings.setSyncDirection(GooCalUtil.convNull(doc.getItemValueString("SyncDirection")));
			// settings.setSyncDelete(GooCalUtil.convNull(doc.getItemValueString("SyncDelete")));
			// settings.setSyncPriority(GooCalUtil.convNull(doc.getItemValueString("SyncPriority")));

			// DateTime lastdt = (DateTime) doc.getItemValue("syncLastDateTime").get(0);
			// settings.setSyncLastDateTime(GooCalUtil.convNotesDateTimeToXS(lastdt));

			// Terada add for LOG BEGIN
			// settings.setLogLevel(doc.getItemValue("LogLevel"));
			// settings.setLogMode(GooCalUtil.convNull(doc.getItemValueString("LogMode")));
			// Terada add for LOG END

			// settings.setSetDoc(doc);

			// settings.setProxyHost(GooCalUtil.convNull(doc.getItemValueString("ProxyHost")));
			// settings.setProxyPort(GooCalUtil.convNull(doc.getItemValueString("ProxyPort")));
			// settings.setProxyUserName(GooCalUtil.convNull(doc.getItemValueString("ProxyUserName")));
			// settings.setProxyPassword(GooCalUtil.convNull(doc.getItemValueString("ProxyPassword")));

			// } catch (DNotesException e) {
			// e.printStackTrace();
			// GooCalUtil.logStackTrace(e);
			// System.exit(-1);
			// }
		}
		return settings;
	}

	public CalendarService getCalendarService() {

		if (calendarService == null) {
			Settings mySets = getSettings();
			// if (!mySets.getProxyHost().equals("") && !mySets.getProxyPort().equals("")) {
			// System.setProperty("http.proxyHost", mySets.getProxyHost());
			// System.setProperty("http.proxyPort", mySets.getProxyPort());
			// System.setProperty("https.proxyHost", mySets.getProxyHost());
			// System.setProperty("https.proxyPort", mySets.getProxyPort());
			// }
			// if (!mySets.getProxyUserName().equals("") && !mySets.getProxyPassword().equals("")) {
			// System.setProperty("http.proxyUserName", mySets.getProxyUserName());
			// System.setProperty("http.proxyPassword", mySets.getProxyPassword());
			// System.setProperty("https.proxyUserName", mySets.getProxyUserName());
			// System.setProperty("https.proxyPassword", mySets.getProxyPassword());
			// }
			calendarService = new CalendarService("SyncNotes2Google");
			try {
				calendarService.setUserCredentials(mySets.getGoogleAccountName(), mySets.getGooglePassword());
			} catch (AuthenticationException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return calendarService;
	}

	public DDatabase getMailDatabase() {

		if (mailDatabase == null) {
			try {
				mailDatabase = getNotesSession().getDatabase(getSettings().getDominoServer(), getSettings().getMailDbFilePath());
				if (mailDatabase.isOpen() == false) {
					mailDatabase.open();
				}
				return mailDatabase;
			} catch (DNotesException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return mailDatabase;

	}

}
