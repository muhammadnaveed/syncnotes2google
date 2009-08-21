package goocalsync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Vector;

import lotus.domino.Document;

public class Settings {

	private String GoogleAccountName;
	private String GooglePassword;
	private String calendarName;
	private String DominoServer;
	private String MailDbFilePath;
	private Calendar syncStartDate; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private Calendar syncEndDate;
	private String SyncDirection;
	private String SyncDelete;
	private String SyncPriority;
	private Calendar syncLastDateTime;
	private Document setDoc;
	private String ProxyHost;
	private String ProxyPort;
	private String ProxyUserName;
	private String ProxyPassword;

	private String LogMode;

	public Settings() {
		try {
			File file = new File("./LastSyncTime");
			syncLastDateTime = Calendar.getInstance();
			if (!file.exists()) {
				syncLastDateTime.setTimeInMillis(0);
				return;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = br.readLine();
			if (line != null) {
				syncLastDateTime.setTimeInMillis(Long.parseLong(line.trim()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getGoogleAccountName() {
		return GoogleAccountName;
	}

	public void setGoogleAccountName(String googleAccountName) {
		GoogleAccountName = googleAccountName;
	}

	public String getGooglePassword() {
		return GooglePassword;
	}

	public void setGooglePassword(String googlePassword) {
		GooglePassword = googlePassword;
	}

	public String getDominoServer() {
		return DominoServer;
	}

	public void setDominoServer(String dominoServer) {
		DominoServer = dominoServer;
	}

	public String getMailDbFilePath() {
		return MailDbFilePath;
	}

	public void setMailDbFilePath(String mailDbFilePath) {
		MailDbFilePath = mailDbFilePath;
	}

	public Calendar getSyncStartDate() {
		return syncStartDate;
	}

	public void setSyncStartDate(Calendar syncStartDate) {
		this.syncStartDate = syncStartDate;
	}

	public Calendar getSyncEndDate() {
		return syncEndDate;
	}

	public void setSyncEndDate(Calendar syncEndDate) {
		this.syncEndDate = syncEndDate;
	}

	public String getSyncDirection() {
		return Constants.BI_DIRECTION;
	}

	public void setSyncDirection(String syncDirection) {
		SyncDirection = syncDirection;
	}

	public String getSyncDelete() {
		return SyncDelete;
	}

	public void setSyncDelete(String syncDelete) {
		SyncDelete = syncDelete;
	}

	public String getSyncPriority() {
		return SyncPriority;
	}

	public Calendar getSyncLastDateTime() {
		return syncLastDateTime;
	}

	public void setSyncLastDateTime(Calendar syncLastDateTime) {
		this.syncLastDateTime = syncLastDateTime;
	}

	public void setSetDoc(Document doc) {
		this.setDoc = doc;
	}

	public Document getSetDoc() {
		return setDoc;
	}

	public void saveSetDoc() {
		// Todo save setings
		try {
			File file = new File("./LastSyncTime");
			FileWriter fw = new FileWriter(file);
			fw.write(System.currentTimeMillis() + "\n");
			fw.close();
		} catch (Exception e) {
		}
	}

	public Vector getLogLevel() {
		Vector v = new Vector();
		v.add(Log.ERROR);
		v.add(Log.WARN);
		v.add(Log.INFO);
		v.add(Log.DEBUG);
		return v;
	}

	public String getLogMode() {
		return LogMode;
	}

	public void setLogMode(String logMode) {
		LogMode = logMode;
	}

	public void setProxyHost(String proxyHost) {
		ProxyHost = proxyHost;
	}

	public String getProxyHost() {
		return ProxyHost;
	}

	public void setProxyPort(String proxyPort) {
		ProxyPort = proxyPort;
	}

	public String getProxyPort() {
		return ProxyPort;
	}

	public void setProxyUserName(String proxyUserName) {
		ProxyUserName = proxyUserName;
	}

	public String getProxyUserName() {
		return ProxyUserName;
	}

	public void setProxyPassword(String proxyPassword) {
		ProxyPassword = proxyPassword;
	}

	public String getProxyPassword() {
		return ProxyPassword;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

}
