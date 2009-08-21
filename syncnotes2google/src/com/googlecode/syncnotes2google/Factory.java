package com.googlecode.syncnotes2google;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.util.AuthenticationException;
import com.googlecode.syncnotes2google.dao.BaseRecur;
import com.googlecode.syncnotes2google.dao.GoogleCalendarDAO;
import com.googlecode.syncnotes2google.dao.NotesCalendarDAO;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DNotesException;
import de.bea.domingo.DNotesFactory;
import de.bea.domingo.DNotesRuntimeException;
import de.bea.domingo.DSession;

public class Factory {

	private static Factory instance = null;
	private static DSession notesSession = null;
	private static Settings settings = null;
	private static CalendarService calendarService = null;
	private static GoogleCalendarDAO googleCalendarDAO = null;
	private static NotesCalendarDAO notesCalendarDAO = null;
	private static DDatabase gcsDatabase = null;
	private static DDatabase mailDatabase = null;

	// Terada add for LOG BEGIN
	private static Log log = null;

	// Terada add for LOG END

	private Factory() {
	}

	public static void freeNotesObject() {
		// try {
		if (gcsDatabase != null) {
			// gcsDatabase.recycle();
		}
		if (mailDatabase != null) {
			// mailDatabase.recycle();
		}
		if (notesSession != null) {
			// notesSession.recycle();
		}
		// } catch (DNotesException e) {
		// e.printStackTrace();
		// GooCalUtil.logStackTrace(e);
		// System.exit(-1);
		// }
	}

	public static Factory getInstance() {
		if (instance == null) {
			instance = new Factory();
		}
		return instance;
	}

	public static DSession getNotesSession() {
		if (notesSession == null) {
			try {
				// NotesThread.sinitThread();
				DNotesFactory factory = DNotesFactory.getInstance();
				notesSession = factory.getSession();
			} catch (DNotesRuntimeException e) {
				Factory.getLog().error("Cannot open Notes Session : " + e.getMessage());
				e.printStackTrace();
				GooCalUtil.logStackTrace(e);
				System.exit(-1);
			}
		}
		return notesSession;
	}

	public static Settings getSettings() {
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

			Calendar sdt = Calendar.getInstance();
			sdt.add(Calendar.DAY_OF_YEAR, -14);
			settings.setSyncStartDate(sdt);
			Calendar edt = Calendar.getInstance();
			edt.add(Calendar.DAY_OF_YEAR, 14);
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

	// Terada add for LOG BEGIN
	public static Log getLog() {
		if (log == null) {
			try {
				// DDatabase db = Factory.getGCSDatabase();
				// DView view = db.getView(Constants.GOOCALSYNC_LOG_VIEW);
				// DDocument doc = (DDocument)view.getAllDocuments().next();
				// if (doc == null) {
				// doc = db.createDocument();
				// doc.appendItemValue("Form", "Log");
				// doc.appendItemValue("Target","this");
				// doc.appendItemValue("LogMessage","");
				// doc.save(true);
				// }
				log = new Log();
			} catch (DNotesRuntimeException e) {
				e.printStackTrace();
				GooCalUtil.logStackTrace(e);
				System.exit(-1);
			}
		}
		return log;
	}

	// Terada add for LOG END

	public static CalendarService getCalendarService() {

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
			calendarService = new CalendarService("Notes2GooCalDontWork");
			try {
				calendarService.setUserCredentials(mySets.getGoogleAccountName(), mySets.getGooglePassword());
			} catch (AuthenticationException e) {
				Factory.getLog().error("Cannot connect to Google");
				Factory.getLog().error("    Account name       : " + mySets.getGoogleAccountName());
				Factory.getLog().error("    Account password   : " + mySets.getGooglePassword());
				Factory.getLog().error("    Message from Google: " + e.getMessage());
				e.printStackTrace();
				GooCalUtil.logStackTrace(e);
				System.exit(-1);
			}
		}
		return calendarService;
	}

	public static GoogleCalendarDAO getGoogleCalendarDAO() {
		if (googleCalendarDAO == null) {
			googleCalendarDAO = new GoogleCalendarDAO();
		}
		return googleCalendarDAO;
	}

	public static NotesCalendarDAO getNotesCalendarDAO() {
		if (notesCalendarDAO == null) {
			notesCalendarDAO = new NotesCalendarDAO();
		}
		return notesCalendarDAO;
	}

	// public static DDatabase getGCSDatabase () {
	//	
	// if (gcsDatabase == null) {
	// try {
	// gcsDatabase = getNotesSession().getDatabase("", Constants.GOOCALSYNC_DB_FILE);
	// if (gcsDatabase.isOpen() == false) {
	// gcsDatabase.open();
	// }
	// return gcsDatabase;
	// } catch (DNotesException e) {
	// Factory.getLog().error("Cannot open GooCalSync database :" + Constants.GOOCALSYNC_DB_FILE);
	// e.printStackTrace();
	// GooCalUtil.logStackTrace(e);
	// System.exit(-1);
	// }
	// }
	// return gcsDatabase;
	//	
	// }

	public static DDatabase getMailDatabase() {

		if (mailDatabase == null) {
			try {
				mailDatabase = getNotesSession().getDatabase(Factory.getSettings().getDominoServer(), Factory.getSettings().getMailDbFilePath());
				if (mailDatabase.isOpen() == false) {
					mailDatabase.open();
				}
				return mailDatabase;
			} catch (DNotesException e) {
				Factory.getLog().error("Cannot open mail database");
				Factory.getLog().error("     Domino server :" + Factory.getSettings().getDominoServer());
				Factory.getLog().error("     Mail Database :" + Factory.getSettings().getMailDbFilePath());
				e.printStackTrace();
				GooCalUtil.logStackTrace(e);
				System.exit(-1);
			}
		}
		return mailDatabase;

	}

}
