package com.googlecode.syncnotes2google.dao;

import java.text.DateFormat;
import java.util.Calendar;

import com.googlecode.syncnotes2google.Factory;
import com.googlecode.syncnotes2google.Log;

public class BaseDoc {

	private String id;
	private String title;
	private String content;
	private Calendar startDateTime; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private Calendar endDateTime; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private String location;
	private Calendar lastupdated; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private int apptype; // Appointment type
	private BaseRecur recur; // recurrence parameter
	private String refId;

	// This method is for debugging.
	public String toString() {
		StringBuffer l = new StringBuffer();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
		l.append("ID             : " + id + "\n");
		l.append("Title          : " + title + "\n");
		l.append("Content        : " + content + "\n");
		l.append("startDateTime  : " + df.format(startDateTime.getTime()) + "\n");
		l.append("endDateTime    : " + df.format(endDateTime.getTime()) + "\n");
		l.append("location       : " + location + "\n");
		l.append("lastupdated    : " + (lastupdated==null?"":df.format(lastupdated.getTime())) + "\n");
		l.append("apptype        : " + Integer.toString(apptype) + "\n");
		if (recur != null) {
			l.append("frequency      : " + Integer.toString(recur.getFrequency()) + "\n");
			l.append("interval       : " + Integer.toString(recur.getInterval()) + "\n");
			l.append("until          : " + recur.getUntil() + "\n");
			if (recur.getRdate() != null) {
				for (Calendar s : recur.getRdate()) {
					l.append("rdate          : " + s + "\n");
				}
			}
		}
		return l.toString();
	}

	public void printError() {

		Log l = Factory.getLog();

		l.error("ID             : " + id);
		l.error("Title          : " + title);
		l.error("Content        : " + content);
		l.error("startDateTime  : " + startDateTime);
		l.error("endDateTime    : " + endDateTime);
		l.error("location       : " + location);
		l.error("lastupdated    : " + lastupdated);
		l.error("apptype        : " + Integer.toString(apptype));
		if (recur != null) {
			l.error("frequency      : " + Integer.toString(recur.getFrequency()));
			l.error("interval       : " + Integer.toString(recur.getInterval()));
			l.error("until          : " + recur.getUntil());
			if (recur.getRdate() != null) {
				for (Calendar s : recur.getRdate()) {
					l.error("rdate          : " + s);
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Calendar startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Calendar getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Calendar endDateTime) {
		this.endDateTime = endDateTime;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLastUpdated(Calendar lastupdated) {
		this.lastupdated = lastupdated;
	}

	public Calendar getLastUpdated() {
		return lastupdated;
	}

	public void setApptype(int apptype) {
		this.apptype = apptype;
	}

	public int getApptype() {
		return apptype;
	}

	public void setRecur(BaseRecur recur) {
		this.recur = recur;
	}

	public BaseRecur getRecur() {
		return recur;
	}

	public String getRefId() {
		return refId;
	}
	
	public void setRefId(String refId) {
		this.refId = refId;
	}
}
