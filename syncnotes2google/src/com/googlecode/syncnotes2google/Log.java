package com.googlecode.syncnotes2google;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.bea.domingo.DDocument;
import de.bea.domingo.DNotesException;
import de.bea.domingo.DNotesRuntimeException;

/**
 * Log class create this clss from Factory classbr />
 * and call log method to write log.
 * @author Junya
 *
 */
public class Log {

	public static final int ERROR = 1;// 0001
	public static final int WARN = 2;// 0010
	public static final int INFO = 4;// 0100
	public static final int DEBUG = 8;// 1000
	public static final boolean APPEND = true;
	public static final boolean OVERWRITE = false;

	// take into account afterward to add log date format on settings.
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

//	private DDocument logDoc;
	private String logMsg = ""; // Stores log messages(until save method)

	private boolean autoSave = true; // calls save() at log method
	
	private int logLevel;// INFO or ERROR or WARNING or DEBUG
	private boolean logMode = true;

	/**
	 * Constructor<br />
	 * Clear stored log messages<br />
	 * Set Auto Save mode off<br />
	 * Set Log Mode to ERROR and WARN.
	 */
	public Log() {
		
//		setLogDoc(logDoc);
		setLogMsg("");
		setAutoSave(true);
		Settings settings = Factory.getSettings();
		
		this.logLevel = 0;
		for( int i = 0; i < settings.getLogLevel().size(); i++) {
			this.logLevel = this.logLevel + (Integer)settings.getLogLevel().get(i);
		}
		
		if(true ) {
			this.logMode = APPEND;
		} else {
			this.logMode = OVERWRITE;
			save(); // clear existing message.
			this.logMode = APPEND;
		}
		
	}

	//Simple methods
	public void error(String msg) {
		log(ERROR, msg);
	}
	public void warn(String msg) {
		log(WARN, msg);
	}
	public void info(String msg) {
		log(INFO, msg);
	}
	public void debug(String msg) {
		log(DEBUG, msg);
	}

	//AutoSave is true then call save method.
	public void log(int mode, String msg) {
		if ((this.logLevel & mode) > 0) {
			if (logMsg.length() == 0) {
				logMsg = sdf.format(new Date()) + "\t" + getModeName(mode) + "\t" + msg;
			} else {
				logMsg = logMsg + "\r\n" + sdf.format(new Date()) + "\t" + getModeName(mode) + "\t" + msg;
			}
			if (isAutoSave()) {
				save();
			}
		}
	}
	
	//save method
	public boolean save() {
		boolean ret_flag = true;
		try {
			String values = "";
//			if(this.logMode == APPEND) { // If append mode, then load previous messages.
//				values = GooCalUtil.convNull(this.logDoc.getItemValueString("LogMessage"));
//			}
			
			if (values.length() == 0) {
				values = this.logMsg;
			} else {
				values = values + "\r\n" + this.logMsg;
			}
			System.out.println(this.logMsg);
//			this.logDoc.replaceItemValue("LogMessage", values);
//			logDoc.save(true);
			this.logMsg = "";
		} catch (DNotesRuntimeException e) {
			e.printStackTrace();
			ret_flag = false;
		}
		return ret_flag;
	}
	
	//For print
	public String getModeName(int mode) {
		switch (mode) {
		case ERROR:
			return "ERROR ";
		case WARN:
			return "WARN  ";
		case INFO:
			return "INFO  ";
		case DEBUG:
			return "DEBUG ";
		default:
			return "?"; // unknown
		}
	}

	// getter, setter part begin
//	public DDocument getLogDoc() {
//		return logDoc;
//	}
//	public void setLogDoc(DDocument logDoc) {
//?		this.logDoc = logDoc;
//	}
	public String getLogMsg() {
		return logMsg;
	}
	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}
	public boolean isAutoSave() {
		return autoSave;
	}
	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}	
	public int getLogMode() {
		return this.logLevel;
	}
	/**
	 * Log mode<br >
	 * setLogMode(Log.ERROR) - only error message will logged <br >
	 * setLogMode(Log.WARNING) - only warning message will logged this is default <br >
	 * setLogMode(Log.INFO) - only info message will logged <br >
	 * setLogMode(Log.DEBUG) - only debug message will logged <br >
	 * setLogMode(Log.ERROR | Log.WARNING) - error and warning message will logged <br >
	 * 
	 * @param logLevel
	 *            Log.ERROR | Log.WARNING | Log.INFO | Log.DEBUG
	 */
	public void setLogMode(int logLevel) {
		this.logLevel = logLevel;
	}
	public boolean isLogAppendMode() {
		return this.logMode;
	}
	public void setLogAppendMode(boolean logMode) {
		this.logMode = logMode;
	}
	// getter, setter part end

}