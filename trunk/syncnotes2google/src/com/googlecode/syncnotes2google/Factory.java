package com.googlecode.syncnotes2google;

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
				DNotesFactory factory = DNotesFactory.getInstance();
				notesSession = factory.getSession();
			} catch (DNotesRuntimeException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return notesSession;
	}

	public Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
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
